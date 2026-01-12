package rt.sentinel;

import rt.sentinel.core.ObjectSerializer;
import rt.sentinel.core.SerializerConfig;

/**
 * SentinelRT - Android逆向对象序列化工具
 * 
 * 主入口类，提供静态方法供Frida JS调用
 * 将任意Java对象转换为JSON字符串，方便在逆向过程中查看对象内容
 * 
 * 功能特性：
 * - 支持所有基本类型和包装类
 * - 支持数组、集合、Map
 * - 支持枚举、日期类型
 * - 支持嵌套对象（可配置深度）
 * - 输出对象的类元信息（类名、父类、接口）
 * - 输出对象的方法签名
 * - 私有字段可访问
 * - byte[]输出为十六进制字符串
 * 
 * Frida使用示例：
 * <pre>
 * Java.perform(function() {
 *     Java.openClassFile("/data/local/tmp/sentinelrt.dex").load();
 *     var SentinelRT = Java.use("rt.sentinel.SentinelRT");
 *     
 *     // 查看帮助
 *     console.log(SentinelRT.help());
 *     
 *     // 序列化对象
 *     var json = SentinelRT.toJson(targetObj);
 *     console.log(json);
 * });
 * </pre>
 * 
 * @author SentinelRT
 * @version 1.0.0
 */
public final class SentinelRT {
    
    /** 版本号 */
    private static final String VERSION = "1.0.0";
    
    /** 单例序列化器 */
    private static final ObjectSerializer serializer = new ObjectSerializer();
    
    private SentinelRT() {
        // 工具类不允许实例化
    }
    
    // ==================== 核心API ====================
    
    /**
     * 将对象转换为JSON字符串（使用默认配置）
     * 
     * @param obj 要序列化的对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        return serializer.serialize(obj);
    }
    
    /**
     * 将对象转换为JSON字符串（指定最大深度）
     * 
     * @param obj 要序列化的对象
     * @param maxDepth 最大递归深度（默认5）
     * @return JSON字符串
     */
    public static String toJson(Object obj, int maxDepth) {
        return serializer.serialize(obj, maxDepth);
    }
    
    /**
     * 将对象转换为JSON字符串（指定深度和格式化）
     * 
     * @param obj 要序列化的对象
     * @param maxDepth 最大递归深度
     * @param pretty 是否美化输出（带缩进换行）
     * @return JSON字符串
     */
    public static String toJson(Object obj, int maxDepth, boolean pretty) {
        return serializer.serialize(obj, maxDepth, pretty);
    }
    
    /**
     * 将对象转换为JSON字符串（使用完整配置）
     * 
     * @param obj 要序列化的对象
     * @param configJson 配置JSON字符串，支持以下字段：
     *                   - maxDepth: int, 最大递归深度（默认5）
     *                   - pretty: boolean, 是否美化输出（默认false）
     *                   - collectionLimit: int, 集合最大元素数（默认100）
     *                   - includeMeta: boolean, 是否包含类型元信息（默认true）
     *                   - includeMethods: boolean, 是否包含方法信息（默认true）
     *                   - indentSize: int, 缩进空格数（默认2）
     * @return JSON字符串
     */
    public static String toJsonWithConfig(Object obj, String configJson) {
        SerializerConfig config = SerializerConfig.fromJson(configJson);
        return serializer.serialize(obj, config);
    }
    
    // ==================== 帮助信息 ====================
    
    /**
     * 获取版本号
     * 
     * @return 版本号字符串
     */
    public static String version() {
        return VERSION;
    }
    
    /**
     * 获取帮助文档
     * 
     * @return 帮助文档字符串
     */
    public static String help() {
        return HELP_TEXT;
    }
    
