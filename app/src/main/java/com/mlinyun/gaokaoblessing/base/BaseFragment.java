package com.mlinyun.gaokaoblessing.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

/**
 * Fragment 基类
 * 提供 ViewBinding、ViewModel 等基础功能
 */
public abstract class BaseFragment<VB extends ViewBinding, VM extends ViewModel> extends Fragment {

    protected VB mBinding;
    protected VM mViewModel;
    protected Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 初始化 ViewBinding
        mBinding = getViewBinding(inflater, container);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public void onDestroyView() {
        super.onDestroyView();
        if (mBinding != null) {
            mBinding = null;
        }
    }

    /**
     * 获取 ViewBinding 实例
     */
    protected abstract VB getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

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
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity<?, ?>) getActivity()).showLoading();
        }
    }

    /**
     * 隐藏加载对话框
     */
    protected void hideLoading() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity<?, ?>) getActivity()).hideLoading();
        }
    }

    /**
     * 显示 Toast 消息
     */
    protected void showToast(String message) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity<?, ?>) getActivity()).showToast(message);
        }
    }

    /**
     * 显示长 Toast 消息
     */
    protected void showLongToast(String message) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity<?, ?>) getActivity()).showLongToast(message);
        }
    }
}
