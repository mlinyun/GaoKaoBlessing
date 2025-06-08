package com.mlinyun.gaokaoblessing.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel 基类
 * 提供通用的加载状态管理和错误处理
 */
public class BaseViewModel extends ViewModel {

    // 加载状态
    protected final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public androidx.lifecycle.LiveData<Boolean> isLoading = _isLoading;

    // 错误信息
    protected final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public androidx.lifecycle.LiveData<String> errorMessage = _errorMessage;

    // 成功消息
    protected final MutableLiveData<String> _successMessage = new MutableLiveData<>();
    public androidx.lifecycle.LiveData<String> successMessage = _successMessage;

    /**
     * 显示加载状态
     */
    protected void showLoading() {
        _isLoading.setValue(true);
    }

    /**
     * 隐藏加载状态
     */
    protected void hideLoading() {
        _isLoading.setValue(false);
    }

    /**
     * 显示错误信息
     */
    protected void showError(String message) {
        _errorMessage.setValue(message);
        hideLoading();
    }

    /**
     * 显示错误信息
     */
    protected void showError(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null || message.isEmpty()) {
            message = "未知错误";
        }
        showError(message);
    }

    /**
     * 显示成功信息
     */
    protected void showSuccess(String message) {
        _successMessage.setValue(message);
        hideLoading();
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        _errorMessage.setValue(null);
    }

    /**
     * 清除成功信息
     */
    public void clearSuccess() {
        _successMessage.setValue(null);
    }
}
