package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.TypeUtils;

/**
 * 枚举类型处理器
 * 
 * 将枚举值输出为其名称字符串
 * 
 * @author SentinelRT
 */
public class EnumHandler implements TypeHandler {
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        return TypeUtils.isEnum(clazz);
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
        } else {
            // 输出枚举名称
            builder.value(((Enum<?>) obj).name());
        }
    }
    
    @Override
    public int getPriority() {
        return 30;
    }
}
