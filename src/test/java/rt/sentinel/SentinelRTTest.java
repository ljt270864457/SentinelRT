package rt.sentinel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SentinelRT 主入口测试类
 * 
 * 测试所有公开API方法
 */
@DisplayName("SentinelRT Main API Tests")
class SentinelRTTest {
    
    // ==================== 基本类型测试 ====================
    
    @Test
    @DisplayName("测试null值")
    void testNull() {
        String json = SentinelRT.toJson(null);
        assertEquals("null", json);
    }
    
    @Test
    @DisplayName("测试boolean类型")
    void testBoolean() {
        assertTrue(SentinelRT.toJson(true).contains("true"));
        assertTrue(SentinelRT.toJson(false).contains("false"));
        assertTrue(SentinelRT.toJson(Boolean.TRUE).contains("true"));
    }
    
    @Test
    @DisplayName("测试整数类型")
    void testIntegers() {
        assertTrue(SentinelRT.toJson((byte) 127).contains("127"));
        assertTrue(SentinelRT.toJson((short) 32767).contains("32767"));
        assertTrue(SentinelRT.toJson(2147483647).contains("2147483647"));
        assertTrue(SentinelRT.toJson(9223372036854775807L).contains("9223372036854775807"));
    }
    
    @Test
    @DisplayName("测试浮点数类型")
    void testFloats() {
        assertTrue(SentinelRT.toJson(3.14f).contains("3.14"));
        assertTrue(SentinelRT.toJson(3.14159265359).contains("3.14159"));
    }
    
    @Test
    @DisplayName("测试特殊浮点值")
    void testSpecialFloats() {
        assertTrue(SentinelRT.toJson(Double.NaN).contains("NaN"));
        assertTrue(SentinelRT.toJson(Double.POSITIVE_INFINITY).contains("Infinity"));
        assertTrue(SentinelRT.toJson(Double.NEGATIVE_INFINITY).contains("-Infinity"));
        assertTrue(SentinelRT.toJson(Float.NaN).contains("NaN"));
    }
    
    @Test
    @DisplayName("测试字符类型")
    void testChar() {
        assertTrue(SentinelRT.toJson('A').contains("A"));
        assertTrue(SentinelRT.toJson(Character.valueOf('Z')).contains("Z"));
    }
    
    // ==================== 字符串测试 ====================
    
    @Test
    @DisplayName("测试普通字符串")
    void testString() {
        String json = SentinelRT.toJson("Hello World");
        assertTrue(json.contains("Hello World"));
    }
    
    @Test
    @DisplayName("测试字符串转义")
    void testStringEscape() {
        String json = SentinelRT.toJson("Hello\nWorld\t\"Test\"\\Path");
        assertTrue(json.contains("\\n"));
        assertTrue(json.contains("\\t"));
        assertTrue(json.contains("\\\""));
        assertTrue(json.contains("\\\\"));
    }
    
    @Test
    @DisplayName("测试Unicode字符串")
    void testUnicodeString() {
        String json = SentinelRT.toJson("中文测试");
        assertTrue(json.contains("中文测试"));
    }
    
    // ==================== 数组测试 ====================
    
    @Test
    @DisplayName("测试byte数组十六进制输出")
    void testByteArrayHex() {
        byte[] bytes = {0x48, 0x65, 0x6c, 0x6c, 0x6f}; // "Hello"
        String json = SentinelRT.toJson(bytes);
        assertTrue(json.contains("48656c6c6f"));
    }
    
    @Test
    @DisplayName("测试int数组")
    void testIntArray() {
        int[] arr = {1, 2, 3, 4, 5};
        String json = SentinelRT.toJson(arr);
        assertTrue(json.contains("["));
        assertTrue(json.contains("1"));
        assertTrue(json.contains("5"));
        assertTrue(json.contains("]"));
    }
    
    @Test
    @DisplayName("测试对象数组")
    void testObjectArray() {
        String[] arr = {"a", "b", "c"};
        String json = SentinelRT.toJson(arr);
        assertTrue(json.contains("\"a\""));
        assertTrue(json.contains("\"b\""));
        assertTrue(json.contains("\"c\""));
    }
    