    /** 帮助文档 */
    private static final String HELP_TEXT = 
        "╔══════════════════════════════════════════════════════════════╗\n" +
        "║              SentinelRT - Object to JSON Tool                ║\n" +
        "║                     Version " + VERSION + "                           ║\n" +
        "╠══════════════════════════════════════════════════════════════╣\n" +
        "║ METHODS:                                                     ║\n" +
        "║                                                              ║\n" +
        "║ ► toJson(Object obj)                                         ║\n" +
        "║   Convert object to JSON with default settings               ║\n" +
        "║   Example: SentinelRT.toJson(myObj)                          ║\n" +
        "║                                                              ║\n" +
        "║ ► toJson(Object obj, int maxDepth)                           ║\n" +
        "║   Convert with specified recursion depth                     ║\n" +
        "║   Example: SentinelRT.toJson(myObj, 3)                       ║\n" +
        "║                                                              ║\n" +
        "║ ► toJson(Object obj, int maxDepth, boolean pretty)           ║\n" +
        "║   Convert with depth and pretty print option                 ║\n" +
        "║   Example: SentinelRT.toJson(myObj, 5, true)                 ║\n" +
        "║                                                              ║\n" +
        "║ ► toJsonWithConfig(Object obj, String configJson)            ║\n" +
        "║   Convert with full configuration                            ║\n" +
        "║   Config options:                                            ║\n" +
        "║     - maxDepth: int (default 5)                              ║\n" +
        "║     - pretty: boolean (default false)                        ║\n" +
        "║     - collectionLimit: int (default 10)                      ║\n" +
        "║     - includeMeta: boolean (default true)                    ║\n" +
        "║     - includeMethods: boolean (default false)                ║\n" +
        "║     - indentSize: int (default 2)                            ║\n" +
        "║   Example:                                                   ║\n" +
        "║     var config = JSON.stringify({                            ║\n" +
        "║       maxDepth: 3,                                           ║\n" +
        "║       pretty: true,                                          ║\n" +
        "║       collectionLimit: 50                                    ║\n" +
        "║     });                                                      ║\n" +
        "║     SentinelRT.toJsonWithConfig(myObj, config)               ║\n" +
        "║                                                              ║\n" +
        "║ ► help()                                                     ║\n" +
        "║   Show this help document                                    ║\n" +
        "║                                                              ║\n" +
        "║ ► version()                                                  ║\n" +
        "║   Get version number                                         ║\n" +
        "╠══════════════════════════════════════════════════════════════╣\n" +
        "║ OUTPUT FORMAT:                                               ║\n" +
        "║                                                              ║\n" +
        "║ {                                                            ║\n" +
        "║   \"@meta\": {                                                 ║\n" +
        "║     \"@class\": \"com.example.User\",                            ║\n" +
        "║     \"@superClass\": \"java.lang.Object\",                       ║\n" +
        "║     \"@interfaces\": [\"java.io.Serializable\"]                  ║\n" +
        "║   },                                                         ║\n" +
        "║   \"@fields\": {                                               ║\n" +
        "║     \"name\": { \"@type\": \"String\", \"@value\": \"test\" }          ║\n" +
        "║   }                                                          ║\n" +
        "║   // Note: @methods默认不输出，需要时设置includeMethods=true   ║\n" +
        "║ }                                                            ║\n" +
        "╠══════════════════════════════════════════════════════════════╣\n" +
        "║ FRIDA USAGE:                                                 ║\n" +
        "║                                                              ║\n" +
        "║ Java.perform(function() {                                    ║\n" +
        "║   Java.openClassFile('/data/local/tmp/sentinelrt.dex')       ║\n" +
        "║     .load();                                                 ║\n" +
        "║   var S = Java.use('rt.sentinel.SentinelRT');                ║\n" +
        "║   console.log(S.toJson(targetObj, 3, true));                 ║\n" +
        "║ });                                                          ║\n" +
        "╚══════════════════════════════════════════════════════════════╝";
    
    /**
     * 主函数（用于测试）
     */
    public static void main(String[] args) {
        System.out.println(help());
    }
}
