package rt.sentinel.json;

/**
 * JSON字符串构建器
 * 
 * 提供构建JSON字符串的低级方法，支持：
 * - 对象（{}）
 * - 数组（[]）
 * - 键值对
 * - 各种类型的值
 * - 字符串转义
 * 
 * @author SentinelRT
 */
public class JsonBuilder {
    
    /** 内部字符串构建器 */
    private final StringBuilder sb;
    
    /** 格式化器（可选） */
    private final JsonFormatter formatter;
    
    /** 当前缩进级别 */
    private int indentLevel;
    
    /** 是否需要逗号分隔符 */
    private boolean needsComma;
    
    /**
     * 创建JSON构建器（紧凑模式）
     */
    public JsonBuilder() {
        this(null);
    }
    
    /**
     * 创建JSON构建器
     * 
     * @param formatter JSON格式化器，null表示紧凑模式
     */
    public JsonBuilder(JsonFormatter formatter) {
        this.sb = new StringBuilder();
        this.formatter = formatter;
        this.indentLevel = 0;
        this.needsComma = false;
    }
    
    /**
     * 开始一个JSON对象
     * 
     * @return this
     */
    public JsonBuilder beginObject() {
        appendCommaIfNeeded();
        sb.append('{');
        indentLevel++;
        needsComma = false;
        appendNewlineIfPretty();
        return this;
    }
    
    /**
     * 结束当前JSON对象
     * 
     * @return this
     */
    public JsonBuilder endObject() {
        indentLevel--;
        appendNewlineIfPretty();
        appendIndentIfPretty();
        sb.append('}');
        needsComma = true;
        return this;
    }
    
    /**
     * 开始一个JSON数组
     * 
     * @return this
     */
    public JsonBuilder beginArray() {
        appendCommaIfNeeded();
        sb.append('[');
        indentLevel++;
        needsComma = false;
        appendNewlineIfPretty();
        return this;
    }
    
    /**
     * 结束当前JSON数组
     * 
     * @return this
     */
    public JsonBuilder endArray() {
        indentLevel--;
        appendNewlineIfPretty();
        appendIndentIfPretty();
        sb.append(']');
        needsComma = true;
        return this;
    }
    
    /**
     * 添加键名（用于对象）
     * 
     * @param key 键名
     * @return this
     */
    public JsonBuilder key(String key) {
        appendCommaIfNeeded();
        appendIndentIfPretty();
        sb.append('"').append(escapeString(key)).append('"');
        sb.append(':');
        if (formatter != null) {
            sb.append(' ');
        }
        needsComma = false;
        return this;
    }
    
    /**
     * 添加null值
     * 
     * @return this
     */
    public JsonBuilder nullValue() {
        appendCommaIfNeeded();
        if (needsComma) {
            appendIndentIfPretty();
        }
        sb.append("null");
        needsComma = true;
        return this;
    }
    
    /**
     * 添加布尔值
     * 
     * @param value 布尔值
     * @return this
     */
    public JsonBuilder value(boolean value) {
        appendCommaIfNeeded();
        if (needsComma) {
            appendIndentIfPretty();
        }
        sb.append(value);
        needsComma = true;
        return this;
    }
    
    /**
     * 添加整数值
     * 
     * @param value 整数值
     * @return this
     */
    public JsonBuilder value(long value) {
        appendCommaIfNeeded();
        if (needsComma) {
            appendIndentIfPretty();
        }
        sb.append(value);
        needsComma = true;
        return this;
    }
    
    /**
     * 添加浮点数值
     * 处理特殊值：NaN, Infinity, -Infinity 输出为字符串
     * 
     * @param value 浮点数值
     * @return this
     */
    public JsonBuilder value(double value) {
        appendCommaIfNeeded();
        if (needsComma) {
            appendIndentIfPretty();
        }
        
        // 处理特殊值
        if (Double.isNaN(value)) {
            sb.append("\"NaN\"");
        } else if (Double.isInfinite(value)) {
            sb.append(value > 0 ? "\"Infinity\"" : "\"-Infinity\"");
        } else {
            sb.append(value);
        }
        needsComma = true;
        return this;
    }
    
    /**
     * 添加字符串值
     * 
     * @param value 字符串值
     * @return this
     */
    public JsonBuilder value(String value) {
        appendCommaIfNeeded();
        if (needsComma) {
            appendIndentIfPretty();
        }
        
        if (value == null) {
            sb.append("null");
        } else {
            sb.append('"').append(escapeString(value)).append('"');
        }
        needsComma = true;
        return this;
    }
    
    /**
     * 添加原始JSON字符串（不转义）
     * 用于嵌入已经格式化好的JSON
     * 
     * @param rawJson 原始JSON字符串
     * @return this
     */
    public JsonBuilder rawValue(String rawJson) {
        appendCommaIfNeeded();
        if (needsComma) {
            appendIndentIfPretty();
        }
        sb.append(rawJson);
        needsComma = true;
        return this;
    }
    
    /**
     * 转义JSON字符串
     * 
     * @param s 原始字符串
     * @return 转义后的字符串
     */
    public static String escapeString(String s) {
        if (s == null) {
            return "";
        }
        
        StringBuilder result = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        // 控制字符使用Unicode转义
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
            }
        }
        return result.toString();
    }
    
    /**
     * 如果需要，添加逗号分隔符
     */
    private void appendCommaIfNeeded() {
        if (needsComma) {
            sb.append(',');
            appendNewlineIfPretty();
        }
    }
    
    /**
     * 如果是美化模式，添加换行符
     */
    private void appendNewlineIfPretty() {
        if (formatter != null) {
            sb.append('\n');
        }
    }
    
    /**
     * 如果是美化模式，添加缩进
     */
    private void appendIndentIfPretty() {
        if (formatter != null) {
            sb.append(formatter.getIndent(indentLevel));
        }
    }
    
    /**
     * 获取构建的JSON字符串
     * 
     * @return JSON字符串
     */
    @Override
    public String toString() {
        return sb.toString();
    }
    
    /**
     * 重置构建器
     */
    public void reset() {
        sb.setLength(0);
        indentLevel = 0;
        needsComma = false;
    }
    
    /**
     * 获取当前长度
     * 
     * @return 当前构建的字符串长度
     */
    public int length() {
        return sb.length();
    }
}
