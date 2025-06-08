package com.mlinyun.gaokaoblessing.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mlinyun.gaokaoblessing.base.BaseFragment;
import com.mlinyun.gaokaoblessing.databinding.FragmentProfileBinding;

/**
 * 个人中心Fragment
 */
public class ProfileFragment extends BaseFragment<FragmentProfileBinding, ProfileViewModel> {

    @Override
    protected FragmentProfileBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<ProfileViewModel> getViewModelClass() {
        return ProfileViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        observeViewModel();
    }

    @Override
    protected void initView() {
        setupViews();
    }

    @Override
    protected void initObserver() {
        observeViewModel();
    }

    @Override
    protected void initData() {
        // TODO: 初始化个人中心数据
    }

    private void setupViews() {
        // TODO: 设置个人中心界面
    }

    private void observeViewModel() {
        // TODO: 观察ViewModel数据变化
    }
}
