package rt.sentinel.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReflectionUtils 测试类
 */
@DisplayName("ReflectionUtils Tests")
class ReflectionUtilsTest {
    
    // ==================== 字段获取测试 ====================
    
    @Test
    @DisplayName("测试获取所有字段")
    void testGetAllFields() {
        List<Field> fields = ReflectionUtils.getAllFields(ChildClass.class);
        
        assertNotNull(fields);
        assertTrue(fields.size() >= 3); // childField, parentProtected, parentPrivate
        
        // 验证字段名
        boolean hasChildField = fields.stream().anyMatch(f -> f.getName().equals("childField"));
        boolean hasParentProtected = fields.stream().anyMatch(f -> f.getName().equals("parentProtected"));
        boolean hasParentPrivate = fields.stream().anyMatch(f -> f.getName().equals("parentPrivate"));
        
        assertTrue(hasChildField, "应该包含子类字段");
        assertTrue(hasParentProtected, "应该包含父类protected字段");
        assertTrue(hasParentPrivate, "应该包含父类private字段");
    }
    
    @Test
    @DisplayName("测试字段可访问性")
    void testFieldAccessibility() {
        List<Field> fields = ReflectionUtils.getAllFields(PrivateFieldClass.class);
        
        for (Field field : fields) {
            assertTrue(field.canAccess(new PrivateFieldClass()), 
                "所有字段应该可访问: " + field.getName());
        }
    }
    
    @Test
    @DisplayName("测试获取字段值")
    void testGetFieldValue() throws NoSuchFieldException {
        ChildClass obj = new ChildClass();
        obj.childField = "test value";
        
        List<Field> fields = ReflectionUtils.getAllFields(ChildClass.class);
        Field childField = fields.stream()
            .filter(f -> f.getName().equals("childField"))
            .findFirst()
            .orElseThrow();
        
        Object value = ReflectionUtils.getFieldValue(childField, obj);
        assertEquals("test value", value);
    }
    
    // ==================== 方法获取测试 ====================
    
    @Test
    @DisplayName("测试获取所有方法")
    void testGetAllMethods() {
        ReflectionUtils.MethodInfo methodInfo = ReflectionUtils.getAllMethods(MethodTestClass.class);
        
        assertNotNull(methodInfo);
        assertNotNull(methodInfo.getConstructors());
        assertNotNull(methodInfo.getStaticMethods());
        assertNotNull(methodInfo.getInstanceMethods());
    }
    
    @Test
    @DisplayName("测试获取构造函数")
    void testGetConstructors() {
        ReflectionUtils.MethodInfo methodInfo = ReflectionUtils.getAllMethods(MultiConstructorClass.class);
        
        List<Constructor<?>> constructors = methodInfo.getConstructors();
        assertTrue(constructors.size() >= 2, "应该至少有2个构造函数");
    }
    
    @Test
    @DisplayName("测试获取静态方法")
    void testGetStaticMethods() {
        ReflectionUtils.MethodInfo methodInfo = ReflectionUtils.getAllMethods(MethodTestClass.class);
        
        List<Method> staticMethods = methodInfo.getStaticMethods();
        assertTrue(staticMethods.stream().anyMatch(m -> m.getName().equals("staticMethod")));
    }
    
    @Test
    @DisplayName("测试获取实例方法")
    void testGetInstanceMethods() {
        ReflectionUtils.MethodInfo methodInfo = ReflectionUtils.getAllMethods(MethodTestClass.class);
        
        List<Method> instanceMethods = methodInfo.getInstanceMethods();
        assertTrue(instanceMethods.stream().anyMatch(m -> m.getName().equals("instanceMethod")));
    }
    
    @Test
    @DisplayName("测试获取父类方法")
    void testGetParentMethods() {
        ReflectionUtils.MethodInfo methodInfo = ReflectionUtils.getAllMethods(ChildMethodClass.class);
        
        List<Method> instanceMethods = methodInfo.getInstanceMethods();
        // 应该包含父类的方法
        assertTrue(instanceMethods.stream().anyMatch(m -> m.getName().equals("parentMethod")));
    }
    
