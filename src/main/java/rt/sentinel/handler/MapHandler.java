package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.TypeUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Map类型处理器
 * 
 * 处理所有Map接口的实现类：
 * - HashMap, LinkedHashMap, TreeMap等
 * - ConcurrentHashMap等
 * 
 * 输出为JSON对象格式
 * 键会被转换为字符串
 * 
 * @author SentinelRT
 */
public class MapHandler implements TypeHandler {
    
    /** 类型处理器注册表 */
    private final TypeHandlerRegistry registry;
    
    /**
     * 创建Map处理器
     * 
     * @param registry 类型处理器注册表（用于处理Map值）
     */
    public MapHandler(TypeHandlerRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        return TypeUtils.isMap(clazz);
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
            return;
        }
        
        Map<?, ?> map = (Map<?, ?>) obj;
        int size = map.size();
        int limit = context.getConfig().getCollectionLimit();
        
        builder.beginObject();
        
        Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        int count = 0;
        
        while (iterator.hasNext() && count < limit) {
            Map.Entry<?, ?> entry = iterator.next();
            
            // 键转为字符串
            String key = entry.getKey() == null ? "null" : entry.getKey().toString();
            builder.key(key);
            
            // 序列化值
            serializeValue(entry.getValue(), builder, context);
            count++;
        }
        
        // 如果Map被截断，添加提示
        if (size > limit) {
            builder.key("__truncated__");
            builder.value(size - limit + " more entries");
        }
        
        builder.endObject();
    }
    
    /**
     * 序列化Map值
     */
    private void serializeValue(Object value, JsonBuilder builder, SerializationContext context) {
        if (value == null) {
            builder.nullValue();
            return;
        }
        
        TypeHandler handler = registry.findHandler(value.getClass());
        if (handler != null) {
            handler.handle(value, builder, context);
        } else {
            builder.value(value.toString());
        }
    }
    
    @Override
    public int getPriority() {
        return 70;
    }
}
