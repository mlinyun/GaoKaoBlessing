package com.mlinyun.gaokaoblessing.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

/**
 * Fragment基类
 * 提供ViewBinding、ViewModel等基础功能
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
        // 初始化ViewBinding
        mBinding = getViewBinding(inflater, container);
        return mBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化ViewModel
        Class<VM> viewModelClass = getViewModelClass();
        if (AndroidViewModel.class.isAssignableFrom(viewModelClass)) {
            // 创建AndroidViewModel的工厂
            ViewModelProvider.AndroidViewModelFactory factory =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
            mViewModel = new ViewModelProvider(this, factory).get(viewModelClass);
        } else {
            // 普通ViewModel
            mViewModel = new ViewModelProvider(this).get(viewModelClass);
        }

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
     * 获取ViewBinding实例
     */
    protected abstract VB getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    /**
     * 获取ViewModel类
     */
    protected abstract Class<VM> getViewModelClass();

    /**
     * 初始化View
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
     * 显示Toast消息
     */
    protected void showToast(String message) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity<?, ?>) getActivity()).showToast(message);
        }
    }

    /**
     * 显示长Toast消息
     */
    protected void showLongToast(String message) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity<?, ?>) getActivity()).showLongToast(message);
        }
    }
}
