package com.mlinyun.gaokaoblessing.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 表单验证工具类
 * 提供用户注册和登录表单的验证功能
 */
public class ValidationUtils {

    // 用户名规则：4-20个字符，支持字母、数字、下划线
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    // 密码规则：6-20个字符
    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int PASSWORD_MAX_LENGTH = 20;

    // 邮箱格式验证
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * 验证用户名
     *
     * @param username 用户名
     * @return 验证结果
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("用户名不能为空");
        }

        username = username.trim();

        if (username.length() < 4) {
            return ValidationResult.error("用户名至少需要4个字符");
        }

        if (username.length() > 20) {
            return ValidationResult.error("用户名不能超过20个字符");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return ValidationResult.error("用户名只能包含字母、数字和下划线");
        }

        // 检查是否以数字开头
        if (Character.isDigit(username.charAt(0))) {
            return ValidationResult.error("用户名不能以数字开头");
        }

        return ValidationResult.success();
    }

    /**
     * 验证密码
     *
     * @param password 密码
     * @return 验证结果
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("密码不能为空");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            return ValidationResult.error("密码至少需要" + PASSWORD_MIN_LENGTH + "个字符");
        }

        if (password.length() > PASSWORD_MAX_LENGTH) {
            return ValidationResult.error("密码不能超过" + PASSWORD_MAX_LENGTH + "个字符");
        }

        // 检查是否包含空格
        if (password.contains(" ")) {
            return ValidationResult.error("密码不能包含空格");
        }

        return ValidationResult.success();
    }

    /**
     * 验证邮箱
     *
     * @param email 邮箱地址
     * @return 验证结果
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.error("邮箱不能为空");
        }

        email = email.trim();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationResult.error("邮箱格式不正确");
        }
        if (email.length() > 100) {
            return ValidationResult.error("邮箱地址过长");
        }

        return ValidationResult.success();
    }

    /**
     * 获取密码强度等级 (1-5)
     *
     * @param password 密码
     * @return 强度等级，1=最弱，5=最强
     */
    public static int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 1;
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        // 根据字符类型组合确定强度
        if (hasSpecial) {
            return 5; // 包含特殊字符
        } else if (hasLower && hasUpper && hasDigit) {
            return 4; // 包含大小写字母和数字
        } else if ((hasLower || hasUpper) && hasDigit) {
            return 3; // 包含字母和数字
        } else if (hasLower || hasUpper) {
            return 2; // 纯字母
        } else if (hasDigit) {
            return 1; // 纯数字
        }

        return 1; // 默认最低强度
    }

    /**
     * 验证确认密码
     *
     * @param password        密码
     * @param confirmPassword 确认密码
     * @return 验证结果
     */
    public static ValidationResult validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return ValidationResult.error("请确认密码");
        }

        if (!password.equals(confirmPassword)) {
            return ValidationResult.error("两次输入的密码不一致");
        }

        return ValidationResult.success();
    }

    /**
     * 验证注册表单
     *
     * @param username        用户名
     * @param password        密码
     * @param confirmPassword 确认密码
     * @param email           邮箱
     * @return 验证结果
     */
    public static ValidationResult validateRegisterForm(String username, String password,
                                                        String confirmPassword, String email) {
        List<String> errors = new ArrayList<>();

        // 验证用户名
        ValidationResult usernameResult = validateUsername(username);
        if (!usernameResult.isValid()) {
            errors.add(usernameResult.getErrorMessage());
        }

        // 验证密码
        ValidationResult passwordResult = validatePassword(password);
        if (!passwordResult.isValid()) {
            errors.add(passwordResult.getErrorMessage());
        }

        // 验证确认密码
        ValidationResult confirmPasswordResult = validateConfirmPassword(password, confirmPassword);
        if (!confirmPasswordResult.isValid()) {
            errors.add(confirmPasswordResult.getErrorMessage());
        }

        // 验证邮箱
        ValidationResult emailResult = validateEmail(email);
        if (!emailResult.isValid()) {
            errors.add(emailResult.getErrorMessage());
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.errors(errors);
    }

    /**
     * 验证登录表单
     *
     * @param username 用户名
     * @param password 密码
     * @return 验证结果
     */
    public static ValidationResult validateLoginForm(String username, String password) {
        List<String> errors = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            errors.add("用户名不能为空");
        }

        if (password == null || password.isEmpty()) {
            errors.add("密码不能为空");
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.errors(errors);
    }

    /**
     * 简单用户名验证（用于测试）
     *
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        username = username.trim();

        if (username.length() < 4 || username.length() > 20) {
            return false;
        }

        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 简单密码验证（用于测试）
     *
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        return password.length() >= PASSWORD_MIN_LENGTH &&
                password.length() <= PASSWORD_MAX_LENGTH &&
                !password.contains(" ");
    }

    /**
     * 简单邮箱验证（用于测试）
     *
     * @param email 邮箱地址
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim();
        return EMAIL_PATTERN.matcher(email).matches() && email.length() <= 100;
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final List<String> errors;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.errors = new ArrayList<>();
            if (errorMessage != null) {
                this.errors.add(errorMessage);
            }
        }

        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.errorMessage = errors.isEmpty() ? null : errors.get(0);
        }

        public static ValidationResult success() {
            return new ValidationResult(true, (String) null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public static ValidationResult errors(List<String> errorList) {
            return new ValidationResult(false, errorList);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
    }
}
