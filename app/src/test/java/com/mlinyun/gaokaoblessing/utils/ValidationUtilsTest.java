package com.mlinyun.gaokaoblessing.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * ValidationUtils的单元测试
 */
public class ValidationUtilsTest {

    @Test
    public void testUsernameValidation() {
        // 测试有效用户名
        assertTrue("有效用户名应该通过验证", ValidationUtils.isValidUsername("test123"));
        assertTrue("最小长度用户名应该通过验证", ValidationUtils.isValidUsername("test"));
        assertTrue("最大长度用户名应该通过验证", ValidationUtils.isValidUsername("12345678901234567890"));

        // 测试无效用户名
        assertFalse("空用户名应该失败", ValidationUtils.isValidUsername(""));
        assertFalse("null用户名应该失败", ValidationUtils.isValidUsername(null));
        assertFalse("过短用户名应该失败", ValidationUtils.isValidUsername("abc"));
        assertFalse("过长用户名应该失败", ValidationUtils.isValidUsername("123456789012345678901"));
    }

    @Test
    public void testPasswordValidation() {
        // 测试有效密码
        assertTrue("有效密码应该通过验证", ValidationUtils.isValidPassword("password123"));
        assertTrue("最小长度密码应该通过验证", ValidationUtils.isValidPassword("123456"));
        assertTrue("最大长度密码应该通过验证", ValidationUtils.isValidPassword("12345678901234567890"));

        // 测试无效密码
        assertFalse("空密码应该失败", ValidationUtils.isValidPassword(""));
        assertFalse("null密码应该失败", ValidationUtils.isValidPassword(null));
        assertFalse("过短密码应该失败", ValidationUtils.isValidPassword("12345"));
        assertFalse("过长密码应该失败", ValidationUtils.isValidPassword("123456789012345678901"));
    }

    @Test
    public void testEmailValidation() {
        // 测试有效邮箱
        assertTrue("有效邮箱应该通过验证", ValidationUtils.isValidEmail("test@example.com"));
        assertTrue("有效邮箱应该通过验证", ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue("有效邮箱应该通过验证", ValidationUtils.isValidEmail("test123@gmail.com"));

        // 测试无效邮箱
        assertFalse("空邮箱应该失败", ValidationUtils.isValidEmail(""));
        assertFalse("null邮箱应该失败", ValidationUtils.isValidEmail(null));
        assertFalse("无@符号邮箱应该失败", ValidationUtils.isValidEmail("testexample.com"));
        assertFalse("无域名邮箱应该失败", ValidationUtils.isValidEmail("test@"));
        assertFalse("无用户名邮箱应该失败", ValidationUtils.isValidEmail("@example.com"));
    }

    @Test
    public void testPasswordStrength() {
        // 测试密码强度
        assertEquals("纯数字密码强度应为1", 1, ValidationUtils.getPasswordStrength("123456"));
        assertEquals("纯字母密码强度应为2", 2, ValidationUtils.getPasswordStrength("abcdef"));
        assertEquals("数字字母密码强度应为3", 3, ValidationUtils.getPasswordStrength("abc123"));
        assertEquals("包含大小写数字密码强度应为4", 4, ValidationUtils.getPasswordStrength("Abc123"));
        assertEquals("包含特殊字符密码强度应为5", 5, ValidationUtils.getPasswordStrength("Abc123!"));

        // 测试边界情况
        assertEquals("空密码强度应为1", 1, ValidationUtils.getPasswordStrength(""));
        assertEquals("null密码强度应为1", 1, ValidationUtils.getPasswordStrength(null));
    }

    @Test
    public void testFormValidation() {
        // 测试登录表单验证
        ValidationUtils.ValidationResult loginResult = ValidationUtils.validateLoginForm("testuser", "password123");
        assertTrue("有效登录表单应该通过验证", loginResult.isValid());

        ValidationUtils.ValidationResult invalidLoginResult = ValidationUtils.validateLoginForm("", "");
        assertFalse("空登录表单应该失败", invalidLoginResult.isValid());
        assertTrue("应该包含错误信息", invalidLoginResult.getErrors().size() > 0);

        // 测试注册表单验证
        ValidationUtils.ValidationResult registerResult = ValidationUtils.validateRegisterForm(
                "testuser", "password123", "password123", "test@example.com");
        assertTrue("有效注册表单应该通过验证", registerResult.isValid());

        ValidationUtils.ValidationResult mismatchResult = ValidationUtils.validateRegisterForm(
                "testuser", "password123", "password456", "test@example.com");
        assertFalse("密码不匹配注册表单应该失败", mismatchResult.isValid());
    }
}
