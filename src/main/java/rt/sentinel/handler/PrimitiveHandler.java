package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.TypeUtils;

/**
 * 基本类型处理器
 * 
 * 处理Java基本类型及其包装类：
 * - boolean/Boolean
 * - byte/Byte
 * - short/Short
 * - char/Character
 * - int/Integer
 * - long/Long
 * - float/Float
 * - double/Double
 * 
 * @author SentinelRT
 */
public class PrimitiveHandler implements TypeHandler {
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        return TypeUtils.isPrimitive(clazz);
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
            return;
        }
        
        Class<?> clazz = obj.getClass();
        
        // 布尔类型
        if (clazz == Boolean.class || clazz == boolean.class) {
            builder.value((Boolean) obj);
            return;
        }
        
        // 字符类型 - 输出为单字符字符串
        if (clazz == Character.class || clazz == char.class) {
            builder.value(String.valueOf(obj));
            return;
        }
        
        // 整数类型
        if (clazz == Byte.class || clazz == byte.class ||
            clazz == Short.class || clazz == short.class ||
            clazz == Integer.class || clazz == int.class ||
            clazz == Long.class || clazz == long.class) {
            builder.value(((Number) obj).longValue());
            return;
        }
        
        // 浮点类型
        if (clazz == Float.class || clazz == float.class ||
            clazz == Double.class || clazz == double.class) {
            builder.value(((Number) obj).doubleValue());
            return;
        }
        
        // 其他Number子类
        if (obj instanceof Number) {
            Number num = (Number) obj;
            // 判断是否为整数
            if (num.doubleValue() == num.longValue()) {
                builder.value(num.longValue());
            } else {
                builder.value(num.doubleValue());
            }
            return;
        }
        
        // fallback: 转为字符串
        builder.value(obj.toString());
    }
    
    @Override
    public int getPriority() {
        // 最高优先级
        return 10;
    }
}
