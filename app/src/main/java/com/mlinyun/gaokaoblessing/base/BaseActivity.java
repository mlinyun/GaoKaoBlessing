package com.mlinyun.gaokaoblessing.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

/**
 * Activity 基类
 * 提供 ViewBinding、ViewModel 等基础功能
 */
public abstract class BaseActivity<VB extends ViewBinding, VM extends ViewModel> extends AppCompatActivity {

    protected VB mBinding;
    protected VM mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化 ViewBinding
        mBinding = getViewBinding();
        setContentView(mBinding.getRoot());

        // 初始化 ViewModel
        mViewModel = new ViewModelProvider(this).get(getViewModelClass());

        // 初始化UI
        initView();

        // 设置观察者
        initObserver();

        // 初始化数据
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinding != null) {
            mBinding = null;
        }
    }

    /**
     * 获取 ViewBinding 实例
     */
    protected abstract VB getViewBinding();

    /**
     * 获取 ViewModel 类
     */
    protected abstract Class<VM> getViewModelClass();

    /**
     * 初始化 View
     */
    protected abstract void initView();

    /**
     * 初始化数据观察者
     */
    protected abstract void initObserver();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 显示加载对话框
     */
    protected void showLoading() {
        // TODO: 实现加载对话框
    }

    /**
     * 隐藏加载对话框
     */
    protected void hideLoading() {
        // TODO: 隐藏加载对话框
    }

    /**
     * 显示 Toast 消息
     */
    protected void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长 Toast 消息
     */
    protected void showLongToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show();
    }
}
