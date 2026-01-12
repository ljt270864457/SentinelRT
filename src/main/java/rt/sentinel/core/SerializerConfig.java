package rt.sentinel.core;

/**
 * 序列化配置类
 * 
 * 用于控制对象序列化的各种行为参数
 * 
 * @author SentinelRT
 */
public class SerializerConfig {
    
    /** 默认最大递归深度 */
    public static final int DEFAULT_MAX_DEPTH = 5;
    
    /** 默认集合元素限制 */
    public static final int DEFAULT_COLLECTION_LIMIT = 10;
    
    /** 默认缩进空格数 */
    public static final int DEFAULT_INDENT_SIZE = 2;
    
    /** 最大递归深度，用于防止循环引用导致的栈溢出 */
    private int maxDepth;
    
    /** 是否美化输出（带缩进和换行） */
    private boolean pretty;
    
    /** 集合（List/Set/Map/Array）的最大元素输出数量 */
    private int collectionLimit;
    
    /** 是否包含类型元信息（@type, @class等） */
    private boolean includeMeta;
    
    /** 是否包含方法信息（@methods） */
    private boolean includeMethods;
    
    /** 缩进空格数（仅在pretty=true时有效） */
    private int indentSize;
    
    /**
     * 创建默认配置
     */
    public SerializerConfig() {
        this.maxDepth = DEFAULT_MAX_DEPTH;
        this.pretty = false;
        this.collectionLimit = DEFAULT_COLLECTION_LIMIT;
        this.includeMeta = true;
        this.includeMethods = false;  // 默认不输出方法信息
        this.indentSize = DEFAULT_INDENT_SIZE;
    }
    
    /**
     * 从JSON字符串解析配置
     * 
     * @param json 配置JSON字符串，格式如：
     *             {"maxDepth":5,"pretty":true,"collectionLimit":50,"includeMeta":true,"includeMethods":true}
     * @return 解析后的配置对象
     */
    public static SerializerConfig fromJson(String json) {
        SerializerConfig config = new SerializerConfig();
        if (json == null || json.trim().isEmpty()) {
            return config;
        }
        
        // 简单的JSON解析（不依赖第三方库）
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            
            // 解析各个字段
            config.maxDepth = parseIntField(json, "maxDepth", DEFAULT_MAX_DEPTH);
            config.pretty = parseBooleanField(json, "pretty", false);
            config.collectionLimit = parseIntField(json, "collectionLimit", DEFAULT_COLLECTION_LIMIT);
            config.includeMeta = parseBooleanField(json, "includeMeta", true);
            config.includeMethods = parseBooleanField(json, "includeMethods", true);
            config.indentSize = parseIntField(json, "indentSize", DEFAULT_INDENT_SIZE);
        }
        
        return config;
    }
    
    /**
     * 从JSON中解析整数字段
     */
    private static int parseIntField(String json, String fieldName, int defaultValue) {
        String pattern = "\"" + fieldName + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) {
            return defaultValue;
        }
        
        int colonIdx = json.indexOf(':', idx + pattern.length());
        if (colonIdx == -1) {
            return defaultValue;
        }
        
        int endIdx = findValueEnd(json, colonIdx + 1);
        String value = json.substring(colonIdx + 1, endIdx).trim();
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 从JSON中解析布尔字段
     */
    private static boolean parseBooleanField(String json, String fieldName, boolean defaultValue) {
        String pattern = "\"" + fieldName + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) {
            return defaultValue;
        }
        
        int colonIdx = json.indexOf(':', idx + pattern.length());
        if (colonIdx == -1) {
            return defaultValue;
        }
        
        int endIdx = findValueEnd(json, colonIdx + 1);
        String value = json.substring(colonIdx + 1, endIdx).trim().toLowerCase();
        
        return "true".equals(value);
    }
    
    /**
     * 查找JSON值的结束位置
     */
    private static int findValueEnd(String json, int start) {
        int commaIdx = json.indexOf(',', start);
        int braceIdx = json.indexOf('}', start);
        
        if (commaIdx == -1 && braceIdx == -1) {
            return json.length();
        } else if (commaIdx == -1) {
            return braceIdx;
        } else if (braceIdx == -1) {
            return commaIdx;
        } else {
            return Math.min(commaIdx, braceIdx);
        }
    }
    
    // ==================== Getters and Setters ====================
    
    public int getMaxDepth() {
        return maxDepth;
    }
    
    public SerializerConfig setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }
    
    public boolean isPretty() {
        return pretty;
    }
    
    public SerializerConfig setPretty(boolean pretty) {
        this.pretty = pretty;
        return this;
    }
    
    public int getCollectionLimit() {
        return collectionLimit;
    }
    
    public SerializerConfig setCollectionLimit(int collectionLimit) {
        this.collectionLimit = collectionLimit;
        return this;
    }
    
    public boolean isIncludeMeta() {
        return includeMeta;
    }
    
    public SerializerConfig setIncludeMeta(boolean includeMeta) {
        this.includeMeta = includeMeta;
        return this;
    }
    
    public boolean isIncludeMethods() {
        return includeMethods;
    }
    
    public SerializerConfig setIncludeMethods(boolean includeMethods) {
        this.includeMethods = includeMethods;
        return this;
    }
    
    public int getIndentSize() {
        return indentSize;
    }
    
    public SerializerConfig setIndentSize(int indentSize) {
        this.indentSize = indentSize;
        return this;
    }
    
    /**
     * 创建配置的副本
     */
    public SerializerConfig copy() {
        SerializerConfig copy = new SerializerConfig();
        copy.maxDepth = this.maxDepth;
        copy.pretty = this.pretty;
        copy.collectionLimit = this.collectionLimit;
        copy.includeMeta = this.includeMeta;
        copy.includeMethods = this.includeMethods;
        copy.indentSize = this.indentSize;
        return copy;
    }
}
