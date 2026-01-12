package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.TypeUtils;

import java.lang.reflect.Array;

/**
 * 数组类型处理器
 * 
 * 处理所有数组类型：
 * - 基本类型数组（int[], byte[], char[]等）
 * - 对象数组（Object[], String[]等）
 * 
 * 特殊处理：byte[] 输出为十六进制字符串
 * 
 * @author SentinelRT
 */
public class ArrayHandler implements TypeHandler {
    
    /** 十六进制字符表 */
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    
    /** 类型处理器注册表 */
    private final TypeHandlerRegistry registry;
    
    /**
     * 创建数组处理器
     * 
     * @param registry 类型处理器注册表（用于处理数组元素）
     */
    public ArrayHandler(TypeHandlerRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        return TypeUtils.isArray(clazz);
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
            return;
        }
        
        Class<?> componentType = obj.getClass().getComponentType();
        
        // 特殊处理 byte[] - 输出为十六进制字符串
        if (componentType == byte.class) {
            builder.value(bytesToHex((byte[]) obj));
            return;
        }
        
        int length = Array.getLength(obj);
        int limit = context.getConfig().getCollectionLimit();
        int outputLength = Math.min(length, limit);
        
        builder.beginArray();
        
        for (int i = 0; i < outputLength; i++) {
            Object element = Array.get(obj, i);
            serializeElement(element, builder, context);
        }
        
        // 如果数组被截断，添加提示
        if (length > limit) {
            builder.value("[... " + (length - limit) + " more elements truncated]");
        }
        
        builder.endArray();
    }
    
    /**
     * 序列化数组元素
     */
    private void serializeElement(Object element, JsonBuilder builder, SerializationContext context) {
        if (element == null) {
            builder.nullValue();
            return;
        }
        
        TypeHandler handler = registry.findHandler(element.getClass());
        if (handler != null) {
            handler.handle(element, builder, context);
        } else {
            builder.value(element.toString());
        }
    }
    
    /**
     * 将byte数组转换为十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串（小写）
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    @Override
    public int getPriority() {
        return 50;
    }
}
