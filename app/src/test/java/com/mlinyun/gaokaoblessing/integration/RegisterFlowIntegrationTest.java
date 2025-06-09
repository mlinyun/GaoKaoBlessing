package com.mlinyun.gaokaoblessing.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mlinyun.gaokaoblessing.utils.PasswordUtils;
import com.mlinyun.gaokaoblessing.utils.ValidationUtils;

import org.junit.Test;

/**
 * 注册流程端到端集成测试
 * 验证修复后的注册功能是否完全正常
 */
public class RegisterFlowIntegrationTest {

    @Test
    public void testCompleteRegisterFlow() {
        // 模拟用户输入
        String username = "testuser123";
        String password = "TestPass123!";
        String confirmPassword = "TestPass123!";
        String email = "test@example.com";

        // 1. 验证表单输入
        ValidationUtils.ValidationResult formResult = ValidationUtils.validateRegisterForm(
                username, password, confirmPassword, email);
        assertTrue("注册表单验证应该通过", formResult.isValid());

        // 2. 验证用户名
        ValidationUtils.ValidationResult usernameResult = ValidationUtils.validateUsername(username);
        assertTrue("用户名验证应该通过", usernameResult.isValid());

        // 3. 验证密码
        ValidationUtils.ValidationResult passwordResult = ValidationUtils.validatePassword(password);
        assertTrue("密码验证应该通过", passwordResult.isValid());

        // 4. 验证邮箱
        ValidationUtils.ValidationResult emailResult = ValidationUtils.validateEmail(email);
        assertTrue("邮箱验证应该通过", emailResult.isValid());

        // 5. 测试密码加密（核心修复点）
        String hashedPassword = PasswordUtils.hashPassword(password);
        assertNotNull("密码哈希不应为null", hashedPassword);
        assertNotEquals("哈希密码不应等于原密码", password, hashedPassword);
        assertTrue("哈希密码应有合理长度", hashedPassword.length() > 20);

        // 6. 测试密码验证（核心修复点）
        boolean verificationResult = PasswordUtils.verifyPassword(password, hashedPassword);
        assertTrue("密码验证应该成功", verificationResult);

        // 7. 测试错误密码验证
        boolean wrongPasswordResult = PasswordUtils.verifyPassword("wrongpassword", hashedPassword);
        assertFalse("错误密码验证应该失败", wrongPasswordResult);

        // 8. 测试密码强度
        int strength = ValidationUtils.getPasswordStrength(password);
        assertTrue("密码强度应该合理", strength >= 3);

        System.out.println("✅ 注册流程端到端测试完全通过！");
        System.out.println("   - 表单验证: ✅");
        System.out.println("   - 密码加密: ✅");
        System.out.println("   - 密码验证: ✅");
        System.out.println("   - 密码强度: " + strength + "/5 ✅");
    }

    @Test
    public void testRegisterFlowEdgeCases() {
        // 测试各种边界情况

        // 1. 最短合法密码
        String shortPassword = "Pass1!";
        String hashedShort = PasswordUtils.hashPassword(shortPassword);
        assertTrue("短密码加密应该成功", PasswordUtils.verifyPassword(shortPassword, hashedShort));        // 2. 最长用户名（正好20个字符）
        String longUsername = "user1234567890123456"; // 20字符
        ValidationUtils.ValidationResult longUsernameResult = ValidationUtils.validateUsername(longUsername);
        assertTrue("最长用户名应该通过验证", longUsernameResult.isValid());

        // 3. 特殊字符密码
        String specialPassword = "P@ssw0rd!#$%";
        String hashedSpecial = PasswordUtils.hashPassword(specialPassword);
        assertTrue("特殊字符密码加密应该成功", PasswordUtils.verifyPassword(specialPassword, hashedSpecial));

        // 4. 中文邮箱前缀
        String chineseEmail = "用户@example.com";
        // 注意：根据实际需求决定是否支持中文邮箱

        System.out.println("✅ 注册流程边界测试完全通过！");
    }

    @Test
    public void testPasswordSecurityConsistency() {
        // 测试密码安全性和一致性
        String password = "SecurePass123!";

        // 多次加密同一密码应该产生相同结果（使用固定盐值）
        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);
        String hash3 = PasswordUtils.hashPassword(password);

        assertEquals("多次加密应产生相同结果", hash1, hash2);
        assertEquals("多次加密应产生相同结果", hash2, hash3);

        // 验证应该全部成功
        assertTrue("密码验证1应该成功", PasswordUtils.verifyPassword(password, hash1));
        assertTrue("密码验证2应该成功", PasswordUtils.verifyPassword(password, hash2));
        assertTrue("密码验证3应该成功", PasswordUtils.verifyPassword(password, hash3));

        System.out.println("✅ 密码安全性一致性测试通过！");
    }

    @Test
    public void testMultipleUserRegistrationScenario() {
        // 模拟多用户注册场景
        String[][] users = {
                {"user1", "Password1!", "user1@test.com"},
                {"user2", "SecurePass2@", "user2@test.com"},
                {"user3", "MyPass123#", "user3@test.com"}
        };

        for (int i = 0; i < users.length; i++) {
            String username = users[i][0];
            String password = users[i][1];
            String email = users[i][2];

            // 验证每个用户的注册信息
            ValidationUtils.ValidationResult result = ValidationUtils.validateRegisterForm(
                    username, password, password, email);
            assertTrue("用户" + (i + 1) + "注册信息应该有效", result.isValid());

            // 加密每个用户的密码
            String hashedPassword = PasswordUtils.hashPassword(password);
            assertNotNull("用户" + (i + 1) + "密码加密应该成功", hashedPassword);

            // 验证每个用户的密码
            boolean verified = PasswordUtils.verifyPassword(password, hashedPassword);
            assertTrue("用户" + (i + 1) + "密码验证应该成功", verified);
        }

        System.out.println("✅ 多用户注册场景测试通过！");
    }
}
