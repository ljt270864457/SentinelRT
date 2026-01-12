package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.TypeUtils;

/**
 * 字符串处理器
 * 
 * 处理String类型，自动进行JSON转义
 * 
 * @author SentinelRT
 */
public class StringHandler implements TypeHandler {
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        return TypeUtils.isString(clazz);
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
        } else {
            builder.value((String) obj);
        }
    }
    
    @Override
    public int getPriority() {
        return 20;
    }
}
