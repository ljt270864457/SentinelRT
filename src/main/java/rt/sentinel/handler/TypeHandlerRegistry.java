package rt.sentinel.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型处理器注册表
 * 
 * 管理所有类型处理器，提供注册、查找功能
 * 支持自定义处理器扩展
 * 
 * @author SentinelRT
 */
public class TypeHandlerRegistry {
    
    /** 处理器列表（按优先级排序） */
    private final List<TypeHandler> handlers;
    
    /** 类型到处理器的缓存 */
    private final Map<Class<?>, TypeHandler> handlerCache;
    
    /** 默认的对象处理器（用于未找到特定处理器的情况） */
    private TypeHandler defaultHandler;
    
    /**
     * 创建类型处理器注册表
     */
    public TypeHandlerRegistry() {
        this.handlers = new ArrayList<>();
        this.handlerCache = new ConcurrentHashMap<>();
    }
    
    /**
     * 注册一个类型处理器
     * 
     * @param handler 要注册的处理器
     */
    public void register(TypeHandler handler) {
        handlers.add(handler);
        // 按优先级排序
        handlers.sort(Comparator.comparingInt(TypeHandler::getPriority));
        // 清空缓存
        handlerCache.clear();
    }
    
    /**
     * 设置默认处理器
     * 用于处理没有特定处理器的类型
     * 
     * @param handler 默认处理器
     */
    public void setDefaultHandler(TypeHandler handler) {
        this.defaultHandler = handler;
    }
    
    /**
     * 查找能处理指定类型的处理器
     * 
     * @param clazz 要处理的类型
     * @return 找到的处理器，如果没有找到则返回默认处理器
     */
    public TypeHandler findHandler(Class<?> clazz) {
        // 先检查缓存
        TypeHandler cached = handlerCache.get(clazz);
        if (cached != null) {
            return cached;
        }
        
        // 遍历处理器查找
        for (TypeHandler handler : handlers) {
            if (handler.canHandle(clazz)) {
                handlerCache.put(clazz, handler);
                return handler;
            }
        }
        
        // 使用默认处理器
        if (defaultHandler != null) {
            handlerCache.put(clazz, defaultHandler);
            return defaultHandler;
        }
        
        return null;
    }
    
    /**
     * 获取所有已注册的处理器
     * 
     * @return 处理器列表的副本
     */
    public List<TypeHandler> getAllHandlers() {
        return new ArrayList<>(handlers);
    }
    
    /**
     * 清空所有处理器
     */
    public void clear() {
        handlers.clear();
        handlerCache.clear();
        defaultHandler = null;
    }
    
    /**
     * 初始化默认处理器
     * 注册所有内置的类型处理器
     * 
     * @return this
     */
    public TypeHandlerRegistry initDefaults() {
        // 按优先级注册处理器（数值越小优先级越高）
        register(new PrimitiveHandler());      // 优先级 10
        register(new StringHandler());          // 优先级 20
        register(new EnumHandler());            // 优先级 30
        register(new DateHandler());            // 优先级 40
        register(new ArrayHandler(this));       // 优先级 50
        register(new CollectionHandler(this));  // 优先级 60
        register(new MapHandler(this));         // 优先级 70
        
        // 设置默认处理器（处理所有其他对象类型）
        setDefaultHandler(new ObjectHandler(this));
        
        return this;
    }
}
