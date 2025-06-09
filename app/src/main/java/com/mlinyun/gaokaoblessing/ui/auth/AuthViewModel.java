package com.mlinyun.gaokaoblessing.ui.auth;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.base.BaseRepository;
import com.mlinyun.gaokaoblessing.data.database.AppDatabase;
import com.mlinyun.gaokaoblessing.data.model.User;
import com.mlinyun.gaokaoblessing.data.repository.UserRepository;
import com.mlinyun.gaokaoblessing.utils.ValidationUtils;

/**
 * 认证ViewModel
 * 处理用户登录、注册相关的业务逻辑
 */
public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    // 当前认证模式（登录/注册）
    private final MutableLiveData<AuthMode> authMode = new MutableLiveData<>(AuthMode.LOGIN);

    // 表单字段
    private final MutableLiveData<String> username = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<String> confirmPassword = new MutableLiveData<>("");
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> rememberPassword = new MutableLiveData<>(false);

    // 表单验证错误
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    private final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();
    private final MutableLiveData<String> emailError = new MutableLiveData<>();

    // 加载状态
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    // 操作结果
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();

    // 密码强度
    private final MutableLiveData<Integer> passwordStrength = new MutableLiveData<>(0);

    public AuthViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        this.userRepository = new UserRepository(database.userDao());
    }

    // Getter methods for LiveData
    public LiveData<AuthMode> getAuthMode() {
        return authMode;
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public LiveData<String> getConfirmPassword() {
        return confirmPassword;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<Boolean> getRememberPassword() {
        return rememberPassword;
    }

    public LiveData<String> getUsernameError() {
        return usernameError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    public LiveData<String> getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }

    public LiveData<Integer> getPasswordStrength() {
        return passwordStrength;
    }

    /**
     * 获取当前是否为登录模式
     */
    public LiveData<Boolean> getIsLoginMode() {
        MutableLiveData<Boolean> isLoginMode = new MutableLiveData<>();
        isLoginMode.setValue(authMode.getValue() == AuthMode.LOGIN);

        // 监听authMode变化并更新isLoginMode
        authMode.observeForever(mode -> {
            isLoginMode.setValue(mode == AuthMode.LOGIN);
        });

        return isLoginMode;
    }

    /**
     * 切换认证模式
     */
    public void switchMode() {
        toggleAuthMode();
    }

    /**
     * 切换认证模式
     */
    public void toggleAuthMode() {
        AuthMode currentMode = authMode.getValue();
        if (currentMode == AuthMode.LOGIN) {
            authMode.setValue(AuthMode.REGISTER);
        } else {
            authMode.setValue(AuthMode.LOGIN);
        }
        clearErrors();
    }

    /**
     * 设置用户名
     */
    public void setUsername(String username) {
        this.username.setValue(username);
        validateUsername(username);
    }

    /**
     * 设置密码
     */
    public void setPassword(String password) {
        this.password.setValue(password);
        validatePassword(password);
        updatePasswordStrength(password);

        // 如果是注册模式，同时验证确认密码
        if (authMode.getValue() == AuthMode.REGISTER) {
            String confirmPwd = confirmPassword.getValue();
            if (confirmPwd != null && !confirmPwd.isEmpty()) {
                validateConfirmPassword(password, confirmPwd);
            }
        }
    }

    /**
     * 设置确认密码
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword.setValue(confirmPassword);
        String pwd = password.getValue();
        if (pwd != null) {
            validateConfirmPassword(pwd, confirmPassword);
        }
    }

    /**
     * 设置邮箱
     */
    public void setEmail(String email) {
        this.email.setValue(email);
        validateEmail(email);
    }

    /**
     * 设置记住密码
     */
    public void setRememberPassword(boolean remember) {
        this.rememberPassword.setValue(remember);
    }

    /**
     * 执行登录
     */
    public void login() {
        String user = username.getValue();
        String pwd = password.getValue();
        Boolean remember = rememberPassword.getValue();

        // 验证登录表单
        ValidationUtils.ValidationResult result = ValidationUtils.validateLoginForm(user, pwd);
        if (!result.isValid()) {
            authResult.setValue(AuthResult.error(result.getErrorMessage()));
            return;
        }

        isLoading.setValue(true);
        LiveData<BaseRepository.Resource<User>> loginResult = userRepository.login(user, pwd, remember != null ? remember : false);

        loginResult.observeForever(resource -> {
            isLoading.setValue(false);

            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null) {
                            authResult.setValue(AuthResult.success(resource.getData(), "登录成功"));
                        } else {
                            authResult.setValue(AuthResult.error("登录失败"));
                        }
                        break;

                    case ERROR:
                        authResult.setValue(AuthResult.error(resource.getMessage()));
                        break;

                    case LOADING:
                        // 加载状态已经通过isLoading处理
                        break;
                }
            }
        });
    }

    /**
     * 执行注册
     */
    public void register() {
        String user = username.getValue();
        String pwd = password.getValue();
        String confirmPwd = confirmPassword.getValue();
        String mail = email.getValue();

        // 验证注册表单
        ValidationUtils.ValidationResult result = ValidationUtils.validateRegisterForm(user, pwd, confirmPwd, mail);
        if (!result.isValid()) {
            authResult.setValue(AuthResult.error(result.getErrorMessage()));
            return;
        }

        isLoading.setValue(true);
        LiveData<BaseRepository.Resource<User>> registerResult = userRepository.register(user, pwd, mail);

        registerResult.observeForever(resource -> {
            isLoading.setValue(false);

            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null) {
                            authResult.setValue(AuthResult.success(resource.getData(), "注册成功"));
                        } else {
                            authResult.setValue(AuthResult.error("注册失败"));
                        }
                        break;

                    case ERROR:
                        authResult.setValue(AuthResult.error(resource.getMessage()));
                        break;

                    case LOADING:
                        // 加载状态已经通过isLoading处理
                        break;
                }
            }
        });
    }

    /**
     * 检查自动登录
     */
    public void checkAutoLogin() {
        isLoading.setValue(true);
        LiveData<BaseRepository.Resource<User>> autoLoginResult = userRepository.checkAutoLogin();

        autoLoginResult.observeForever(resource -> {
            isLoading.setValue(false);

            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null) {
                            authResult.setValue(AuthResult.success(resource.getData(), "自动登录成功"));
                        } else {
                            // 没有有效的自动登录用户，不需要显示错误
                            authResult.setValue(AuthResult.noAutoLogin());
                        }
                        break;

                    case ERROR:
                        authResult.setValue(AuthResult.error(resource.getMessage()));
                        break;

                    case LOADING:
                        // 加载状态已经通过isLoading处理
                        break;
                }
            }
        });
    }

    /**
     * 清空表单
     */
    public void clearForm() {
        username.setValue("");
        password.setValue("");
        confirmPassword.setValue("");
        email.setValue("");
        rememberPassword.setValue(false);
        clearErrors();
        passwordStrength.setValue(0);
    }

    /**
     * 清除错误信息
     */
    public void clearErrors() {
        usernameError.setValue(null);
        passwordError.setValue(null);
        confirmPasswordError.setValue(null);
        emailError.setValue(null);
    }

    /**
     * AuthResult包装类
     * 用于封装认证操作的结果
     */
    public static class AuthResult {
        private final boolean success;
        private final User user;
        private final String message;
        private final AuthResultType type;

        private AuthResult(boolean success, User user, String message, AuthResultType type) {
            this.success = success;
            this.user = user;
            this.message = message;
            this.type = type;
        }

        public static AuthResult success(User user, String message) {
            return new AuthResult(true, user, message, AuthResultType.SUCCESS);
        }

        public static AuthResult error(String message) {
            return new AuthResult(false, null, message, AuthResultType.ERROR);
        }

        public static AuthResult noAutoLogin() {
            return new AuthResult(false, null, null, AuthResultType.NO_AUTO_LOGIN);
        }

        public boolean isSuccess() {
            return success;
        }

        public User getUser() {
            return user;
        }

        public String getMessage() {
            return message;
        }

        public AuthResultType getType() {
            return type;
        }

        public enum AuthResultType {
            SUCCESS, ERROR, NO_AUTO_LOGIN
        }
    }

    /**
     * 认证模式枚举
     */
    public enum AuthMode {
        LOGIN, REGISTER
    }

    // 私有验证方法

    /**
     * 验证用户名
     */
    private void validateUsername(String username) {
        ValidationUtils.ValidationResult result = ValidationUtils.validateUsername(username);
        usernameError.setValue(result.isValid() ? null : result.getErrorMessage());
    }

    /**
     * 验证密码
     */
    private void validatePassword(String password) {
        ValidationUtils.ValidationResult result = ValidationUtils.validatePassword(password);
        passwordError.setValue(result.isValid() ? null : result.getErrorMessage());
    }

    /**
     * 验证确认密码
     */
    private void validateConfirmPassword(String password, String confirmPassword) {
        ValidationUtils.ValidationResult result = ValidationUtils.validateConfirmPassword(password, confirmPassword);
        confirmPasswordError.setValue(result.isValid() ? null : result.getErrorMessage());
    }

    /**
     * 验证邮箱
     */
    private void validateEmail(String email) {
        ValidationUtils.ValidationResult result = ValidationUtils.validateEmail(email);
        emailError.setValue(result.isValid() ? null : result.getErrorMessage());
    }

    /**
     * 更新密码强度
     */
    private void updatePasswordStrength(String password) {
        int strength = ValidationUtils.getPasswordStrength(password);
        passwordStrength.setValue(strength);
    }
}
