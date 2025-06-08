package com.mlinyun.gaokaoblessing.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mlinyun.gaokaoblessing.base.BaseFragment;
import com.mlinyun.gaokaoblessing.databinding.FragmentStudentBinding;

/**
 * 学生管理Fragment
 */
public class StudentFragment extends BaseFragment<FragmentStudentBinding, StudentViewModel> {    @Override
protected FragmentStudentBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
    return FragmentStudentBinding.inflate(inflater, container, false);
}

    @Override
    protected Class<StudentViewModel> getViewModelClass() {
        return StudentViewModel.class;
    }    @Override
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
        // TODO: 初始化学生管理数据
    }

    private void setupViews() {
        // TODO: 设置学生管理界面
    }

    private void observeViewModel() {
        // TODO: 观察ViewModel数据变化
    }
}
