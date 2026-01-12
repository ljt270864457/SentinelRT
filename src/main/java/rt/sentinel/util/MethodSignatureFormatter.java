package rt.sentinel.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * 方法签名格式化器
 * 
 * 将 Method 和 Constructor 对象转换为可读的签名字符串
 * 
 * 输出格式：[修饰符] [返回类型] [方法名]([参数类型 参数名, ...])
 * 
 * 示例：
 * - public static byte[][] m44143a(List list, riq riqVar)
 * - private static List m44144b(int[] iArr)
 * - public void onCreate(Bundle bundle)
 * 
 * @author SentinelRT
 */
public final class MethodSignatureFormatter {
    
    private MethodSignatureFormatter() {
        // 工具类不允许实例化
    }
    
    /**
     * 格式化方法签名
     * 
     * @param method 方法对象
     * @return 格式化后的方法签名字符串
     */
    public static String format(Method method) {
        StringBuilder sb = new StringBuilder();
        
        // 1. 修饰符
        String modifiers = Modifier.toString(method.getModifiers());
        if (!modifiers.isEmpty()) {
            sb.append(modifiers).append(" ");
        }
        
        // 2. 返回类型
        sb.append(formatTypeName(method.getReturnType())).append(" ");
        
        // 3. 方法名
        sb.append(method.getName());
        
        // 4. 参数列表
        sb.append("(");
        sb.append(formatParameters(method.getParameters(), method.getParameterTypes()));
        sb.append(")");
        
        return sb.toString();
    }
    
    /**
     * 格式化构造函数签名
     * 
     * @param constructor 构造函数对象
     * @return 格式化后的构造函数签名字符串
     */
    public static String format(Constructor<?> constructor) {
        StringBuilder sb = new StringBuilder();
        
        // 1. 修饰符
        String modifiers = Modifier.toString(constructor.getModifiers());
        if (!modifiers.isEmpty()) {
            sb.append(modifiers).append(" ");
        }
        
        // 2. 类名（构造函数没有返回类型，使用类的完整名称）
        sb.append(constructor.getDeclaringClass().getName());
        
        // 3. 参数列表
        sb.append("(");
        sb.append(formatParameters(constructor.getParameters(), constructor.getParameterTypes()));
        sb.append(")");
        
        return sb.toString();
    }
    
    /**
     * 格式化参数列表
     * 
     * @param parameters 参数对象数组（包含参数名）
     * @param parameterTypes 参数类型数组
     * @return 格式化后的参数列表字符串
     */
    private static String formatParameters(Parameter[] parameters, Class<?>[] parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            
            // 参数类型
            sb.append(formatTypeName(parameterTypes[i]));
            sb.append(" ");
            
            // 参数名
            if (parameters != null && i < parameters.length && parameters[i].isNamePresent()) {
                sb.append(parameters[i].getName());
            } else {
                // 如果参数名不可用，生成默认名称
                sb.append(generateParamName(parameterTypes[i], i));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 格式化类型名称
     * 处理数组类型和泛型类型
     * 
     * @param type 类型
     * @return 格式化后的类型名称
     */
    private static String formatTypeName(Class<?> type) {
        if (type == null) {
            return "void";
        }
        
        // 处理数组类型
        if (type.isArray()) {
            return formatTypeName(type.getComponentType()) + "[]";
        }
        
        // 使用简单类名
        return type.getSimpleName();
    }
    
    /**
     * 生成默认参数名
     * 根据参数类型生成有意义的参数名
     * 
     * @param type 参数类型
     * @param index 参数索引
     * @return 生成的参数名
     */
    private static String generateParamName(Class<?> type, int index) {
        String typeName = type.getSimpleName();
        
        // 处理数组类型
        if (type.isArray()) {
            typeName = type.getComponentType().getSimpleName() + "Arr";
        }
        
        // 将类型名首字母小写作为参数名
        if (typeName.length() > 0) {
            char first = Character.toLowerCase(typeName.charAt(0));
            if (typeName.length() > 1) {
                typeName = first + typeName.substring(1);
            } else {
                typeName = String.valueOf(first);
            }
        }
        
        // 基本类型使用特定的名称
        if (type == int.class || type == Integer.class) {
            typeName = "i";
        } else if (type == long.class || type == Long.class) {
            typeName = "l";
        } else if (type == boolean.class || type == Boolean.class) {
            typeName = "b";
        } else if (type == String.class) {
            typeName = "str";
        } else if (type == byte[].class) {
            typeName = "bytes";
        } else if (type == int[].class) {
            typeName = "iArr";
        } else if (type == Object.class) {
            typeName = "obj";
        }
        
        // 如果有多个同类型参数，添加索引后缀
        // 为简化实现，始终添加索引（除非是第一个）
        if (index > 0 && typeName.length() <= 3) {
            typeName = typeName + index;
        }
        
        return typeName;
    }
    
    /**
     * 获取方法的简短签名（不含修饰符）
     * 
     * @param method 方法对象
     * @return 简短签名字符串
     */
    public static String formatShort(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatTypeName(method.getReturnType())).append(" ");
        sb.append(method.getName());
        sb.append("(");
        
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatTypeName(paramTypes[i]));
        }
        
        sb.append(")");
        return sb.toString();
    }
}
