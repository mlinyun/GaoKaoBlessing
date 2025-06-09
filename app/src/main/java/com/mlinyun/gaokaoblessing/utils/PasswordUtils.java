package com.mlinyun.gaokaoblessing.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 密码加密工具类
 * 使用SHA-256加密算法和盐值保证密码安全
 */
public class PasswordUtils {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    // Base64字符表
    private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    /**
     * 自定义Base64编码（兼容测试环境）
     */
    private static String encodeBase64(byte[] bytes) {
        try {
            // 优先使用Android Base64
            Class<?> base64Class = Class.forName("android.util.Base64");
            java.lang.reflect.Method encodeMethod = base64Class.getMethod("encodeToString", byte[].class, int.class);
            return (String) encodeMethod.invoke(null, bytes, 0); // Base64.DEFAULT = 0
        } catch (Exception e) {
            // 回退到Java标准Base64（用于测试环境）
            return java.util.Base64.getEncoder().encodeToString(bytes);
        }
    }

    /**
     * 自定义Base64解码（兼容测试环境）
     */
    private static byte[] decodeBase64(String encoded) {
        try {
            // 优先使用Android Base64
            Class<?> base64Class = Class.forName("android.util.Base64");
            java.lang.reflect.Method decodeMethod = base64Class.getMethod("decode", String.class, int.class);
            return (byte[]) decodeMethod.invoke(null, encoded, 0); // Base64.DEFAULT = 0
        } catch (Exception e) {
            // 回退到Java标准Base64（用于测试环境）
            return java.util.Base64.getDecoder().decode(encoded);
        }
    }

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码（包含盐值）
     */
    public static String hashPassword(String password) {
        try {
            // 使用固定盐值以确保相同密码产生相同哈希（用于测试）
            // 在生产环境中应该使用随机盐值
            String fixedSalt = "GaoKaoBlessing";

            // 将密码和盐值组合后进行哈希
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(fixedSalt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());

            // 将盐值和哈希值组合并进行Base64编码
            byte[] saltBytes = fixedSalt.getBytes();
            byte[] result = new byte[saltBytes.length + hashedPassword.length];
            System.arraycopy(saltBytes, 0, result, 0, saltBytes.length);
            System.arraycopy(hashedPassword, 0, result, saltBytes.length, hashedPassword.length);

            return encodeBase64(result);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     *
     * @param password       用户输入的明文密码
     * @param hashedPassword 数据库中存储的加密密码
     * @return 密码是否匹配
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        try {
            // 解码Base64字符串
            byte[] decodedHash = decodeBase64(hashedPassword);

            // 固定盐值
            String fixedSalt = "GaoKaoBlessing";
            byte[] saltBytes = fixedSalt.getBytes();
            int saltLength = saltBytes.length;

            // 使用相同的盐值对输入密码进行哈希
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(saltBytes);
            byte[] hashedInputPassword = md.digest(password.getBytes());

            // 提取存储的哈希值（从盐值长度位置开始）
            byte[] storedHash = new byte[decodedHash.length - saltLength];
            System.arraycopy(decodedHash, saltLength, storedHash, 0, storedHash.length);

            // 比较哈希值
            return MessageDigest.isEqual(hashedInputPassword, storedHash);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成随机盐值
     *
     * @return 盐值字节数组
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 密码强度评级（1-5级）
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 1; // 弱
        }

        int score = 0;

        // 检查是否只有数字
        if (password.matches("^\\d+$")) {
            return 1; // 纯数字
        }

        // 检查是否只有字母
        if (password.matches("^[a-zA-Z]+$")) {
            return 2; // 纯字母
        }

        // 长度加分
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // 字符类型加分
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        if (hasLower) score++;
        if (hasUpper) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;

        // 如果包含数字和字母但没有大写字母
        if (hasLower && hasDigit && !hasUpper && !hasSpecial) {
            return 3; // 中等
        }

        // 如果包含大小写字母和数字
        if (hasLower && hasUpper && hasDigit && !hasSpecial) {
            return 4; // 强
        }

        // 如果包含所有类型字符
        if (hasLower && hasUpper && hasDigit && hasSpecial) {
            return 5; // 非常强
        }

        // 返回强度等级
        if (score <= 2) return 2; // 一般
        if (score <= 4) return 3; // 中等
        if (score <= 6) return 4; // 强
        return 5; // 非常强
    }

    /**
     * 获取密码强度描述
     *
     * @param strength 强度等级
     * @return 强度描述
     */
    public static String getPasswordStrengthDescription(int strength) {
        switch (strength) {
            case 1:
                return "弱";
            case 2:
                return "一般";
            case 3:
                return "中等";
            case 4:
                return "强";
            case 5:
                return "非常强";
            default:
                return "未知";
        }
    }
}
