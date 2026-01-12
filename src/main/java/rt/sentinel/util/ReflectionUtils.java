package rt.sentinel.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 反射工具类
 * 
 * 提供获取类的字段、方法、接口等信息的工具方法
 * 所有字段和方法都会设置 setAccessible(true) 以确保可访问
 * 
 * @author SentinelRT
 */
public final class ReflectionUtils {
    
    private ReflectionUtils() {
        // 工具类不允许实例化
    }
    
    /**
     * 获取类的所有字段（包括父类的字段）
     * 
     * @param clazz 目标类
     * @return 所有字段列表，按声明顺序排列，父类字段在前
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        
        // 递归获取所有父类的字段，直到Object类
        while (current != null && current != Object.class) {
            Field[] declaredFields = current.getDeclaredFields();
            for (Field field : declaredFields) {
                // 跳过合成字段（如编译器生成的字段）
                if (field.isSynthetic()) {
                    continue;
                }
                try {
                    // 强制设置可访问，确保private字段也能读取
                    field.setAccessible(true);
                    fields.add(field);
                } catch (SecurityException e) {
                    // 安全限制，跳过该字段
                }
            }
            current = current.getSuperclass();
        }
        
        return fields;
    }
    
    /**
     * 获取字段值
     * 
     * @param field 字段
     * @param obj 对象实例
     * @return 字段值，如果获取失败返回null
     */
    public static Object getFieldValue(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            // 获取失败，返回null
            return null;
        }
    }
    
    /**
     * 获取类的方法信息
     * 
     * @param clazz 目标类
     * @return 方法信息对象，包含构造函数、静态方法和实例方法
     */
    public static MethodInfo getAllMethods(Class<?> clazz) {
        List<Constructor<?>> constructors = new ArrayList<>();
        List<Method> staticMethods = new ArrayList<>();
        List<Method> instanceMethods = new ArrayList<>();
        
        // 获取当前类的构造函数
        try {
            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : declaredConstructors) {
                if (!constructor.isSynthetic()) {
                    constructor.setAccessible(true);
                    constructors.add(constructor);
                }
            }
        } catch (SecurityException e) {
            // 安全限制，跳过
        }
        
        // 递归获取所有父类的方法
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Method[] declaredMethods = current.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    // 跳过合成方法和桥接方法
                    if (method.isSynthetic() || method.isBridge()) {
                        continue;
                    }
                    
                    try {
                        method.setAccessible(true);
                        if (Modifier.isStatic(method.getModifiers())) {
                            staticMethods.add(method);
                        } else {
                            instanceMethods.add(method);
                        }
                    } catch (SecurityException e) {
                        // 安全限制，跳过该方法
                    }
                }
            } catch (SecurityException e) {
                // 安全限制，跳过
            }
            
            current = current.getSuperclass();
        }
        
        return new MethodInfo(constructors, staticMethods, instanceMethods);
    }
    
    /**
     * 获取类实现的所有接口（包括父类实现的接口）
     * 
     * @param clazz 目标类
     * @return 所有接口列表
     */
    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        // 使用LinkedHashSet保持顺序并去重
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        collectInterfaces(clazz, interfaces);
        return new ArrayList<>(interfaces);
    }
    
    /**
     * 递归收集接口
     */
    private static void collectInterfaces(Class<?> clazz, Set<Class<?>> interfaces) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        
        // 添加直接实现的接口
        for (Class<?> iface : clazz.getInterfaces()) {
            interfaces.add(iface);
            // 接口可能继承其他接口
            collectInterfaces(iface, interfaces);
        }
        
        // 递归处理父类
        collectInterfaces(clazz.getSuperclass(), interfaces);
    }
    
    /**
     * 获取类的父类
     * 
     * @param clazz 目标类
     * @return 父类，如果是Object或接口则返回null
     */
    public static Class<?> getSuperClass(Class<?> clazz) {
        if (clazz == null || clazz == Object.class || clazz.isInterface()) {
            return null;
        }
        return clazz.getSuperclass();
    }
    
    /**
     * 获取类的继承链
     * 
     * @param clazz 目标类
     * @return 继承链列表，从当前类到Object（不包含Object）
     */
    public static List<Class<?>> getInheritanceChain(Class<?> clazz) {
        List<Class<?>> chain = new ArrayList<>();
        Class<?> current = clazz;
        
        while (current != null && current != Object.class) {
            chain.add(current);
            current = current.getSuperclass();
        }
        
        return chain;
    }
    
    /**
     * 方法信息容器类
     */
    public static class MethodInfo {
        private final List<Constructor<?>> constructors;
        private final List<Method> staticMethods;
        private final List<Method> instanceMethods;
        
        public MethodInfo(List<Constructor<?>> constructors, 
                         List<Method> staticMethods,
                         List<Method> instanceMethods) {
            this.constructors = constructors;
            this.staticMethods = staticMethods;
            this.instanceMethods = instanceMethods;
        }
        
        public List<Constructor<?>> getConstructors() {
            return constructors;
        }
        
        public List<Method> getStaticMethods() {
            return staticMethods;
        }
        
        public List<Method> getInstanceMethods() {
            return instanceMethods;
        }
    }
}
