package rt.sentinel.core;

import java.util.HashSet;
import java.util.Set;

/**
 * 序列化上下文
 * 
 * 在单次序列化过程中维护状态信息，包括：
 * - 当前递归深度
 * - 已访问对象集合（用于检测循环引用）
 * - 配置信息引用
 * 
 * @author SentinelRT
 */
public class SerializationContext {
    
    /** 序列化配置 */
    private final SerializerConfig config;
    
    /** 当前递归深度 */
    private int currentDepth;
    
    /** 已访问对象的身份哈希码集合，用于检测循环引用 */
    private final Set<Integer> visitedObjects;
    
    /**
     * 创建序列化上下文
     * 
     * @param config 序列化配置
     */
    public SerializationContext(SerializerConfig config) {
        this.config = config;
        this.currentDepth = 0;
        this.visitedObjects = new HashSet<>();
    }
    
    /**
     * 获取配置
     * 
     * @return 序列化配置
     */
    public SerializerConfig getConfig() {
        return config;
    }
    
    /**
     * 获取当前深度
     * 
     * @return 当前递归深度
     */
    public int getCurrentDepth() {
        return currentDepth;
    }
    
    /**
     * 检查是否已达到最大深度
     * 
     * @return true 如果已达到最大深度
     */
    public boolean isMaxDepthReached() {
        return currentDepth >= config.getMaxDepth();
    }
    
    /**
     * 进入下一层深度
     * 在序列化嵌套对象前调用
     */
    public void enterDepth() {
        currentDepth++;
    }
    
    /**
     * 退出当前深度
     * 在序列化嵌套对象完成后调用
     */
    public void exitDepth() {
        currentDepth--;
    }
    
    /**
     * 检查对象是否已被访问过（循环引用检测）
     * 
     * @param obj 要检查的对象
     * @return true 如果对象已被访问过
     */
    public boolean isVisited(Object obj) {
        if (obj == null) {
            return false;
        }
        return visitedObjects.contains(System.identityHashCode(obj));
    }
    
    /**
     * 标记对象为已访问
     * 
     * @param obj 要标记的对象
     */
    public void markVisited(Object obj) {
        if (obj != null) {
            visitedObjects.add(System.identityHashCode(obj));
        }
    }
    
    /**
     * 取消对象的已访问标记
     * 在完成对象序列化后调用，允许同一对象在不同分支中出现
     * 
     * @param obj 要取消标记的对象
     */
    public void unmarkVisited(Object obj) {
        if (obj != null) {
            visitedObjects.remove(System.identityHashCode(obj));
        }
    }
    
    /**
     * 获取已访问对象数量
     * 
     * @return 已访问对象数量
     */
    public int getVisitedCount() {
        return visitedObjects.size();
    }
    
    /**
     * 重置上下文状态
     * 用于重用上下文对象
     */
    public void reset() {
        this.currentDepth = 0;
        this.visitedObjects.clear();
    }
}
