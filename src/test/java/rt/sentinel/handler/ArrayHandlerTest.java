package rt.sentinel.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArrayHandler 测试类
 * 
 * 重点测试 byte[] 十六进制输出
 */
@DisplayName("ArrayHandler Tests")
class ArrayHandlerTest {
    
    // ==================== byte[] 十六进制输出测试 ====================
    
    @Test
    @DisplayName("测试byte数组转十六进制 - Hello")
    void testBytesToHexHello() {
        byte[] bytes = {0x48, 0x65, 0x6c, 0x6c, 0x6f}; // "Hello"
        String hex = ArrayHandler.bytesToHex(bytes);
        assertEquals("48656c6c6f", hex);
    }
    
    @Test
    @DisplayName("测试byte数组转十六进制 - 空数组")
    void testBytesToHexEmpty() {
        byte[] bytes = {};
        String hex = ArrayHandler.bytesToHex(bytes);
        assertEquals("", hex);
    }
    
    @Test
    @DisplayName("测试byte数组转十六进制 - null")
    void testBytesToHexNull() {
        String hex = ArrayHandler.bytesToHex(null);
        assertEquals("", hex);
    }
    
    @Test
    @DisplayName("测试byte数组转十六进制 - 单字节")
    void testBytesToHexSingleByte() {
        byte[] bytes = {(byte) 0xFF};
        String hex = ArrayHandler.bytesToHex(bytes);
        assertEquals("ff", hex);
    }
    
    @Test
    @DisplayName("测试byte数组转十六进制 - 所有值")
    void testBytesToHexAllValues() {
        byte[] bytes = {0x00, 0x0F, (byte) 0xF0, (byte) 0xFF};
        String hex = ArrayHandler.bytesToHex(bytes);
        assertEquals("000ff0ff", hex);
    }
    
    @Test
    @DisplayName("测试byte数组转十六进制 - 负值")
    void testBytesToHexNegative() {
        byte[] bytes = {-1, -128}; // 0xFF, 0x80
        String hex = ArrayHandler.bytesToHex(bytes);
        assertEquals("ff80", hex);
    }
    
    @Test
    @DisplayName("测试byte数组转十六进制 - 二进制数据")
    void testBytesToHexBinary() {
        byte[] bytes = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        String hex = ArrayHandler.bytesToHex(bytes);
        assertEquals("0102030405060708", hex);
    }
    
    @Test
    @DisplayName("测试byte数组转十六进制 - 小写输出")
    void testBytesToHexLowercase() {
        byte[] bytes = {(byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
        String hex = ArrayHandler.bytesToHex(bytes);
        assertEquals("abcdef", hex);
        // 确保是小写
        assertEquals(hex.toLowerCase(), hex);
    }
}
