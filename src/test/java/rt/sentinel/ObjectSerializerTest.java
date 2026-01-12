package rt.sentinel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import rt.sentinel.core.ObjectSerializer;
import rt.sentinel.core.SerializerConfig;

import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ObjectSerializer 序列化引擎测试
 * 
 * 测试复杂对象序列化、嵌套对象、继承等场景
 */
@DisplayName("ObjectSerializer Tests")
class ObjectSerializerTest {
    
    private ObjectSerializer serializer;
    
    @BeforeEach
    void setUp() {
        serializer = new ObjectSerializer();
    }
    
    // ==================== 嵌套对象测试 ====================
    
    @Test
    @DisplayName("测试自定义类对象作为字段")
    void testNestedCustomObject() {
        User user = new User();
        user.name = "张三";
        user.age = 25;
        user.address = new Address();
        user.address.city = "北京";
        user.address.street = "长安街";
        user.address.zipCode = "100000";
        
        String json = serializer.serialize(user, 5, true);
        
        // 验证包含用户信息
        assertTrue(json.contains("张三"));
        assertTrue(json.contains("25"));
        
        // 验证包含嵌套的地址信息
        assertTrue(json.contains("北京"));
        assertTrue(json.contains("长安街"));
        assertTrue(json.contains("100000"));
        
        // 验证类元信息
        assertTrue(json.contains("@class"));
        assertTrue(json.contains("User"));
        assertTrue(json.contains("Address"));
    }
    
    @Test
    @DisplayName("测试深度嵌套对象")
    void testDeeplyNestedObject() {
        Node root = new Node("root");
        root.child = new Node("level1");
        root.child.child = new Node("level2");
        root.child.child.child = new Node("level3");
        root.child.child.child.child = new Node("level4");
        
        // 深度3应该能看到level2，但level3会被截断
        String json3 = serializer.serialize(root, 3, false);
        assertTrue(json3.contains("level2"));
        assertTrue(json3.contains("MaxDepthReached"));
        
        // 深度5应该能看到所有
        String json5 = serializer.serialize(root, 5, false);
        assertTrue(json5.contains("level4"));
    }
    
    // ==================== 继承测试 ====================
    
    @Test
    @DisplayName("测试继承场景 - 子类包含父类的私有字段")
    void testInheritance() {
        ChildClass child = new ChildClass();
        child.childField = "child value";
        child.setParentPrivate("parent private value");
        child.parentPublic = "parent public value";
        
        String json = serializer.serialize(child, 3, true);
        
        // 验证子类字段
        assertTrue(json.contains("childField"));
        assertTrue(json.contains("child value"));
        
        // 验证父类公共字段
        assertTrue(json.contains("parentPublic"));
        assertTrue(json.contains("parent public value"));
        
        // 验证父类私有字段也被获取
        assertTrue(json.contains("parentPrivate"));
        assertTrue(json.contains("parent private value"));
    }
    
    @Test
    @DisplayName("测试多层继承")
    void testMultiLevelInheritance() {
        GrandChild grandChild = new GrandChild();
        grandChild.grandChildField = "gc";
        grandChild.childField = "c";
        grandChild.parentPublic = "p";
        
        String json = serializer.serialize(grandChild, 3, true);
        
        assertTrue(json.contains("grandChildField"));
        assertTrue(json.contains("childField"));
        assertTrue(json.contains("parentPublic"));
    }
    
    // ==================== 类元信息测试 ====================
    
    @Test
    @DisplayName("测试类元信息 - 类名")
    void testMetaClassName() {
        SimpleClass obj = new SimpleClass();
        String json = serializer.serialize(obj, 3, true);
        
        assertTrue(json.contains("@meta"));
        assertTrue(json.contains("@class"));
        assertTrue(json.contains("SimpleClass"));
    }
    
    @Test
    @DisplayName("测试类元信息 - 父类")
    void testMetaSuperClass() {
        ChildClass child = new ChildClass();
        String json = serializer.serialize(child, 3, true);
        
        assertTrue(json.contains("@superClass"));
        assertTrue(json.contains("ParentClass"));
    }
    
    @Test
    @DisplayName("测试类元信息 - 接口")
    void testMetaInterfaces() {
        ClassWithInterfaces obj = new ClassWithInterfaces();
        String json = serializer.serialize(obj, 3, true);
        
        assertTrue(json.contains("@interfaces"));
        assertTrue(json.contains("Serializable"));
        assertTrue(json.contains("Comparable"));
    }
    
    @Test
    @DisplayName("测试无接口的类")
    void testNoInterfaces() {
        SimpleClass obj = new SimpleClass();
        String json = serializer.serialize(obj, 3, true);
        
        assertTrue(json.contains("@interfaces"));
        // 空接口列表在美化输出中可能换行
        assertTrue(json.contains("@interfaces") && 
                   (json.contains("[]") || json.contains("[\n")));
    }
    
    // ==================== 方法信息测试 ====================
    
    @Test
    @DisplayName("测试构造函数获取")
    void testConstructors() {
        MultiConstructorClass obj = new MultiConstructorClass();
        
        // 需要明确开启方法信息输出
        SerializerConfig config = new SerializerConfig();
        config.setMaxDepth(3).setPretty(true).setIncludeMethods(true);
        String json = serializer.serialize(obj, config);
        
        assertTrue(json.contains("@methods"));
        assertTrue(json.contains("constructors"));
        assertTrue(json.contains("MultiConstructorClass()"));
        assertTrue(json.contains("MultiConstructorClass(String"));
    }
    
