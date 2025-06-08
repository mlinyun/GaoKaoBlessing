package com.mlinyun.gaokaoblessing.ui.wall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mlinyun.gaokaoblessing.base.BaseFragment;
import com.mlinyun.gaokaoblessing.databinding.FragmentWallBinding;

/**
 * 祈福墙Fragment - 显示所有用户的祈福内容
 */
public class WallFragment extends BaseFragment<FragmentWallBinding, WallViewModel> {
    @Override
    protected FragmentWallBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentWallBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<WallViewModel> getViewModelClass() {
        return WallViewModel.class;
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
        // TODO: 初始化祈福墙数据
    }

    private void setupViews() {
        // TODO: 设置祈福墙界面
    }

    private void observeViewModel() {
        // TODO: 观察ViewModel数据变化
    }
}
