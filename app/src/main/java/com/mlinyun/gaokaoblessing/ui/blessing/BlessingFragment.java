package com.mlinyun.gaokaoblessing.ui.blessing;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mlinyun.gaokaoblessing.base.BaseFragment;
import com.mlinyun.gaokaoblessing.databinding.FragmentBlessingBinding;

/**
 * 祈福主页Fragment
 */
public class BlessingFragment extends BaseFragment<FragmentBlessingBinding, BlessingViewModel> {

    @Override
    protected FragmentBlessingBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentBlessingBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<BlessingViewModel> getViewModelClass() {
        return BlessingViewModel.class;
    }

    @Override
    protected void initView() {
        // 初始化祈福相关UI组件
        setupBlessingUI();
    }

    @Override
    protected void initObserver() {
        // 观察祈福数据变化
        mViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        mViewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showToast(errorMessage);
                mViewModel.clearError();
            }
        });
    }

    @Override
    protected void initData() {
        // 加载祈福数据
        mViewModel.loadBlessingData();
    }

    private void setupBlessingUI() {
        // TODO: 设置祈福界面UI组件
        mBinding.tvTitle.setText("高考祈福");
    }
}
