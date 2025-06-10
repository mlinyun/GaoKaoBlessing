package com.mlinyun.gaokaoblessing.ui.blessing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.databinding.ActivityCreateBlessingBinding;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.AdvancedBlessingTemplateAdapter;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.StudentSelectionAdapter;
import com.mlinyun.gaokaoblessing.ui.blessing.viewmodel.CreateBlessingViewModel;

/**
 * 创建祈福Activity - 按照UI原型图实现
 * 包含多步骤流程：学生选择、模板选择、自定义内容、特效选择
 */
public class CreateBlessingActivity extends AppCompatActivity {

    private ActivityCreateBlessingBinding binding;
    private CreateBlessingViewModel viewModel;
    // 适配器
    private StudentSelectionAdapter studentAdapter;
    private AdvancedBlessingTemplateAdapter templateAdapter;

    // 当前步骤
    private int currentStep = 0;
    private static final int STEP_SELECT_STUDENT = 0;
    private static final int STEP_SELECT_TEMPLATE = 1;
    private static final int STEP_CUSTOM_CONTENT = 2;
    private static final int STEP_SELECT_EFFECT = 3;

    // 选择的数据
    private Student selectedStudent;
    private String selectedTemplate;
    private String customContent;
    private String selectedEffect = "默认";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBlessingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        initViews();
        initObservers();
        loadInitialData();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(CreateBlessingViewModel.class);
    }

    private void initViews() {
        // 设置状态栏
        getWindow().setStatusBarColor(getColor(R.color.blessing_primary_dark));

        // 初始化顶部导航
        binding.btnBack.setOnClickListener(v -> handleBackPressed());
        binding.btnHelp.setOnClickListener(v -> showHelpDialog());

        // 初始化步骤指示器
        updateStepIndicator();

        // 初始化学生选择适配器
        studentAdapter = new StudentSelectionAdapter();
        studentAdapter.setOnStudentSelectedListener(this::onStudentSelected);
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudents.setAdapter(studentAdapter);
        // 初始化模板选择适配器
        templateAdapter = new AdvancedBlessingTemplateAdapter();
        templateAdapter.setOnTemplateSelectedListener(this::onTemplateSelected);
        binding.rvTemplates.setLayoutManager(new GridLayoutManager(this, 1));
        binding.rvTemplates.setAdapter(templateAdapter);

        // 初始化模板分类标签
        initTemplateCategories();

        // 初始化特效选择
        initEffectSelection();

        // 底部操作按钮
        binding.btnPrevious.setOnClickListener(v -> previousStep());
        binding.btnNext.setOnClickListener(v -> nextStep());
        binding.btnComplete.setOnClickListener(v -> completeBlessing());

        // 添加学生按钮
        binding.btnAddStudent.setOnClickListener(v -> addNewStudent());

        // 显示第一步
        showStep(STEP_SELECT_STUDENT);
    }

    private void initObservers() {
        // 观察学生列表
        viewModel.getStudents().observe(this, students -> {
            if (students != null) {
                studentAdapter.updateStudents(students);
            }
        });

        // 观察祈福模板
        viewModel.getTemplates().observe(this, templates -> {
            if (templates != null) {
                templateAdapter.updateTemplates(templates);
            }
        });

        // 观察创建结果
        viewModel.getCreateResult().observe(this, result -> {
            if (result != null && result) {
                Toast.makeText(this, "祈福创建成功！", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });

        // 观察错误信息
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // 观察加载状态
        viewModel.getLoading().observe(this, loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }

    private void loadInitialData() {
        viewModel.loadStudents();
        viewModel.loadDefaultTemplates();
    }

    private void initTemplateCategories() {
        // 添加模板分类标签
        String[] categories = {"全部", "学业", "健康", "平安", "成功", "鼓励"};

        for (String category : categories) {
            TabLayout.Tab tab = binding.tlTemplateCategories.newTab();
            tab.setText(category);
            binding.tlTemplateCategories.addTab(tab);
        }

        binding.tlTemplateCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText().toString();
                if ("全部".equals(category)) {
                    viewModel.loadDefaultTemplates();
                } else {
                    viewModel.loadTemplatesByCategory(category);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initEffectSelection() {
        // 初始化特效选择
        String[] effects = {"默认", "金光闪闪", "彩虹特效", "星光点点", "花瓣飞舞"};

        for (String effect : effects) {
            View effectView = getLayoutInflater().inflate(R.layout.item_effect_selection, binding.llEffects, false);
            // 设置特效选择逻辑
            effectView.setOnClickListener(v -> {
                selectedEffect = effect;
                updateEffectSelection();
            });
            binding.llEffects.addView(effectView);
        }
    }

    private void showStep(int step) {
        currentStep = step;
        updateStepIndicator();

        // 隐藏所有步骤视图
        binding.layoutSelectStudent.setVisibility(View.GONE);
        binding.layoutSelectTemplate.setVisibility(View.GONE);
        binding.layoutCustomContent.setVisibility(View.GONE);
        binding.layoutSelectEffect.setVisibility(View.GONE);

        // 显示当前步骤视图
        switch (step) {
            case STEP_SELECT_STUDENT:
                binding.layoutSelectStudent.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("选择考生");
                binding.tvStepDescription.setText("请选择要祈福的考生");
                break;
            case STEP_SELECT_TEMPLATE:
                binding.layoutSelectTemplate.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("选择祈福模板");
                binding.tvStepDescription.setText("选择合适的祈福模板或自定义内容");
                break;
            case STEP_CUSTOM_CONTENT:
                binding.layoutCustomContent.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("自定义祈福内容");
                binding.tvStepDescription.setText("编辑您的祈福内容");
                // 预填充选择的模板内容
                if (selectedTemplate != null) {
                    binding.etCustomContent.setText(selectedTemplate);
                }
                break;
            case STEP_SELECT_EFFECT:
                binding.layoutSelectEffect.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("选择特效");
                binding.tvStepDescription.setText("为您的祈福添加特效");
                break;
        }

        // 更新按钮状态
        binding.btnPrevious.setVisibility(step > 0 ? View.VISIBLE : View.GONE);
        binding.btnNext.setVisibility(step < STEP_SELECT_EFFECT ? View.VISIBLE : View.GONE);
        binding.btnComplete.setVisibility(step == STEP_SELECT_EFFECT ? View.VISIBLE : View.GONE);
    }

    private void updateStepIndicator() {
        // 更新步骤指示器
        for (int i = 0; i < binding.layoutStepIndicator.getChildCount(); i++) {
            View stepView = binding.layoutStepIndicator.getChildAt(i);
            if (i < currentStep) {
                stepView.setBackgroundResource(R.drawable.step_completed);
            } else if (i == currentStep) {
                stepView.setBackgroundResource(R.drawable.step_active);
            } else {
                stepView.setBackgroundResource(R.drawable.step_inactive);
            }
        }
    }

    private void updateEffectSelection() {
        // 更新特效选择状态
        for (int i = 0; i < binding.llEffects.getChildCount(); i++) {
            View effectView = binding.llEffects.getChildAt(i);
            effectView.setSelected(false);
            // 根据selectedEffect设置选中状态
        }
    }

    private void onStudentSelected(Student student) {
        selectedStudent = student;
        binding.btnNext.setEnabled(true);
    }

    private void onTemplateSelected(String template) {
        selectedTemplate = template;
        binding.btnNext.setEnabled(true);
    }

    private void previousStep() {
        if (currentStep > 0) {
            showStep(currentStep - 1);
        }
    }

    private void nextStep() {
        if (!validateCurrentStep()) {
            return;
        }

        if (currentStep == STEP_CUSTOM_CONTENT) {
            customContent = binding.etCustomContent.getText().toString().trim();
        }

        if (currentStep < STEP_SELECT_EFFECT) {
            showStep(currentStep + 1);
        }
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case STEP_SELECT_STUDENT:
                if (selectedStudent == null) {
                    Toast.makeText(this, "请选择考生", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case STEP_SELECT_TEMPLATE:
                if (selectedTemplate == null || selectedTemplate.isEmpty()) {
                    Toast.makeText(this, "请选择祈福模板", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case STEP_CUSTOM_CONTENT:
                String content = binding.etCustomContent.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(this, "请输入祈福内容", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
        }
        return true;
    }

    private void completeBlessing() {
        if (!validateCurrentStep()) {
            return;
        }

        // 获取最终的祈福内容
        String finalContent = binding.etCustomContent.getText().toString().trim();
        if (finalContent.isEmpty()) {
            finalContent = selectedTemplate;
        }

        // 创建祈福记录
        viewModel.createBlessing(selectedStudent, finalContent, selectedEffect);
    }

    private void addNewStudent() {
        // 通过对话框添加学生
        com.mlinyun.gaokaoblessing.ui.student.dialog.AddStudentDialog dialog =
                new com.mlinyun.gaokaoblessing.ui.student.dialog.AddStudentDialog();
        dialog.setOnStudentSaveListener(new com.mlinyun.gaokaoblessing.ui.student.dialog.AddStudentDialog.OnStudentSaveListener() {
            @Override
            public void onStudentAdd(String name, String school, String className, String subjectType, int graduationYear, String avatarUrl) {
                // 添加学生成功后刷新学生列表
                viewModel.loadStudents();
            }

            @Override
            public void onStudentUpdate(com.mlinyun.gaokaoblessing.data.entity.Student student) {
                // 更新学生成功后刷新学生列表
                viewModel.loadStudents();
            }
        });
        dialog.show(getSupportFragmentManager(), "AddStudentDialog");
    }

    private void showHelpDialog() {
        // 显示帮助对话框
        Toast.makeText(this, "帮助功能开发中...", Toast.LENGTH_SHORT).show();
    }

    private void handleBackPressed() {
        if (currentStep > 0) {
            previousStep();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // 重新加载学生列表
            viewModel.loadStudents();
        }
    }
}
