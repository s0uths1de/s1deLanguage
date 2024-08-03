package top.s0uths1de.jpp;

public enum TokenType {
    KEYWORD,    // 关键字
    IDENTIFIER, // 标识符
    NUMBER,     // 数字
    FLOAT,      // 浮点数
    OPERATOR,   // 操作符
    WHITESPACE, // 空白
    SEPARATOR,  // 分节符
    STRING,     // 字符串
    CHARACTER,  // 字符
    COMMENT,    // 注释
    BOOLEAN,    // 布尔值
    NEWLINE,    // 换行符
    SPECIAL,    // 特殊符号
    JAVADOC,    // 文档注释
    TEMPLATE,   // 模板字面值（反引号）
    MULTILINE_STRING, // 多行字符串
    EOF,        // 文件结束
    UNKNOWN     // 未知类型
}
