package rt.sentinel.core;

import rt.sentinel.handler.TypeHandler;
import rt.sentinel.handler.TypeHandlerRegistry;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.json.JsonFormatter;

/**
 * 对象序列化引擎
 * 
 * 核心序列化类，将任意Java对象转换为JSON字符串
 * 
 * 使用示例：
 * <pre>
 * ObjectSerializer serializer = new ObjectSerializer();
 * String json = serializer.serialize(myObject);
 * </pre>
 * 
 * @author SentinelRT
 */
public class ObjectSerializer {
    
    /** 类型处理器注册表 */
    private final TypeHandlerRegistry registry;
    
    /** 默认配置 */
    private SerializerConfig defaultConfig;
    
    /**
     * 创建序列化器（使用默认处理器）
     */
    public ObjectSerializer() {
        this.registry = new TypeHandlerRegistry().initDefaults();
        this.defaultConfig = new SerializerConfig();
    }
    
    /**
     * 创建序列化器（使用自定义处理器注册表）
     * 
     * @param registry 自定义类型处理器注册表
     */
    public ObjectSerializer(TypeHandlerRegistry registry) {
        this.registry = registry;
        this.defaultConfig = new SerializerConfig();
    }
    
    /**
     * 使用默认配置序列化对象
     * 
     * @param obj 要序列化的对象
     * @return JSON字符串
     */
    public String serialize(Object obj) {
        return serialize(obj, defaultConfig);
    }
    
    /**
     * 使用指定深度序列化对象
     * 
     * @param obj 要序列化的对象
     * @param maxDepth 最大递归深度
     * @return JSON字符串
     */
    public String serialize(Object obj, int maxDepth) {
        SerializerConfig config = defaultConfig.copy();
        config.setMaxDepth(maxDepth);
        return serialize(obj, config);
    }
    
    /**
     * 使用指定深度和格式化选项序列化对象
     * 
     * @param obj 要序列化的对象
     * @param maxDepth 最大递归深度
     * @param pretty 是否美化输出
     * @return JSON字符串
     */
    public String serialize(Object obj, int maxDepth, boolean pretty) {
        SerializerConfig config = defaultConfig.copy();
        config.setMaxDepth(maxDepth);
        config.setPretty(pretty);
        return serialize(obj, config);
    }
    
    /**
     * 使用指定配置序列化对象
     * 
     * @param obj 要序列化的对象
     * @param config 序列化配置
     * @return JSON字符串
     */
    public String serialize(Object obj, SerializerConfig config) {
        if (config == null) {
            config = defaultConfig;
        }
        
        // 创建JSON构建器
        JsonFormatter formatter = config.isPretty() 
            ? new JsonFormatter(config.getIndentSize()) 
            : null;
        JsonBuilder builder = new JsonBuilder(formatter);
        
        // 创建序列化上下文
        SerializationContext context = new SerializationContext(config);
        
        // 处理null
        if (obj == null) {
            return "null";
        }
        
        // 查找并使用处理器
        TypeHandler handler = registry.findHandler(obj.getClass());
        if (handler != null) {
            handler.handle(obj, builder, context);
        } else {
            // 无处理器，返回toString
            builder.value(obj.toString());
        }
        
        return builder.toString();
    }
    
    /**
     * 设置默认配置
     * 
     * @param config 默认配置
     */
    public void setDefaultConfig(SerializerConfig config) {
        this.defaultConfig = config != null ? config : new SerializerConfig();
    }
    
    /**
     * 获取默认配置
     * 
     * @return 默认配置
     */
    public SerializerConfig getDefaultConfig() {
        return defaultConfig;
    }
    
    /**
     * 获取类型处理器注册表
     * 
     * @return 类型处理器注册表
     */
    public TypeHandlerRegistry getRegistry() {
        return registry;
    }
    
    /**
     * 注册自定义类型处理器
     * 
     * @param handler 类型处理器
     */
    public void registerHandler(TypeHandler handler) {
        registry.register(handler);
    }
}
