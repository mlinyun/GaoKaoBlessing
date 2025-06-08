package com.mlinyun.gaokaoblessing.ui.startup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.base.BaseViewModel;

/**
 * 启动页ViewModel
 * 处理启动页的业务逻辑
 */
public class StartupViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> _navigateToMain = new MutableLiveData<>();
    public LiveData<Boolean> navigateToMain = _navigateToMain;

    private final MutableLiveData<String> _showToast = new MutableLiveData<>();
    public LiveData<String> showToast = _showToast;

    public LiveData<Boolean> getNavigateToMain() {
        return navigateToMain;
    }

    public LiveData<String> getShowToast() {
        return showToast;
    }

    /**
     * 开始祈福之旅
     */
    public void startJourney() {
        showLoading();

        // 模拟检查用户状态或初始化数据
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            hideLoading();
            // 直接进入主页面（暂时跳过注册登录）
            _navigateToMain.setValue(true);
        }, 500);
    }

    /**
     * 微信快速登录
     */
    public void quickLogin() {
        showLoading();

        // 模拟微信登录检查
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            hideLoading();

            // V2.0版本功能预告
            _showToast.setValue("V2.0版本将支持微信快速登录");

            // 延迟后提示使用其他方式
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                _showToast.setValue("现在先体验应用功能");

                // 再延迟后跳转到主页面
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    _navigateToMain.setValue(true);
                }, 1500);
            }, 2000);
        }, 500);
    }

    /**
     * 检查用户登录状态
     */
    public void checkUserStatus() {
        // TODO: 在后续版本中实现
        // 检查SharedPreferences中的用户登录状态
        // 如果已登录，直接跳转到主页面
    }

    /**
     * 初始化应用数据
     */
    public void initAppData() {
        // TODO: 在后续版本中实现
        // 初始化祈福模板数据
        // 检查数据库版本
        // 预加载必要数据
    }
}