    // ==================== 接口获取测试 ====================
    
    @Test
    @DisplayName("测试获取所有接口")
    void testGetAllInterfaces() {
        List<Class<?>> interfaces = ReflectionUtils.getAllInterfaces(InterfaceTestClass.class);
        
        assertTrue(interfaces.stream().anyMatch(i -> i == Serializable.class));
        assertTrue(interfaces.stream().anyMatch(i -> i == Comparable.class));
    }
    
    @Test
    @DisplayName("测试获取父类实现的接口")
    void testGetParentInterfaces() {
        List<Class<?>> interfaces = ReflectionUtils.getAllInterfaces(ChildInterfaceClass.class);
        
        // 应该包含父类实现的接口
        assertTrue(interfaces.stream().anyMatch(i -> i == Serializable.class));
    }
    
    @Test
    @DisplayName("测试无接口的类")
    void testNoInterfaces() {
        List<Class<?>> interfaces = ReflectionUtils.getAllInterfaces(NoInterfaceClass.class);
        
        assertTrue(interfaces.isEmpty());
    }
    
    // ==================== 父类获取测试 ====================
    
    @Test
    @DisplayName("测试获取父类")
    void testGetSuperClass() {
        Class<?> superClass = ReflectionUtils.getSuperClass(ChildClass.class);
        assertEquals(ParentClass.class, superClass);
    }
    
    @Test
    @DisplayName("测试Object类的父类")
    void testObjectSuperClass() {
        Class<?> superClass = ReflectionUtils.getSuperClass(Object.class);
        assertNull(superClass);
    }
    
    @Test
    @DisplayName("测试直接继承Object的类")
    void testDirectObjectInheritance() {
        Class<?> superClass = ReflectionUtils.getSuperClass(NoInterfaceClass.class);
        assertEquals(Object.class, superClass);
    }
    
    // ==================== 继承链测试 ====================
    
    @Test
    @DisplayName("测试获取继承链")
    void testGetInheritanceChain() {
        List<Class<?>> chain = ReflectionUtils.getInheritanceChain(GrandChildClass.class);
        
        assertEquals(3, chain.size());
        assertEquals(GrandChildClass.class, chain.get(0));
        assertEquals(ChildClass.class, chain.get(1));
        assertEquals(ParentClass.class, chain.get(2));
    }
    
    // ==================== 测试辅助类 ====================
    
    static class ParentClass {
        private String parentPrivate = "private";
        protected String parentProtected = "protected";
    }
    
    static class ChildClass extends ParentClass {
        public String childField;
    }
    
    static class GrandChildClass extends ChildClass {
        public String grandChildField;
    }
    
    static class PrivateFieldClass {
        private String field1 = "v1";
        private int field2 = 42;
    }
    
    static class MultiConstructorClass {
        public MultiConstructorClass() {}
        public MultiConstructorClass(String s) {}
        public MultiConstructorClass(String s, int i) {}
    }
    
    static class MethodTestClass {
        public static void staticMethod() {}
        private static void privateStaticMethod() {}
        public void instanceMethod() {}
        private void privateInstanceMethod() {}
    }
    
    static class ParentMethodClass {
        public void parentMethod() {}
    }
    
    static class ChildMethodClass extends ParentMethodClass {
        public void childMethod() {}
    }
    
    static class InterfaceTestClass implements Serializable, Comparable<InterfaceTestClass> {
        private static final long serialVersionUID = 1L;
        @Override
        public int compareTo(InterfaceTestClass o) { return 0; }
    }
    
    static class ParentInterfaceClass implements Serializable {
        private static final long serialVersionUID = 1L;
    }
    
    static class ChildInterfaceClass extends ParentInterfaceClass {
    }
    
    static class NoInterfaceClass {
        public String field;
    }
}
