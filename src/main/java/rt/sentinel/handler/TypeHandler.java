package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;

/**
 * 类型处理器接口
 * 
 * 定义将特定类型对象序列化为JSON的方法
 * 每种需要特殊处理的类型都应该有对应的处理器实现
 * 
 * @author SentinelRT
 */
public interface TypeHandler {
    
    /**
     * 检查此处理器是否能处理指定类型
     * 
     * @param clazz 要检查的类型
     * @return true 如果此处理器能处理该类型
     */
    boolean canHandle(Class<?> clazz);
    
    /**
     * 将对象序列化到JsonBuilder
     * 
     * @param obj 要序列化的对象（不为null）
     * @param builder JSON构建器
     * @param context 序列化上下文
     */
    void handle(Object obj, JsonBuilder builder, SerializationContext context);
    
    /**
     * 获取处理器优先级（数值越小优先级越高）
     * 默认优先级为100
     * 
     * @return 优先级值
     */
    default int getPriority() {
        return 100;
    }
}
