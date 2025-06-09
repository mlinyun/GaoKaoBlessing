package com.mlinyun.gaokaoblessing.ui.auth;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.manager.UserSessionManager;
import com.mlinyun.gaokaoblessing.utils.ValidationUtils;

/**
 * 用户认证Fragment - 登录/注册
 * 支持模式切换，表单验证，密码强度显示
 */
public class AuthFragment extends Fragment {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_REMEMBER_PASSWORD = "remember_password";
    private static final String KEY_SAVED_USERNAME = "saved_username";
    private static final String KEY_SAVED_PASSWORD = "saved_password";

    // ViewModeld
    private AuthViewModel viewModel;
    // UI组件
    private TextView titleText;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputLayout emailLayout;
    private TextInputEditText usernameEdit;
    private TextInputEditText passwordEdit;
    private TextInputEditText confirmPasswordEdit;
    private TextInputEditText emailEdit;
    private CheckBox rememberPasswordCheck;
    private Button authButton;
    private TextView switchModeText;
    private TextView forgotPasswordText;
    private LinearLayout passwordStrengthContainer;
    private View passwordStrengthIndicator;
    private TextView passwordStrengthText;
    private LinearLayout thirdPartyContainer;
    private ProgressBar loadingProgress;
    private TextView agreementText;
    private Button backButton;
    private LinearLayout rememberPasswordContainer;

