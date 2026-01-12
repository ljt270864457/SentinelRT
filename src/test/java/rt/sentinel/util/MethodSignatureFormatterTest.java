package rt.sentinel.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MethodSignatureFormatter 测试类
 */
@DisplayName("MethodSignatureFormatter Tests")
class MethodSignatureFormatterTest {
    
    // ==================== 方法签名格式化测试 ====================
    
    @Test
    @DisplayName("测试公共方法签名")
    void testPublicMethodSignature() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("publicMethod");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("public"));
        assertTrue(signature.contains("void"));
        assertTrue(signature.contains("publicMethod"));
        assertTrue(signature.contains("()"));
    }
    
    @Test
    @DisplayName("测试私有方法签名")
    void testPrivateMethodSignature() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("privateMethod");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("private"));
    }
    
    @Test
    @DisplayName("测试静态方法签名")
    void testStaticMethodSignature() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("staticMethod");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("static"));
    }
    
    @Test
    @DisplayName("测试带返回值的方法签名")
    void testMethodWithReturn() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("methodWithReturn");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("String"));
        assertTrue(signature.contains("methodWithReturn"));
    }
    
    @Test
    @DisplayName("测试带参数的方法签名")
    void testMethodWithParams() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("methodWithParams", String.class, int.class);
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("String"));
        assertTrue(signature.contains("int"));
    }
    
    @Test
    @DisplayName("测试数组返回类型")
    void testArrayReturnType() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("arrayReturnMethod");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("byte[][]"));
    }
    
    @Test
    @DisplayName("测试数组参数类型")
    void testArrayParamType() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("arrayParamMethod", int[].class);
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("int[]"));
    }
    
    @Test
    @DisplayName("测试List参数类型")
    void testListParamType() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("listParamMethod", List.class);
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("List"));
    }
    
    // ==================== 构造函数签名格式化测试 ====================
    
    @Test
    @DisplayName("测试无参构造函数签名")
    void testNoArgConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = TestClass.class.getDeclaredConstructor();
        String signature = MethodSignatureFormatter.format(constructor);
        
        assertTrue(signature.contains("TestClass"));
        assertTrue(signature.contains("()"));
    }
    
    @Test
    @DisplayName("测试有参构造函数签名")
    void testArgConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = TestClass.class.getDeclaredConstructor(String.class);
        String signature = MethodSignatureFormatter.format(constructor);
        
        assertTrue(signature.contains("TestClass"));
        assertTrue(signature.contains("String"));
    }
    
    @Test
    @DisplayName("测试多参数构造函数签名")
    void testMultiArgConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = TestClass.class.getDeclaredConstructor(String.class, int.class, double.class);
        String signature = MethodSignatureFormatter.format(constructor);
        
        assertTrue(signature.contains("String"));
        assertTrue(signature.contains("int"));
        assertTrue(signature.contains("double"));
    }
    
    // ==================== 方法修饰符测试 ====================
    
    @Test
    @DisplayName("测试synchronized方法签名")
    void testSynchronizedMethod() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("synchronizedMethod");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("synchronized"));
    }
    
    @Test
    @DisplayName("测试final方法签名")
    void testFinalMethod() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("finalMethod");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("final"));
    }
    
    @Test
    @DisplayName("测试protected方法签名")
    void testProtectedMethod() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("protectedMethod");
        String signature = MethodSignatureFormatter.format(method);
        
        assertTrue(signature.contains("protected"));
    }
    
    // ==================== 简短签名测试 ====================
    
    @Test
    @DisplayName("测试简短签名")
    void testShortSignature() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("methodWithParams", String.class, int.class);
        String signature = MethodSignatureFormatter.formatShort(method);
        
        // 简短签名不应包含修饰符
        assertFalse(signature.contains("public"));
        assertTrue(signature.contains("void"));
        assertTrue(signature.contains("methodWithParams"));
        assertTrue(signature.contains("String"));
        assertTrue(signature.contains("int"));
    }
    
    // ==================== 测试辅助类 ====================
    
    static class TestClass {
        
        public TestClass() {}
        
        public TestClass(String s) {}
        
        public TestClass(String s, int i, double d) {}
        
        public void publicMethod() {}
        
        private void privateMethod() {}
        
        public static void staticMethod() {}
        
        public String methodWithReturn() {
            return "";
        }
        
        public void methodWithParams(String s, int i) {}
        
        public byte[][] arrayReturnMethod() {
            return null;
        }
        
        public void arrayParamMethod(int[] arr) {}
        
        public void listParamMethod(List<String> list) {}
        
        public synchronized void synchronizedMethod() {}
        
        public final void finalMethod() {}
        
        protected void protectedMethod() {}
    }
}