    @Test
    @DisplayName("测试静态方法获取")
    void testStaticMethods() {
        ClassWithMethods obj = new ClassWithMethods();
        
        // 需要明确开启方法信息输出
        SerializerConfig config = new SerializerConfig();
        config.setMaxDepth(3).setPretty(true).setIncludeMethods(true);
        String json = serializer.serialize(obj, config);
        
        assertTrue(json.contains("staticMethods"));
        assertTrue(json.contains("staticMethod"));
        assertTrue(json.contains("static"));
    }
    
    @Test
    @DisplayName("测试实例方法获取")
    void testInstanceMethods() {
        ClassWithMethods obj = new ClassWithMethods();
        
        // 需要明确开启方法信息输出
        SerializerConfig config = new SerializerConfig();
        config.setMaxDepth(3).setPretty(true).setIncludeMethods(true);
        String json = serializer.serialize(obj, config);
        
        assertTrue(json.contains("instanceMethods"));
        assertTrue(json.contains("instanceMethod"));
    }
    
    @Test
    @DisplayName("测试方法签名格式")
    void testMethodSignatureFormat() {
        ClassWithMethods obj = new ClassWithMethods();
        
        // 需要明确开启方法信息输出
        SerializerConfig config = new SerializerConfig();
        config.setMaxDepth(3).setPretty(true).setIncludeMethods(true);
        String json = serializer.serialize(obj, config);
        
        // 验证包含修饰符、返回类型、方法名、参数
        assertTrue(json.contains("public"));
        assertTrue(json.contains("private"));
    }
    
    // ==================== 私有字段访问测试 ====================
    
    @Test
    @DisplayName("测试私有字段访问")
    void testPrivateFieldAccess() {
        PrivateFieldsClass obj = new PrivateFieldsClass();
        String json = serializer.serialize(obj, 3, true);
        
        assertTrue(json.contains("privateField"));
        assertTrue(json.contains("private value"));
    }
    
    @Test
    @DisplayName("测试protected字段访问")
    void testProtectedFieldAccess() {
        ProtectedFieldsClass obj = new ProtectedFieldsClass();
        String json = serializer.serialize(obj, 3, true);
        
        assertTrue(json.contains("protectedField"));
        assertTrue(json.contains("protected value"));
    }
    
    // ==================== 集合截断测试 ====================
    
    @Test
    @DisplayName("测试大集合截断")
    void testLargeCollectionTruncation() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            list.add(i);
        }
        
        SerializerConfig config = new SerializerConfig();
        config.setCollectionLimit(50);
        
        String json = serializer.serialize(list, config);
        
        assertTrue(json.contains("truncated"));
        assertTrue(json.contains("150")); // 200 - 50 = 150 more
    }
    
    // ==================== 配置测试 ====================
    
    @Test
    @DisplayName("测试不包含元信息")
    void testWithoutMeta() {
        SerializerConfig config = new SerializerConfig();
        config.setIncludeMeta(false);
        
        SimpleClass obj = new SimpleClass();
        String json = serializer.serialize(obj, config);
        
        assertFalse(json.contains("@meta"));
    }
    
    @Test
    @DisplayName("测试不包含方法信息")
    void testWithoutMethods() {
        SerializerConfig config = new SerializerConfig();
        config.setIncludeMethods(false);
        
        SimpleClass obj = new SimpleClass();
        String json = serializer.serialize(obj, config);
        
        assertFalse(json.contains("@methods"));
    }
    
    // ==================== 测试辅助类 ====================
    
    static class SimpleClass {
        public String field = "value";
    }
    
    static class User {
        public String name;
        public int age;
        public Address address;
    }
    
    static class Address {
        public String city;
        public String street;
        public String zipCode;
    }
    
    static class Node {
        public String name;
        public Node child;
        
        public Node(String name) {
            this.name = name;
        }
    }
    
    static class ParentClass {
        private String parentPrivate = "";
        public String parentPublic = "";
        
        public void setParentPrivate(String value) {
            this.parentPrivate = value;
        }
    }
    
    static class ChildClass extends ParentClass {
        public String childField;
    }
    
    static class GrandChild extends ChildClass {
        public String grandChildField;
    }
    
    static class ClassWithInterfaces implements Serializable, Comparable<ClassWithInterfaces> {
        private static final long serialVersionUID = 1L;
        public String field = "test";
        
        @Override
        public int compareTo(ClassWithInterfaces o) {
            return 0;
        }
    }
    
    static class MultiConstructorClass {
        public String value;
        
        public MultiConstructorClass() {
            this.value = "default";
        }
        
        public MultiConstructorClass(String value) {
            this.value = value;
        }
        
        public MultiConstructorClass(String value, int count) {
            this.value = value + count;
        }
    }
    
    static class ClassWithMethods {
        public String field = "test";
        
        public static void staticMethod() {}
        
        public static String staticMethodWithReturn() {
            return "result";
        }
        
        private static void privateStaticMethod() {}
        
        public void instanceMethod() {}
        
        public String instanceMethodWithReturn() {
            return "result";
        }
        
        private void privateInstanceMethod() {}
        
        protected int protectedMethod(String param) {
            return 0;
        }
    }
    
    static class PrivateFieldsClass {
        private String privateField = "private value";
        private int privateInt = 42;
    }
    
    static class ProtectedFieldsClass {
        protected String protectedField = "protected value";
        protected int protectedInt = 42;
    }
}