    // 状态管理
    private boolean isLoginMode = true;
    private SharedPreferences sharedPreferences;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModel();
        setupObservers();
        setupListeners();
        loadSavedCredentials();
        updateUIForMode();
    }

    private void initViews(View view) {
        titleText = view.findViewById(R.id.titleText);
        usernameLayout = view.findViewById(R.id.usernameLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        confirmPasswordLayout = view.findViewById(R.id.confirmPasswordLayout);
        emailLayout = view.findViewById(R.id.emailLayout);
        usernameEdit = view.findViewById(R.id.usernameEdit);
        passwordEdit = view.findViewById(R.id.passwordEdit);
        confirmPasswordEdit = view.findViewById(R.id.confirmPasswordEdit);
        emailEdit = view.findViewById(R.id.emailEdit);
        rememberPasswordCheck = view.findViewById(R.id.rememberPasswordCheck);
        authButton = view.findViewById(R.id.authButton);
        switchModeText = view.findViewById(R.id.switchModeText);
        forgotPasswordText = view.findViewById(R.id.forgotPasswordText);
        passwordStrengthContainer = view.findViewById(R.id.passwordStrengthContainer);
        passwordStrengthIndicator = view.findViewById(R.id.passwordStrengthIndicator);
        passwordStrengthText = view.findViewById(R.id.passwordStrengthText);
        thirdPartyContainer = view.findViewById(R.id.thirdPartyContainer);
        loadingProgress = view.findViewById(R.id.loadingProgress);
        agreementText = view.findViewById(R.id.tv_agreement);
        backButton = view.findViewById(R.id.btn_back);
        rememberPasswordContainer = view.findViewById(R.id.ll_remember_password);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(AuthViewModel.class);
    }

    private void setupObservers() {
        // 观察认证状态
        viewModel.getAuthResult().observe(getViewLifecycleOwner(), result -> {
            hideLoading();
            if (result.isSuccess()) {
                handleAuthSuccess(result.getMessage());
            } else {
                handleAuthError(result.getMessage());
            }
        });

        // 观察加载状态
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });
        // 观察验证错误
        viewModel.getUsernameError().observe(getViewLifecycleOwner(), error -> {
            usernameLayout.setError(error);
        });

        viewModel.getPasswordError().observe(getViewLifecycleOwner(), error -> {
            passwordLayout.setError(error);
        });

        viewModel.getConfirmPasswordError().observe(getViewLifecycleOwner(), error -> {
            confirmPasswordLayout.setError(error);
        });

        viewModel.getEmailError().observe(getViewLifecycleOwner(), error -> {
            emailLayout.setError(error);
        });

        // 观察模式切换
        viewModel.getIsLoginMode().observe(getViewLifecycleOwner(), isLogin -> {
            this.isLoginMode = isLogin;
            updateUIForMode();
        });
    }

    private void setupListeners() {
        // 输入框监听
        setupTextWatchers();

        // 按钮点击
        authButton.setOnClickListener(v -> handleAuthButtonClick());

        // 模式切换
        switchModeText.setOnClickListener(v -> switchMode());

        // 忘记密码
        forgotPasswordText.setOnClickListener(v -> handleForgotPassword());        // 记住密码
        rememberPasswordCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setRememberPassword(isChecked);
        });

        // 返回按钮
        backButton.setOnClickListener(v -> handleBackButtonClick());
    }

    private void setupTextWatchers() {
        // 用户名输入监听
        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setUsername(s.toString());
                clearError(usernameLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // 密码输入监听
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPassword(s.toString());
                clearError(passwordLayout);

                // 注册模式下显示密码强度
                if (!isLoginMode) {
                    updatePasswordStrength(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 确认密码输入监听
        confirmPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setConfirmPassword(s.toString());
                clearError(confirmPasswordLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 邮箱输入监听
        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setEmail(s.toString());
                clearError(emailLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void clearError(TextInputLayout layout) {
        if (layout.getError() != null) {
            layout.setError(null);
        }
    }

    private void handleAuthButtonClick() {
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String confirmPassword = confirmPasswordEdit.getText().toString();
        String email = emailEdit.getText().toString();
        boolean rememberPassword = rememberPasswordCheck.isChecked();

        // 设置表单值到ViewModel
        viewModel.setUsername(username);
        viewModel.setPassword(password);
        viewModel.setRememberPassword(rememberPassword);

        if (isLoginMode) {
            viewModel.login();
        } else {
            viewModel.setConfirmPassword(confirmPassword);
            viewModel.setEmail(email);
            viewModel.register();
        }
    }

    private void switchMode() {
        viewModel.switchMode();

        // 清除所有错误信息
        usernameLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        emailLayout.setError(null);

        // 播放切换动画
        animateModeSwitchUI();
    }

    private void updateUIForMode() {
        if (isLoginMode) {
            // 登录模式
            titleText.setText(R.string.login_title);
            authButton.setText(R.string.login);
            switchModeText.setText(R.string.switch_to_register);
            confirmPasswordLayout.setVisibility(View.GONE);
            emailLayout.setVisibility(View.GONE);
            passwordStrengthContainer.setVisibility(View.GONE);
            rememberPasswordContainer.setVisibility(View.VISIBLE); // 显示记住密码容器
            thirdPartyContainer.setVisibility(View.VISIBLE); // 显示第三方登录
            agreementText.setVisibility(View.VISIBLE); // 显示协议文本
        } else {
            // 注册模式
            titleText.setText(R.string.register_title);
            authButton.setText(R.string.register);
            switchModeText.setText(R.string.switch_to_login);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
            emailLayout.setVisibility(View.VISIBLE);
            passwordStrengthContainer.setVisibility(View.VISIBLE);
            rememberPasswordContainer.setVisibility(View.GONE); // 隐藏记住密码容器
            thirdPartyContainer.setVisibility(View.GONE); // 隐藏第三方登录
            agreementText.setVisibility(View.GONE); // 隐藏协议文本
        }
    }

    private void animateModeSwitchUI() {
        // 标题动画
        ObjectAnimator titleAnimator = ObjectAnimator.ofFloat(titleText, "alpha", 0f, 1f);
        titleAnimator.setDuration(300);
        titleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        titleAnimator.start();

        // 按钮动画
        ObjectAnimator buttonAnimator = ObjectAnimator.ofFloat(authButton, "alpha", 0f, 1f);
        buttonAnimator.setDuration(300);
        buttonAnimator.setStartDelay(100);
        buttonAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        buttonAnimator.start();
    }

    private void updatePasswordStrength(String password) {
        int strength = ValidationUtils.getPasswordStrength(password);

        // 更新强度指示器
        float progress = strength / 5.0f;
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0f, progress);
        progressAnimator.setDuration(300);
        progressAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            passwordStrengthIndicator.setScaleX(value);
        });
        progressAnimator.start();

        // 更新强度文本和颜色
        String[] strengthTexts = {
                getString(R.string.password_strength_weak),
                getString(R.string.password_strength_fair),
                getString(R.string.password_strength_good),
                getString(R.string.password_strength_strong),
                getString(R.string.password_strength_very_strong)
        };

        int[] strengthColors = {
                R.color.password_strength_weak,
                R.color.password_strength_fair,
                R.color.password_strength_good,
                R.color.password_strength_strong,
                R.color.password_strength_very_strong
        };

        if (strength > 0) {
            passwordStrengthText.setText(strengthTexts[strength - 1]);
            int color = getResources().getColor(strengthColors[strength - 1], null);
            passwordStrengthIndicator.setBackgroundColor(color);
            passwordStrengthText.setTextColor(color);
        }
    }

    private void handleAuthSuccess(String message) {
        showToast(message);

        if (isLoginMode) {
            // 登录成功 - 保存登录信息到会话管理器
            AuthViewModel.AuthResult result = viewModel.getAuthResult().getValue();
            if (result != null && result.getUser() != null) {
                // 设置用户会话
                UserSessionManager.getInstance(requireContext()).setCurrentUser(result.getUser());
            }

            // 保存登录信息到SharedPreferences（记住密码功能）
            if (rememberPasswordCheck.isChecked()) {
                saveCredentials(usernameEdit.getText().toString(), passwordEdit.getText().toString());
            }

            // 导航到主界面
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToMain();
            } else {
                requireActivity().finish();
            }
        } else {
            // 注册成功 - 切换到登录模式并清空表单
            clearForm();
            viewModel.switchMode(); // 切换到登录模式
            showToast("注册成功！请使用您的账号登录");
        }
    }

    private void handleAuthError(String message) {
        showToast(message);
    }

    private void handleForgotPassword() {
        showToast("忘记密码功能将在后续版本中实现");
    }

    private void handleBackButtonClick() {
        // 返回到启动页面
        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).navigateToStartup();
        } else {
            requireActivity().finish();
        }
    }

    private void showLoading() {
        loadingProgress.setVisibility(View.VISIBLE);
        authButton.setEnabled(false);
        authButton.setAlpha(0.6f);
    }

    private void hideLoading() {
        loadingProgress.setVisibility(View.GONE);
        authButton.setEnabled(true);
        authButton.setAlpha(1.0f);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveCredentials(String username, String password) {
        sharedPreferences.edit()
                .putBoolean(KEY_REMEMBER_PASSWORD, true)
                .putString(KEY_SAVED_USERNAME, username)
                .putString(KEY_SAVED_PASSWORD, password)
                .apply();
    }

    private void loadSavedCredentials() {
        boolean rememberPassword = sharedPreferences.getBoolean(KEY_REMEMBER_PASSWORD, false);
        if (rememberPassword) {
            String savedUsername = sharedPreferences.getString(KEY_SAVED_USERNAME, "");
            String savedPassword = sharedPreferences.getString(KEY_SAVED_PASSWORD, "");

            usernameEdit.setText(savedUsername);
            passwordEdit.setText(savedPassword);
            rememberPasswordCheck.setChecked(true);
        }
    }

    /**
     * 清空表单内容
     */
    private void clearForm() {
        usernameEdit.setText("");
        passwordEdit.setText("");
        confirmPasswordEdit.setText("");
        emailEdit.setText("");

        // 清除所有错误信息
        usernameLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        emailLayout.setError(null);

        // 重置密码强度指示器
        passwordStrengthIndicator.setBackgroundColor(getResources().getColor(R.color.password_strength_weak, null));
        passwordStrengthText.setText(R.string.password_strength_weak);
        passwordStrengthText.setTextColor(getResources().getColor(R.color.password_strength_weak, null));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理资源
        if (viewModel != null) {
            viewModel.clearErrors();
        }
    }
}