    @Test
    @DisplayName("测试空数组")
    void testEmptyArray() {
        int[] arr = {};
        String json = SentinelRT.toJson(arr);
        assertTrue(json.contains("[]"));
    }
    
    // ==================== 集合测试 ====================
    
    @Test
    @DisplayName("测试ArrayList")
    void testArrayList() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        String json = SentinelRT.toJson(list);
        assertTrue(json.contains("1"));
        assertTrue(json.contains("2"));
        assertTrue(json.contains("3"));
    }
    
    @Test
    @DisplayName("测试HashSet")
    void testHashSet() {
        Set<String> set = new HashSet<>(Arrays.asList("a", "b"));
        String json = SentinelRT.toJson(set);
        assertTrue(json.contains("\"a\"") || json.contains("\"b\""));
    }
    
    @Test
    @DisplayName("测试空集合")
    void testEmptyCollection() {
        List<String> list = new ArrayList<>();
        String json = SentinelRT.toJson(list);
        assertTrue(json.contains("[]"));
    }
    
    // ==================== Map测试 ====================
    
    @Test
    @DisplayName("测试HashMap")
    void testHashMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        String json = SentinelRT.toJson(map);
        assertTrue(json.contains("\"one\""));
        assertTrue(json.contains("1"));
    }
    
    @Test
    @DisplayName("测试空Map")
    void testEmptyMap() {
        Map<String, String> map = new HashMap<>();
        String json = SentinelRT.toJson(map);
        assertTrue(json.contains("{}"));
    }
    
    // ==================== 枚举测试 ====================
    
    enum TestEnum { 
        RUNNING, STOPPED, PAUSED 
    }
    
    @Test
    @DisplayName("测试枚举类型")
    void testEnum() {
        String json = SentinelRT.toJson(TestEnum.RUNNING);
        assertTrue(json.contains("RUNNING"));
    }
    
    // ==================== 日期测试 ====================
    
    @Test
    @DisplayName("测试Date类型")
    void testDate() {
        Date date = new Date(0); // 1970-01-01T00:00:00.000Z
        String json = SentinelRT.toJson(date);
        assertTrue(json.contains("1970-01-01"));
    }
    
    @Test
    @DisplayName("测试Calendar类型")
    void testCalendar() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        String json = SentinelRT.toJson(cal);
        assertTrue(json.contains("1970"));
    }
    
    // ==================== 帮助信息测试 ====================
    
    @Test
    @DisplayName("测试help()方法")
    void testHelp() {
        String help = SentinelRT.help();
        assertNotNull(help);
        assertTrue(help.contains("SentinelRT"));
        assertTrue(help.contains("toJson"));
    }
    
    @Test
    @DisplayName("测试version()方法")
    void testVersion() {
        String version = SentinelRT.version();
        assertNotNull(version);
        assertTrue(version.matches("\\d+\\.\\d+\\.\\d+"));
    }
    
    // ==================== 配置测试 ====================
    
    @Test
    @DisplayName("测试美化输出")
    void testPrettyPrint() {
        SimpleClass obj = new SimpleClass();
        obj.name = "test";
        
        String compact = SentinelRT.toJson(obj, 3, false);
        String pretty = SentinelRT.toJson(obj, 3, true);
        
        // 美化输出应该包含换行
        assertTrue(pretty.contains("\n"));
        assertFalse(compact.contains("\n"));
    }
    
    @Test
    @DisplayName("测试配置JSON")
    void testConfigJson() {
        SimpleClass obj = new SimpleClass();
        obj.name = "test";
        
        String config = "{\"maxDepth\":2,\"pretty\":true,\"includeMeta\":true}";
        String json = SentinelRT.toJsonWithConfig(obj, config);
        
        assertNotNull(json);
        assertTrue(json.contains("\n")); // pretty = true
    }
    
    // ==================== 测试辅助类 ====================
    
    static class SimpleClass {
        public String name;
        private int age = 25;
        protected double score = 98.5;
    }
}
