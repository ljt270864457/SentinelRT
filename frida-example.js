/**
 * SentinelRT Frida 使用示例
 * 
 * 功能：将Java对象转换为JSON，方便在Android逆向过程中查看对象内容
 * 
 * 使用步骤：
 * 1. 构建项目：./build-dex.sh
 * 2. 将DEX推送到设备：adb push target/sentinelrt.dex /data/local/tmp/
 * 3. 修改下面的包名和目标类
 * 4. 运行Frida：frida -U -f <包名> -l frida-example.js
 */

Java.perform(function() {
    console.log("[*] SentinelRT Example Script Starting...");
    
    // =============================================
    // Step 1: 加载 SentinelRT DEX
    // =============================================
    try {
        Java.openClassFile("/data/local/tmp/sentinelrt.dex").load();
        console.log("[+] SentinelRT DEX loaded successfully!");
    } catch (e) {
        console.log("[-] Failed to load SentinelRT DEX: " + e);
        console.log("    请确保已将 sentinelrt.dex 推送到 /data/local/tmp/");
        return;
    }
    
    // 获取 SentinelRT 类引用
    var SentinelRT = Java.use("rt.sentinel.SentinelRT");
    
    // 打印帮助信息
    console.log("\n" + SentinelRT.help());
    console.log("\n[*] SentinelRT Version: " + SentinelRT.version());
    
    // =============================================
    // Step 2: Hook 目标类的方法
    // =============================================
    
    // 示例：Hook MainActivity 的 onCreate 方法
    // 请根据实际情况修改包名和类名
    /*
    var MainActivity = Java.use("com.example.app.MainActivity");
    MainActivity.onCreate.implementation = function(savedInstanceState) {
        console.log("\n[*] MainActivity.onCreate called");
        
        // 序列化当前Activity对象
        var json = SentinelRT.toJson(this, 3, true);
        console.log("[*] Activity object:");
        console.log(json);
        
        // 调用原方法
        return this.onCreate(savedInstanceState);
    };
    */
    
    // =============================================
    // 使用示例
    // =============================================
    
    // 示例1：基础用法 - 默认配置
    console.log("\n=== 示例1: 基础用法 ===");
    var str = Java.use("java.lang.String").$new("Hello SentinelRT!");
    console.log(SentinelRT.toJson(str));
    
    // 示例2：指定深度
    console.log("\n=== 示例2: 指定深度 ===");
    var list = Java.use("java.util.ArrayList").$new();
    list.add("item1");
    list.add("item2");
    list.add(Java.use("java.lang.Integer").valueOf(123));
    console.log(SentinelRT.toJson(list, 2));
    
    // 示例3：美化输出
    console.log("\n=== 示例3: 美化输出 ===");
    var map = Java.use("java.util.HashMap").$new();
    map.put("name", "张三");
    map.put("age", Java.use("java.lang.Integer").valueOf(25));
    console.log(SentinelRT.toJson(map, 3, true));
    
    // 示例4：完整配置 - 开启方法信息输出
    console.log("\n=== 示例4: 完整配置（包含方法信息）===");
    var config = JSON.stringify({
        maxDepth: 3,
        pretty: true,
        collectionLimit: 10,
        includeMeta: true,
        includeMethods: true  // 需要输出方法信息时设置为true
    });
    console.log(SentinelRT.toJsonWithConfig(map, config));
    
    // =============================================
    // 实用工具函数
    // =============================================
    
    /**
     * 将对象序列化为JSON并打印
     * @param {Object} obj - 要序列化的对象
     * @param {string} label - 标签/描述
     * @param {number} depth - 最大深度（默认3）
     */
    function dumpObject(obj, label, depth) {
        depth = depth || 3;
        console.log("\n[DUMP] " + (label || "Object") + ":");
        console.log(SentinelRT.toJson(obj, depth, true));
    }
    
    /**
     * 只输出对象的字段（不输出方法信息）
     * @param {Object} obj - 要序列化的对象
     * @param {string} label - 标签/描述
     */
    function dumpFields(obj, label) {
        var config = JSON.stringify({
            maxDepth: 3,
            pretty: true,
            includeMethods: false
        });
        console.log("\n[FIELDS] " + (label || "Object") + ":");
        console.log(SentinelRT.toJsonWithConfig(obj, config));
    }
    
    /**
     * 只输出对象的方法签名
     * @param {Object} obj - 要序列化的对象
     * @param {string} label - 标签/描述
     */
    function dumpMethods(obj, label) {
        var config = JSON.stringify({
            maxDepth: 1,
            pretty: true,
            includeMeta: true,
            includeMethods: true
        });
        console.log("\n[METHODS] " + (label || "Object") + ":");
        console.log(SentinelRT.toJsonWithConfig(obj, config));
    }
    
    // 将工具函数挂载到全局（方便在Frida REPL中使用）
    global.SentinelRT = SentinelRT;
    global.dumpObject = dumpObject;
    global.dumpFields = dumpFields;
    global.dumpMethods = dumpMethods;
    
    console.log("\n[*] 工具函数已挂载到全局:");
    console.log("    - SentinelRT.toJson(obj)");
    console.log("    - SentinelRT.toJson(obj, depth)");
    console.log("    - SentinelRT.toJson(obj, depth, pretty)");
    console.log("    - SentinelRT.toJsonWithConfig(obj, configJson)");
    console.log("    - dumpObject(obj, label, depth)");
    console.log("    - dumpFields(obj, label)");
    console.log("    - dumpMethods(obj, label)");
    
    console.log("\n[*] SentinelRT Ready!");
});
