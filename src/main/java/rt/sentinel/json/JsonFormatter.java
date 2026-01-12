package rt.sentinel.json;

/**
 * JSON格式化器
 * 
 * 用于控制JSON输出的格式化选项，如缩进
 * 
 * @author SentinelRT
 */
public class JsonFormatter {
    
    /** 缩进字符串缓存 */
    private final String[] indentCache;
    
    /** 单级缩进字符串 */
    private final String singleIndent;
    
    /** 最大缓存级别 */
    private static final int MAX_CACHE_LEVEL = 20;
    
    /**
     * 创建格式化器
     * 
     * @param indentSize 每级缩进的空格数
     */
    public JsonFormatter(int indentSize) {
        // 构建单级缩进字符串
        StringBuilder sb = new StringBuilder(indentSize);
        for (int i = 0; i < indentSize; i++) {
            sb.append(' ');
        }
        this.singleIndent = sb.toString();
        
        // 预生成缩进缓存
        this.indentCache = new String[MAX_CACHE_LEVEL];
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < MAX_CACHE_LEVEL; i++) {
            indentCache[i] = indent.toString();
            indent.append(singleIndent);
        }
    }
    
    /**
     * 使用默认缩进大小（2个空格）创建格式化器
     */
    public JsonFormatter() {
        this(2);
    }
    
    /**
     * 获取指定级别的缩进字符串
     * 
     * @param level 缩进级别
     * @return 缩进字符串
     */
    public String getIndent(int level) {
        if (level < 0) {
            return "";
        }
        if (level < MAX_CACHE_LEVEL) {
            return indentCache[level];
        }
        
        // 超出缓存范围，动态生成
        StringBuilder sb = new StringBuilder(level * singleIndent.length());
        for (int i = 0; i < level; i++) {
            sb.append(singleIndent);
        }
        return sb.toString();
    }
    
    /**
     * 获取单级缩进字符串
     * 
     * @return 单级缩进字符串
     */
    public String getSingleIndent() {
        return singleIndent;
    }
}
