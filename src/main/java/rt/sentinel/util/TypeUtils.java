package rt.sentinel.util;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 类型判断工具类
 * 
 * 提供各种类型检测方法，用于确定对象应该使用哪个处理器
 * 
 * @author SentinelRT
 */
public final class TypeUtils {
    
    private TypeUtils() {
        // 工具类不允许实例化
    }
    
    /**
     * 判断是否为null
     * 
     * @param obj 对象
     * @return true 如果为null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }
    
    /**
     * 判断是否为基本类型或其包装类
     * 
     * @param clazz 类型
     * @return true 如果是基本类型或包装类
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() ||
               clazz == Boolean.class ||
               clazz == Byte.class ||
               clazz == Short.class ||
               clazz == Character.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Float.class ||
               clazz == Double.class;
    }
    
    /**
     * 判断是否为数值类型
     * 
     * @param clazz 类型
     * @return true 如果是数值类型
     */
    public static boolean isNumber(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz) ||
               clazz == byte.class ||
               clazz == short.class ||
               clazz == int.class ||
               clazz == long.class ||
               clazz == float.class ||
               clazz == double.class;
    }
    
    /**
     * 判断是否为布尔类型
     * 
     * @param clazz 类型
     * @return true 如果是布尔类型
     */
    public static boolean isBoolean(Class<?> clazz) {
        return clazz == boolean.class || clazz == Boolean.class;
    }
    
    /**
     * 判断是否为字符类型
     * 
     * @param clazz 类型
     * @return true 如果是字符类型
     */
    public static boolean isChar(Class<?> clazz) {
        return clazz == char.class || clazz == Character.class;
    }
    
    /**
     * 判断是否为字符串类型
     * 
     * @param clazz 类型
     * @return true 如果是字符串类型
     */
    public static boolean isString(Class<?> clazz) {
        return clazz == String.class;
    }
    
    /**
     * 判断是否为数组类型
     * 
     * @param clazz 类型
     * @return true 如果是数组类型
     */
    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }
    
    /**
     * 判断是否为byte数组
     * 
     * @param clazz 类型
     * @return true 如果是byte[]
     */
    public static boolean isByteArray(Class<?> clazz) {
        return clazz == byte[].class;
    }
    
    /**
     * 判断是否为Collection类型
     * 
     * @param clazz 类型
     * @return true 如果是Collection类型
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }
    
    /**
     * 判断是否为Map类型
     * 
     * @param clazz 类型
     * @return true 如果是Map类型
     */
    public static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }
    
    /**
     * 判断是否为枚举类型
     * 
     * @param clazz 类型
     * @return true 如果是枚举类型
     */
    public static boolean isEnum(Class<?> clazz) {
        return clazz.isEnum();
    }
    
    /**
     * 判断是否为日期类型
     * 
     * @param clazz 类型
     * @return true 如果是Date或Calendar类型
     */
    public static boolean isDate(Class<?> clazz) {
        return Date.class.isAssignableFrom(clazz) ||
               Calendar.class.isAssignableFrom(clazz);
    }
    
    /**
     * 获取类型的简单名称
     * 处理数组类型，返回如 "int[]"、"String[][]" 等形式
     * 
     * @param clazz 类型
     * @return 类型简单名称
     */
    public static String getSimpleTypeName(Class<?> clazz) {
        if (clazz.isArray()) {
            return getSimpleTypeName(clazz.getComponentType()) + "[]";
        }
        return clazz.getSimpleName();
    }
    
    /**
     * 获取类型的完整名称
     * 处理数组类型，返回如 "int[]"、"java.lang.String[][]" 等形式
     * 
     * @param clazz 类型
     * @return 类型完整名称
     */
    public static String getFullTypeName(Class<?> clazz) {
        if (clazz.isArray()) {
            return getFullTypeName(clazz.getComponentType()) + "[]";
        }
        return clazz.getName();
    }
    
    /**
     * 判断是否为JDK内置类型（以java.或javax.开头）
     * 
     * @param clazz 类型
     * @return true 如果是JDK内置类型
     */
    public static boolean isJdkType(Class<?> clazz) {
        String name = clazz.getName();
        return name.startsWith("java.") || 
               name.startsWith("javax.") ||
               name.startsWith("sun.") ||
               name.startsWith("com.sun.");
    }
    
    /**
     * 判断是否为Android系统类型
     * 
     * @param clazz 类型
     * @return true 如果是Android系统类型
     */
    public static boolean isAndroidType(Class<?> clazz) {
        String name = clazz.getName();
        return name.startsWith("android.") ||
               name.startsWith("androidx.") ||
               name.startsWith("dalvik.") ||
               name.startsWith("com.android.");
    }
}
