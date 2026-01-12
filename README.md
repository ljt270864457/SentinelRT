# SentinelRT - Android逆向对象序列化工具

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-113%20passed-brightgreen.svg)](src/test)

将任意Java对象转换为JSON字符串，专为Android逆向工程设计，配合Frida使用。

## ✨ 核心特性

- 🔍 **完整对象序列化** - 支持所有Java类型（基本类型、集合、数组、自定义类等）
- 🔐 **私有字段访问** - 自动通过反射访问private/protected字段
- 📊 **类元信息输出** - 类名、父类、接口信息
- 🛠️ **方法信息展示** - 构造函数、静态方法、实例方法签名（可选）
- 🔄 **嵌套对象支持** - 可配置递归深度，防止循环引用
- 🎨 **灵活输出格式** - 紧凑/美化两种模式
- 📦 **DEX封装** - 直接加载到Android进程
- ⚡ **零依赖** - 纯Java 11实现，无第三方库

## 🚀 快速开始

### 构建

```bash
# 编译为JAR
mvn clean package -DskipTests

# 构建DEX（需要Android SDK）
./build-dex.sh
```

### 部署到设备

```bash
adb push target/sentinelrt.dex /data/local/tmp/
```

### Frida使用

```javascript
Java.perform(function() {
    // 加载DEX
    Java.openClassFile("/data/local/tmp/sentinelrt.dex").load();
    var SentinelRT = Java.use("rt.sentinel.SentinelRT");
    
    // 查看帮助
    console.log(SentinelRT.help());
    
    // 序列化对象
    var obj = ...; // 你要查看的对象
    console.log(SentinelRT.toJson(obj));
});
```

## 📖 API文档

### 基础API

```javascript
// 1. 使用默认配置
SentinelRT.toJson(obj)

// 2. 指定最大深度
SentinelRT.toJson(obj, 3)

// 3. 指定深度 + 美化输出
SentinelRT.toJson(obj, 5, true)

// 4. 完整配置
var config = JSON.stringify({
    maxDepth: 5,              // 最大递归深度
    pretty: true,             // 美化输出
    collectionLimit: 10,      // 集合最大元素数（默认10）
    includeMeta: true,        // 包含类型元信息
    includeMethods: false,    // 包含方法信息（默认false）
    indentSize: 2             // 缩进空格数
});
SentinelRT.toJsonWithConfig(obj, config);
```

### 配置项说明

| 配置项 | 类型 | 默认值 | 说明 |
|-------|------|--------|------|
| `maxDepth` | int | 5 | 最大递归深度，防止循环引用 |
| `pretty` | boolean | false | 是否美化输出（带换行缩进） |
| `collectionLimit` | int | **10** | 集合/数组最大输出元素数 |
| `includeMeta` | boolean | true | 是否包含类型元信息 |
| `includeMethods` | boolean | **false** | 是否包含方法签名（默认不输出） |
| `indentSize` | int | 2 | 美化输出的缩进空格数 |

## 📄 输出格式

### 默认输出（不包含方法）

```json
{
  "@meta": {
    "@class": "com.example.User",
    "@superClass": "java.lang.Object",
    "@interfaces": ["java.io.Serializable"]
  },
  "@fields": {
    "name": {
      "@type": "java.lang.String",
      "@value": "张三"
    },
    "age": {
      "@type": "int",
      "@value": 25
    },
    "address": {
      "@type": "com.example.Address",
      "@value": {...}
    }
  }
}
```

### 包含方法信息（需要设置 `includeMethods: true`）

```json
{
  "@meta": {...},
  "@fields": {...},
  "@methods": {
    "constructors": [
      "public com.example.User()",
      "public com.example.User(String name, int age)"
    ],
    "staticMethods": [
      "public static User getInstance()"
    ],
    "instanceMethods": [
      "public String getName()",
      "public void setName(String name)"
    ]
  }
}
```

## 🎯 典型使用场景

### 场景1: Hook方法查看参数

```javascript
var MainActivity = Java.use("com.example.app.MainActivity");
MainActivity.login.implementation = function(username, password) {
    console.log("[*] login() called");
    console.log("Username: " + SentinelRT.toJson(username));
    console.log("Password: " + SentinelRT.toJson(password));
    return this.login(username, password);
};
```

### 场景2: 查看对象完整结构（包含方法）

```javascript
var config = JSON.stringify({
    maxDepth: 3,
    pretty: true,
    includeMethods: true  // 显示方法签名
});
var json = SentinelRT.toJsonWithConfig(this, config);
console.log(json);
```

### 场景3: 只查看字段值（不要方法）

```javascript
// 默认配置已经不包含方法信息
var json = SentinelRT.toJson(obj, 3, true);
console.log(json);
```

## 🔧 数据类型处理

| 类型 | 输出格式 | 示例 |
|------|----------|------|
| `null` | `null` | `null` |
| `boolean` | `true`/`false` | `true` |
| `int/long` | 数字 | `42` |
| `float/double` | 数字 | `3.14` |
| `NaN/Infinity` | 字符串 | `"NaN"`, `"Infinity"` |
| `char` | 字符串 | `"A"` |
| `String` | 转义字符串 | `"hello\nworld"` |
| **`byte[]`** | **十六进制** | `"48656c6c6f"` |
| `int[]` | JSON数组 | `[1, 2, 3]` |
| `List/Set` | JSON数组 | `[1, 2, 3]` |
| `Map` | JSON对象 | `{"key": "value"}` |
| `Enum` | 枚举名 | `"RUNNING"` |
| `Date` | ISO 8601 | `"2026-01-12T10:30:00.000Z"` |
| 自定义对象 | 递归序列化 | `{@meta, @fields, ...}` |

## 🧪 测试

项目包含113个单元测试，覆盖率100%：

```bash
mvn test
```

测试覆盖：
- ✅ 所有基本类型和包装类
- ✅ 字符串转义（引号、换行、Unicode）
- ✅ 数组、集合、Map
- ✅ 枚举、日期类型
- ✅ 嵌套对象、继承场景
- ✅ 私有字段访问
- ✅ byte[]十六进制输出
- ✅ 方法签名格式化
- ✅ 类元信息获取

## 📁 项目结构

```
SentinelRT/
├── src/main/java/rt/sentinel/
│   ├── SentinelRT.java              # 主入口
│   ├── core/                        # 核心引擎
│   ├── handler/                     # 类型处理器
│   ├── json/                        # JSON构建器
│   └── util/                        # 工具类
├── src/test/java/                   # 113个测试用例
├── build-dex.sh                     # DEX构建脚本
├── frida-example.js                 # Frida使用示例
└── pom.xml                          # Maven配置
```

## 📝 更新日志

### v1.0.0 (2026-01-12)

- ✨ 初始版本发布
- 🔧 **默认不输出方法信息** - `includeMethods` 默认为 `false`
- 📊 支持完整对象序列化
- 🔐 自动访问私有字段
- 📦 byte[]输出为十六进制
- ✅ 113个测试全部通过

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 许可证

MIT License

---

**提示**: 默认情况下不输出方法信息以减少输出体积。如需查看方法，请设置 `includeMethods: true`。
