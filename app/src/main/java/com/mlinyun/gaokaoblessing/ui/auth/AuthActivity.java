package com.mlinyun.gaokaoblessing.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.ui.main.MainActivity;
import com.mlinyun.gaokaoblessing.ui.startup.StartupActivity;

/**
 * 用户认证Activity
 * 承载登录和注册功能
 */
public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏样式
        setupStatusBar();

        setContentView(R.layout.activity_auth);

        // 如果是第一次创建，添加AuthFragment
        if (savedInstanceState == null) {
            addAuthFragment();
        }
    }

    private void setupStatusBar() {
        // 设置状态栏为透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // 设置状态栏文字颜色
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        // 设置状态栏背景色
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.auth_background));
    }

    private void addAuthFragment() {
        Fragment authFragment = AuthFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, authFragment, "AuthFragment");
        transaction.commit();
    }

    /**
     * 处理认证成功后的导航
     */
    public void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // 添加过渡动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 返回到启动页面
     */
    public void navigateToStartup() {
        Intent intent = new Intent(this, StartupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

        // 添加过渡动画
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        // 返回到启动页面而不是退出应用
        super.onBackPressed();
        navigateToStartup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
