package top.s0uths1de.jpp;

import java.util.*;

/**
 * 词法分析器类，用于将输入字符串分解为标记（Token）。
 */
public class Lexer {
    private final String input; // 输入字符串
    private int position;       // 当前字符的位置
    private char currentChar;   // 当前字符
    private List<Token> tokens; // 用于存储标记的列表

    // 定义关键字集合
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList("if", "else", "while", "for", "true", "false"));
    // 定义操作符集合
    private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList("+", "-", "*", "/", "=", "<", ">", "++", "--", "==", "<=", ">=", "!="));
    // 定义分节符集合，包括数组标识符
    private static final Set<String> SEPARATORS = new HashSet<>(Arrays.asList(";", ",", "(", ")", "{", "}", "[", "]"));
    // 定义特殊符号集合
    private static final Set<String> SPECIAL_SYMBOLS = new HashSet<>(Arrays.asList("@", "#", "$"));

    /**
     * 构造函数，初始化词法分析器。
     *
     * @param input 输入的字符串
     */
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.currentChar = !input.isEmpty() ? input.charAt(0) : '\0'; // 初始化当前字符
        this.tokens = new ArrayList<>(); // 初始化标记列表
    }

    /**
     * 前进一个字符。
     */
    private void advance() {
        position++;
        if (position < input.length()) {
            currentChar = input.charAt(position);
        } else {
            currentChar = '\0'; // 文件结束
        }
    }

    /**
     * 查看下一个字符。
     *
     * @return 下一个字符
     */
    private char peek() {
        if (position + 1 < input.length()) {
            return input.charAt(position + 1);
        } else {
            return '\0';
        }
    }

    /**
     * 跳过空白字符（但不包括换行符）。
     */
    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar) && currentChar != '\n') {
            advance();
        }
    }

    /**
     * 跳过注释，包括单行注释、多行注释和文档注释。
     */
    private void skipComment() {
        if (currentChar == '/' && peek() == '/') {
            // 单行注释
            while (currentChar != '\n' && currentChar != '\0') {
                advance();
            }
        } else if (currentChar == '/' && peek() == '*') {
            advance(); // 跳过 '*'
            advance(); // 跳过 '/'
            if (currentChar == '*' && peek() == '*') {
                // 文档注释
                advance(); // 跳过 '*'
                StringBuilder comment = new StringBuilder("/**");
                while (currentChar != '\0') {
                    comment.append(currentChar);
                    if (currentChar == '*' && peek() == '/') {
                        advance();
                        comment.append(currentChar);
                        break;
                    }
                    advance();
                }
                advance(); // 跳过最后的 '/'
                tokens.add(new Token(TokenType.JAVADOC, comment.toString()));
            } else {
                // 多行注释
                while (currentChar != '\0') {
                    if (currentChar == '*' && peek() == '/') {
                        advance();
                        advance();
                        break;
                    }
                    advance();
                }
            }
        }
    }

    /**
     * 收集标识符。
     *
     * @return 收集到的标识符
     */
    private String collectIdentifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && Character.isLetterOrDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    /**
     * 收集数字（包括浮点数）。
     *
     * @return 收集到的数字
     */
    private String collectNumber() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isDigit(currentChar) || currentChar == '.')) {
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    /**
     * 收集字符串字面值（双引号）。
     *
     * @return 收集到的字符串
     */
    private String collectString() {
        StringBuilder result = new StringBuilder();
        advance(); // 跳过起始引号
        while (currentChar != '\0' && currentChar != '"') {
            result.append(currentChar);
            advance();
        }
        advance(); // 跳过结束引号
        return result.toString();
    }

    /**
     * 收集单个字符字面值（单引号）。
     *
     * @return 收集到的字符
     */
    private String collectCharacter() {
        StringBuilder result = new StringBuilder();
        advance(); // 跳过起始单引号
        if (currentChar != '\0' && currentChar != '\'') {
            result.append(currentChar);
            advance();
        }
        advance(); // 跳过结束单引号
        return result.toString();
    }

    /**
     * 收集多行字符串字面值（三引号）。
     *
     * @return 收集到的多行字符串
     */
    private String collectMultilineString() {
        StringBuilder result = new StringBuilder();
        advance(); // 跳过第一个三引号
        advance(); // 跳过第二个三引号
        advance(); // 跳过第三个三引号
        while (currentChar != '\0') {
            if (currentChar == '"' && peek() == '"' && peekNext() == '"') {
                advance(); // 跳过第一个引号
                advance(); // 跳过第二个引号
                advance(); // 跳过第三个引号
                break;
            }
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    /**
     * 查看接下来的两个字符。
     *
     * @return 接下来的两个字符
     */
    private char peekNext() {
        if (position + 2 < input.length()) {
            return input.charAt(position + 2);
        } else {
            return '\0';
        }
    }
    /**
     * 词法分析，将输入字符串分解为标记。
     *
     * @return 标记列表
     */
    public List<Token> tokenize() {
        tokens = new ArrayList<>();
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar) && currentChar != '\n') {
                skipWhitespace();
            } else if (currentChar == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\\n"));
                advance();
            } else if (Character.isLetter(currentChar)) {
                String value = collectIdentifier();
                TokenType type = KEYWORDS.contains(value) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
                tokens.add(new Token(type, value));
            } else if (Character.isDigit(currentChar) || (currentChar == '.' && Character.isDigit(peek()))) {
                String value = collectNumber();
                TokenType type = value.contains(".") ? TokenType.FLOAT : TokenType.NUMBER;
                tokens.add(new Token(type, value));
            } else if (currentChar == '"') {
                String value = collectString();
                tokens.add(new Token(TokenType.STRING, value));
            } else if (currentChar == '\'') {
                String value = collectCharacter();
                tokens.add(new Token(TokenType.CHARACTER, value));
            } else if (currentChar == '"' && peek() == '"' && peekNext() == '"') {
                String value = collectMultilineString();
                tokens.add(new Token(TokenType.MULTILINE_STRING, value));
            } else if (currentChar == '/' && (peek() == '/' || peek() == '*')) {
                skipComment();
            } else {
                String op = Character.toString(currentChar);
                if (SEPARATORS.contains(op)) {
                    tokens.add(new Token(TokenType.SEPARATOR, op));
                    advance();
                } else if (SPECIAL_SYMBOLS.contains(op)) {
                    tokens.add(new Token(TokenType.SPECIAL, op));
                    advance();
                } else {
                    char nextChar = peek();
                    if (OPERATORS.contains(op + nextChar)) {
                        op += nextChar;
                        advance();
                    }
                    if (OPERATORS.contains(op)) {
                        tokens.add(new Token(TokenType.OPERATOR, op));
                    } else {
                        tokens.add(new Token(TokenType.UNKNOWN, Character.toString(currentChar)));
                    }
                    advance();
                }
            }
        }

        tokens.add(new Token(TokenType.EOF, "EOF")); // 添加文件结束标记
        return tokens;
    }

    /**
     * 主方法，用于测试词法分析器。
     */
    public static void main(String[] args) {
        String input = """
                /**
                 * 主函数入口点。
                 * 
                 * @param args 命令行参数
                 */
                int main() {
                    // 定义整数变量
                    int a = 10; // 单行注释
                    float b = 20.5;
                    
                    // 字符字面值
                    char c = 'A';
                    
                    // 字符串字面值
                    String str = "Hello, World!";
                    
                    // 多行字符串字面值
                    String multilineStr = \"\"\"
                        This is a
                        multi-line stringc  
                        with multiple lines.
                    \"\"\";
                    
                    // 条件语句
                    if (a < b) {
                        a++;
                    } else {
                        a--;
                    }
                    
                    // 操作符测试
                    int result = a + b * 2 / (1 - 3) == 10;
                    
                    // 特殊符号和分隔符测试
                    @SpecialSymbol
                    return result;
                }
                """;

        long l = System.currentTimeMillis();

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        long l1 = System.currentTimeMillis();

        for (Token token : tokens) {
            System.out.println(token);
        }

        long l2 = System.currentTimeMillis();

        System.out.println(l1-l);
        System.out.println(l2-l1);
        System.out.println(l2-l);
    }
}
