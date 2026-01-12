package rt.sentinel.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonBuilder 测试类
 */
@DisplayName("JsonBuilder Tests")
class JsonBuilderTest {
    
    // ==================== 基本值测试 ====================
    
    @Test
    @DisplayName("测试null值")
    void testNullValue() {
        JsonBuilder builder = new JsonBuilder();
        builder.nullValue();
        assertEquals("null", builder.toString());
    }
    
    @Test
    @DisplayName("测试布尔值true")
    void testBooleanTrue() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(true);
        assertEquals("true", builder.toString());
    }
    
    @Test
    @DisplayName("测试布尔值false")
    void testBooleanFalse() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(false);
        assertEquals("false", builder.toString());
    }
    
    @Test
    @DisplayName("测试整数")
    void testLongValue() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(12345L);
        assertEquals("12345", builder.toString());
    }
    
    @Test
    @DisplayName("测试负整数")
    void testNegativeLong() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(-9999L);
        assertEquals("-9999", builder.toString());
    }
    
    @Test
    @DisplayName("测试浮点数")
    void testDoubleValue() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(3.14159);
        assertTrue(builder.toString().startsWith("3.14"));
    }
    
    @Test
    @DisplayName("测试NaN")
    void testNaN() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(Double.NaN);
        assertEquals("\"NaN\"", builder.toString());
    }
    
    @Test
    @DisplayName("测试正无穷")
    void testPositiveInfinity() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(Double.POSITIVE_INFINITY);
        assertEquals("\"Infinity\"", builder.toString());
    }
    
    @Test
    @DisplayName("测试负无穷")
    void testNegativeInfinity() {
        JsonBuilder builder = new JsonBuilder();
        builder.value(Double.NEGATIVE_INFINITY);
        assertEquals("\"-Infinity\"", builder.toString());
    }
    
    @Test
    @DisplayName("测试字符串")
    void testStringValue() {
        JsonBuilder builder = new JsonBuilder();
        builder.value("hello");
        assertEquals("\"hello\"", builder.toString());
    }
    
    @Test
    @DisplayName("测试null字符串")
    void testNullString() {
        JsonBuilder builder = new JsonBuilder();
        builder.value((String) null);
        assertEquals("null", builder.toString());
    }
    
    // ==================== 字符串转义测试 ====================
    
    @Test
    @DisplayName("测试双引号转义")
    void testEscapeQuote() {
        String result = JsonBuilder.escapeString("say \"hello\"");
        assertEquals("say \\\"hello\\\"", result);
    }
    
    @Test
    @DisplayName("测试反斜杠转义")
    void testEscapeBackslash() {
        String result = JsonBuilder.escapeString("C:\\path\\to\\file");
        assertEquals("C:\\\\path\\\\to\\\\file", result);
    }
    
    @Test
    @DisplayName("测试换行符转义")
    void testEscapeNewline() {
        String result = JsonBuilder.escapeString("line1\nline2");
        assertEquals("line1\\nline2", result);
    }
    
    @Test
    @DisplayName("测试回车符转义")
    void testEscapeCarriageReturn() {
        String result = JsonBuilder.escapeString("line1\rline2");
        assertEquals("line1\\rline2", result);
    }
    
    @Test
    @DisplayName("测试制表符转义")
    void testEscapeTab() {
        String result = JsonBuilder.escapeString("col1\tcol2");
        assertEquals("col1\\tcol2", result);
    }
    
    @Test
    @DisplayName("测试退格符转义")
    void testEscapeBackspace() {
        String result = JsonBuilder.escapeString("text\b");
        assertEquals("text\\b", result);
    }
    
    @Test
    @DisplayName("测试换页符转义")
    void testEscapeFormFeed() {
        String result = JsonBuilder.escapeString("text\f");
        assertEquals("text\\f", result);
    }
    
    @Test
    @DisplayName("测试控制字符转义")
    void testEscapeControlChar() {
        String result = JsonBuilder.escapeString("text\u0001");
        assertEquals("text\\u0001", result);
    }
    
    @Test
    @DisplayName("测试Unicode字符不转义")
    void testUnicodeNotEscaped() {
        String result = JsonBuilder.escapeString("中文测试");
        assertEquals("中文测试", result);
    }
    
    // ==================== 对象构建测试 ====================
    
    @Test
    @DisplayName("测试空对象")
    void testEmptyObject() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginObject().endObject();
        assertEquals("{}", builder.toString());
    }
    
    @Test
    @DisplayName("测试简单对象")
    void testSimpleObject() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginObject()
               .key("name").value("test")
               .endObject();
        assertEquals("{\"name\":\"test\"}", builder.toString());
    }
    
    @Test
    @DisplayName("测试多字段对象")
    void testMultiFieldObject() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginObject()
               .key("name").value("test")
               .key("age").value(25L)
               .key("active").value(true)
               .endObject();
        String json = builder.toString();
        
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"age\":25"));
        assertTrue(json.contains("\"active\":true"));
    }
    
    // ==================== 数组构建测试 ====================
    
    @Test
    @DisplayName("测试空数组")
    void testEmptyArray() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginArray().endArray();
        assertEquals("[]", builder.toString());
    }
    
    @Test
    @DisplayName("测试简单数组")
    void testSimpleArray() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginArray()
               .value(1L)
               .value(2L)
               .value(3L)
               .endArray();
        assertEquals("[1,2,3]", builder.toString());
    }
    
    @Test
    @DisplayName("测试混合类型数组")
    void testMixedArray() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginArray()
               .value("text")
               .value(123L)
               .value(true)
               .nullValue()
               .endArray();
        assertEquals("[\"text\",123,true,null]", builder.toString());
    }
    
    // ==================== 嵌套结构测试 ====================
    
    @Test
    @DisplayName("测试嵌套对象")
    void testNestedObject() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginObject()
               .key("outer")
               .beginObject()
                   .key("inner").value("value")
               .endObject()
               .endObject();
        assertEquals("{\"outer\":{\"inner\":\"value\"}}", builder.toString());
    }
    
    @Test
    @DisplayName("测试对象中的数组")
    void testObjectWithArray() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginObject()
               .key("items")
               .beginArray()
                   .value(1L)
                   .value(2L)
               .endArray()
               .endObject();
        assertEquals("{\"items\":[1,2]}", builder.toString());
    }
    
    @Test
    @DisplayName("测试数组中的对象")
    void testArrayWithObject() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginArray()
               .beginObject()
                   .key("id").value(1L)
               .endObject()
               .beginObject()
                   .key("id").value(2L)
               .endObject()
               .endArray();
        assertEquals("[{\"id\":1},{\"id\":2}]", builder.toString());
    }
    
    // ==================== 美化输出测试 ====================
    
    @Test
    @DisplayName("测试美化输出 - 对象")
    void testPrettyObject() {
        JsonFormatter formatter = new JsonFormatter(2);
        JsonBuilder builder = new JsonBuilder(formatter);
        
        builder.beginObject()
               .key("name").value("test")
               .endObject();
        
        String json = builder.toString();
        assertTrue(json.contains("\n"));
        assertTrue(json.contains("  ")); // 2空格缩进
    }
    
    @Test
    @DisplayName("测试美化输出 - 数组")
    void testPrettyArray() {
        JsonFormatter formatter = new JsonFormatter(2);
        JsonBuilder builder = new JsonBuilder(formatter);
        
        builder.beginArray()
               .value(1L)
               .value(2L)
               .endArray();
        
        String json = builder.toString();
        assertTrue(json.contains("\n"));
    }
    
    // ==================== 原始值测试 ====================
    
    @Test
    @DisplayName("测试原始JSON值")
    void testRawValue() {
        JsonBuilder builder = new JsonBuilder();
        builder.beginObject()
               .key("raw").rawValue("{\"nested\":true}")
               .endObject();
        assertEquals("{\"raw\":{\"nested\":true}}", builder.toString());
    }
    
    // ==================== 重置测试 ====================
    
    @Test
    @DisplayName("测试重置")
    void testReset() {
        JsonBuilder builder = new JsonBuilder();
        builder.value("test");
        assertEquals("\"test\"", builder.toString());
        
        builder.reset();
        assertEquals("", builder.toString());
        assertEquals(0, builder.length());
    }
}
