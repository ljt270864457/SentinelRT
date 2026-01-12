package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.TypeUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Collection类型处理器
 * 
 * 处理所有Collection接口的实现类：
 * - List (ArrayList, LinkedList等)
 * - Set (HashSet, TreeSet等)
 * - Queue, Deque等
 * 
 * 输出为JSON数组格式
 * 
 * @author SentinelRT
 */
public class CollectionHandler implements TypeHandler {
    
    /** 类型处理器注册表 */
    private final TypeHandlerRegistry registry;
    
    /**
     * 创建Collection处理器
     * 
     * @param registry 类型处理器注册表（用于处理集合元素）
     */
    public CollectionHandler(TypeHandlerRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        return TypeUtils.isCollection(clazz);
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
            return;
        }
        
        Collection<?> collection = (Collection<?>) obj;
        int size = collection.size();
        int limit = context.getConfig().getCollectionLimit();
        
        builder.beginArray();
        
        Iterator<?> iterator = collection.iterator();
        int count = 0;
        
        while (iterator.hasNext() && count < limit) {
            Object element = iterator.next();
            serializeElement(element, builder, context);
            count++;
        }
        
        // 如果集合被截断，添加提示
        if (size > limit) {
            builder.value("[... " + (size - limit) + " more elements truncated]");
        }
        
        builder.endArray();
    }
    
    /**
     * 序列化集合元素
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
    
    @Override
    public int getPriority() {
        return 60;
    }
}
