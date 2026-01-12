package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.MethodSignatureFormatter;
import rt.sentinel.util.ReflectionUtils;
import rt.sentinel.util.TypeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 通用对象处理器
 * 
 * 处理所有没有特定处理器的对象类型
 * 通过反射获取对象的所有字段并递归序列化
 * 
 * 输出格式：
 * {
 *   "@meta": {
 *     "@class": "完整类名",
 *     "@superClass": "父类名",
 *     "@interfaces": ["接口1", "接口2"]
 *   },
 *   "@fields": {
 *     "fieldName": {
 *       "@type": "字段类型",
 *       "@value": 字段值
 *     }
 *   },
 *   "@methods": {
 *     "constructors": ["签名1", "签名2"],
 *     "staticMethods": ["签名1"],
 *     "instanceMethods": ["签名1", "签名2"]
 *   }
 * }
 * 
 * @author SentinelRT
 */
public class ObjectHandler implements TypeHandler {
    
    /** 类型处理器注册表 */
    private final TypeHandlerRegistry registry;
    
    /**
     * 创建对象处理器
     * 
     * @param registry 类型处理器注册表（用于处理字段值）
     */
    public ObjectHandler(TypeHandlerRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        // 作为默认处理器，可以处理任何类型
        return true;
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
            return;
        }
        
        Class<?> clazz = obj.getClass();
        
        // 检查深度限制
        if (context.isMaxDepthReached()) {
            builder.value("[MaxDepthReached: " + clazz.getName() + "]");
            return;
        }
        
        // 检查循环引用
        if (context.isVisited(obj)) {
            builder.value("[CircularReference: " + clazz.getName() + "@" + 
                         Integer.toHexString(System.identityHashCode(obj)) + "]");
            return;
        }
        
        // 标记为已访问
        context.markVisited(obj);
        context.enterDepth();
        
        try {
            builder.beginObject();
            
            // 输出类元信息
            if (context.getConfig().isIncludeMeta()) {
                serializeMeta(clazz, builder);
            }
            
            // 输出字段
            serializeFields(obj, clazz, builder, context);
            
            // 输出方法信息
            if (context.getConfig().isIncludeMethods()) {
                serializeMethods(clazz, builder);
            }
            
            builder.endObject();
        } finally {
            context.exitDepth();
            context.unmarkVisited(obj);
        }
    }
    
    /**
     * 序列化类元信息
     */
    private void serializeMeta(Class<?> clazz, JsonBuilder builder) {
        builder.key("@meta");
        builder.beginObject();
        
        // 类名
        builder.key("@class");
        builder.value(clazz.getName());
        
        // 父类
        Class<?> superClass = ReflectionUtils.getSuperClass(clazz);
        builder.key("@superClass");
        if (superClass != null) {
            builder.value(superClass.getName());
        } else {
            builder.value("java.lang.Object");
        }
        
        // 接口列表
        builder.key("@interfaces");
        List<Class<?>> interfaces = ReflectionUtils.getAllInterfaces(clazz);
        builder.beginArray();
        for (Class<?> iface : interfaces) {
            builder.value(iface.getName());
        }
        builder.endArray();
        
        builder.endObject();
    }
    
    /**
     * 序列化所有字段
     */
    private void serializeFields(Object obj, Class<?> clazz, JsonBuilder builder, 
                                  SerializationContext context) {
        builder.key("@fields");
        builder.beginObject();
        
        List<Field> fields = ReflectionUtils.getAllFields(clazz);
        
        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> declaringClass = field.getDeclaringClass();
            
            // 如果字段来自父类，添加类名前缀以区分
            if (declaringClass != clazz) {
                fieldName = declaringClass.getSimpleName() + "." + fieldName;
            }
            
            builder.key(fieldName);
            builder.beginObject();
            
            // 字段类型
            builder.key("@type");
            builder.value(TypeUtils.getFullTypeName(field.getType()));
            
            // 字段值
            builder.key("@value");
            Object value = ReflectionUtils.getFieldValue(field, obj);
            serializeValue(value, builder, context);
            
            builder.endObject();
        }
        
        builder.endObject();
    }
    
    /**
     * 序列化方法信息
     */
    private void serializeMethods(Class<?> clazz, JsonBuilder builder) {
        builder.key("@methods");
        builder.beginObject();
        
        ReflectionUtils.MethodInfo methodInfo = ReflectionUtils.getAllMethods(clazz);
        
        // 构造函数
        builder.key("constructors");
        builder.beginArray();
        for (Constructor<?> constructor : methodInfo.getConstructors()) {
            builder.value(MethodSignatureFormatter.format(constructor));
        }
        builder.endArray();
        
        // 静态方法
        builder.key("staticMethods");
        builder.beginArray();
        for (Method method : methodInfo.getStaticMethods()) {
            builder.value(MethodSignatureFormatter.format(method));
        }
        builder.endArray();
        
        // 实例方法
        builder.key("instanceMethods");
        builder.beginArray();
        for (Method method : methodInfo.getInstanceMethods()) {
            builder.value(MethodSignatureFormatter.format(method));
        }
        builder.endArray();
        
        builder.endObject();
    }
    
    /**
     * 序列化字段值
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
        // 最低优先级，作为默认处理器
        return Integer.MAX_VALUE;
    }
}
