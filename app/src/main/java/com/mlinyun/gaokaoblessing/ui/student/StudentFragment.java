package com.mlinyun.gaokaoblessing.ui.student;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.mlinyun.gaokaoblessing.base.BaseFragment;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.databinding.FragmentStudentBinding;
import com.mlinyun.gaokaoblessing.ui.student.adapter.StudentAdapter;
import com.mlinyun.gaokaoblessing.ui.student.dialog.AddStudentDialog;

/**
 * 学生管理Fragment
 */
public class StudentFragment extends BaseFragment<FragmentStudentBinding, StudentViewModel>
        implements StudentAdapter.OnStudentActionListener, AddStudentDialog.OnStudentSaveListener {

    private StudentAdapter studentAdapter;
    private boolean isFilterVisible = false;

    @Override
    protected FragmentStudentBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentStudentBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<StudentViewModel> getViewModelClass() {
        return StudentViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        observeViewModel();
    }

    @Override
    protected void initView() {
        setupRecyclerView();
        setupSearchView();
        setupClickListeners();
    }

    @Override
    protected void initObserver() {
        observeViewModel();
    }

    @Override
    protected void initData() {
        // TODO: 初始化学生管理数据 - 在ViewModel中通过Repository加载
    }

    private void setupViews() {
        setupRecyclerView();
        setupSearchView();
        setupClickListeners();
        setupFilterChips();
    }

    private void setupRecyclerView() {
        studentAdapter = new StudentAdapter(getContext());
        studentAdapter.setOnStudentActionListener(this);

        mBinding.rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvStudents.setAdapter(studentAdapter);
    }

    private void setupSearchView() {
        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.searchStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupClickListeners() {
        // 添加学生按钮
        mBinding.fabAddStudent.setOnClickListener(v -> showAddStudentDialog());

        // 筛选按钮
        mBinding.ivFilter.setOnClickListener(v -> toggleFilterVisibility());
    }

    private void setupFilterChips() {
        mBinding.chipAll.setOnClickListener(v -> {
            selectFilterChip(mBinding.chipAll);
            mViewModel.showFollowedStudents(false);
        });

        mBinding.chipFollowed.setOnClickListener(v -> {
            selectFilterChip(mBinding.chipFollowed);
            mViewModel.showFollowedStudents(true);
        });
    }

    private void selectFilterChip(Chip selectedChip) {
        mBinding.chipAll.setChecked(false);
        mBinding.chipFollowed.setChecked(false);
        selectedChip.setChecked(true);
    }

    private void toggleFilterVisibility() {
        isFilterVisible = !isFilterVisible;
        mBinding.hsvFilters.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);

        // 旋转筛选图标
        float rotation = isFilterVisible ? 180f : 0f;
        mBinding.ivFilter.animate().rotation(rotation).setDuration(200).start();
    }

    private void observeViewModel() {
        // 观察学生列表
        mViewModel.getStudents().observe(getViewLifecycleOwner(), students -> {
            if (students != null) {
                studentAdapter.submitList(students);
                updateEmptyState(students.isEmpty());
            }
        });

        // 观察学生数量
        mViewModel.getStudentCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                String countText = String.format("共有 %d 位考生", count);
                mBinding.tvStudentCount.setText(countText);
            }
        });

        // 观察Toast消息
        mViewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // 观察添加对话框显示
        mViewModel.getShowAddDialog().observe(getViewLifecycleOwner(), show -> {
            if (show != null && show) {
                showAddStudentDialog();
            }
        });

        // 观察编辑学生
        mViewModel.getEditingStudent().observe(getViewLifecycleOwner(), student -> {
            if (student != null) {
                showEditStudentDialog(student);
            }
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        mBinding.llEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mBinding.rvStudents.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showAddStudentDialog() {
        AddStudentDialog dialog = AddStudentDialog.newInstance();
        dialog.setOnStudentSaveListener(this);
        dialog.show(getChildFragmentManager(), "AddStudentDialog");
    }

    private void showEditStudentDialog(Student student) {
        AddStudentDialog dialog = AddStudentDialog.newInstance(student);
        dialog.setOnStudentSaveListener(this);
        dialog.show(getChildFragmentManager(), "EditStudentDialog");
    }

    private void showDeleteConfirmDialog(Student student) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除考生")
                .setMessage(String.format("确定要删除考生\"%s\"吗？\n删除后无法恢复。", student.getName()))
                .setPositiveButton("删除", (dialog, which) -> mViewModel.deleteStudent(student))
                .setNegativeButton("取消", null)
                .show();
    }

    // StudentAdapter.OnStudentActionListener 实现
    @Override
    public void onStudentClick(Student student) {
        // TODO: 打开学生详情页面
        Toast.makeText(getContext(), "点击了 " + student.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFollowClick(Student student, boolean isFollowed) {
        mViewModel.toggleFollowStatus(student);
    }

    @Override
    public void onEditClick(Student student) {
        showEditStudentDialog(student);
    }

    @Override
    public void onDeleteClick(Student student) {
        showDeleteConfirmDialog(student);
    }

    @Override
    public void onBlessClick(Student student) {
        // TODO: 打开祝福页面
        Toast.makeText(getContext(), "为 " + student.getName() + " 送上祝福", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewBlessingsClick(Student student) {
        // TODO: 查看历史祝福记录
        Toast.makeText(getContext(), "查看 " + student.getName() + " 的祝福记录", Toast.LENGTH_SHORT).show();
    }

    // AddStudentDialog.OnStudentSaveListener 实现
    @Override
    public void onStudentAdd(String name, String school, String className, String subjectType, int graduationYear, String avatarUrl) {
        mViewModel.addStudent(name, school, className, subjectType, graduationYear, avatarUrl);
    }

    @Override
    public void onStudentUpdate(Student student) {
        mViewModel.updateStudent(student);
    }
}
