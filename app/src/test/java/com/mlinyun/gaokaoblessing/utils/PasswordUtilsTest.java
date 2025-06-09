package com.mlinyun.gaokaoblessing.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * PasswordUtils的单元测试
 */
public class PasswordUtilsTest {

    @Test
    public void testPasswordHashing() {
        String password = "testpassword123";

        // 测试密码哈希
        String hashedPassword = PasswordUtils.hashPassword(password);
        assertNotNull("哈希密码不应为null", hashedPassword);
        assertNotEquals("哈希密码不应等于原密码", password, hashedPassword);
        assertTrue("哈希密码长度应大于0", hashedPassword.length() > 0);

        // 测试同一密码多次哈希结果相同
        String hashedPassword2 = PasswordUtils.hashPassword(password);
        assertEquals("同一密码多次哈希应产生相同结果", hashedPassword, hashedPassword2);
    }

    @Test
    public void testPasswordVerification() {
        String password = "testpassword123";
        String hashedPassword = PasswordUtils.hashPassword(password);

        // 测试正确密码验证
        assertTrue("正确密码应该验证通过", PasswordUtils.verifyPassword(password, hashedPassword));

        // 测试错误密码验证
        assertFalse("错误密码应该验证失败", PasswordUtils.verifyPassword("wrongpassword", hashedPassword));
        assertFalse("null密码应该验证失败", PasswordUtils.verifyPassword(null, hashedPassword));
        assertFalse("空密码应该验证失败", PasswordUtils.verifyPassword("", hashedPassword));
    }

    @Test
    public void testPasswordStrengthChecking() {
        // 测试不同强度的密码
        assertEquals("弱密码强度检测", 1, PasswordUtils.checkPasswordStrength("123456"));
        assertEquals("中等密码强度检测", 3, PasswordUtils.checkPasswordStrength("abc123"));
        assertEquals("强密码强度检测", 5, PasswordUtils.checkPasswordStrength("Abc123!@#"));

        // 测试边界情况
        assertEquals("空密码强度检测", 1, PasswordUtils.checkPasswordStrength(""));
        assertEquals("null密码强度检测", 1, PasswordUtils.checkPasswordStrength(null));
    }

    @Test
    public void testPasswordSaltGeneration() {
        // 如果PasswordUtils有salt生成方法，可以测试
        // 这里假设有generateSalt方法
        // String salt1 = PasswordUtils.generateSalt();
        // String salt2 = PasswordUtils.generateSalt();
        // assertNotEquals("每次生成的salt应该不同", salt1, salt2);
        // assertNotNull("salt不应为null", salt1);
        // assertTrue("salt长度应大于0", salt1.length() > 0);
    }

    @Test
    public void testHashingConsistency() {
        String[] testPasswords = {
                "password123",
                "MySecurePass!",
                "简单密码",
                "1234567890",
                "abcdefghijk",
                "ABCDEFGHIJK",
                "!@#$%^&*()"
        };

        for (String password : testPasswords) {
            String hash1 = PasswordUtils.hashPassword(password);
            String hash2 = PasswordUtils.hashPassword(password);

            assertEquals("密码哈希应该保持一致: " + password, hash1, hash2);
            assertTrue("密码验证应该成功: " + password, PasswordUtils.verifyPassword(password, hash1));
        }
    }

    @Test
    public void testEdgeCases() {
        // 测试极端情况
        String emptyPassword = "";
        String hashedEmpty = PasswordUtils.hashPassword(emptyPassword);
        assertNotNull("空密码哈希不应为null", hashedEmpty);
        assertTrue("空密码应能验证", PasswordUtils.verifyPassword(emptyPassword, hashedEmpty));

        // 测试很长的密码
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longPassword.append("a");
        }
        String longPasswordStr = longPassword.toString();
        String hashedLong = PasswordUtils.hashPassword(longPasswordStr);
        assertNotNull("长密码哈希不应为null", hashedLong);
        assertTrue("长密码应能验证", PasswordUtils.verifyPassword(longPasswordStr, hashedLong));
    }
}
