package com.mlinyun.gaokaoblessing.ui.blessing;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.base.BaseFragment;
import com.mlinyun.gaokaoblessing.data.model.User;
import com.mlinyun.gaokaoblessing.databinding.FragmentBlessingBinding;
import com.mlinyun.gaokaoblessing.manager.UserSessionManager;
import com.mlinyun.gaokaoblessing.ui.auth.AuthActivity;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.BlessingTemplateAdapter;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.RecentBlessingsAdapter;
import com.mlinyun.gaokaoblessing.ui.blessing.view.FloatingParticleView;
import com.mlinyun.gaokaoblessing.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 祈福主页Fragment - 按照UI原型图实现
 * 包含顶部导航、欢迎区域、快速操作、最近祈福记录等功能
 */
public class BlessingFragment extends BaseFragment<FragmentBlessingBinding, BlessingViewModel> {
    private RecentBlessingsAdapter recentBlessingsAdapter;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private UserSessionManager userSessionManager;
    private FloatingParticleView particleView;

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
        // 初始化用户会话管理器
        userSessionManager = UserSessionManager.getInstance(requireContext());
        setupTopNavigation();
        setupWelcomeArea();
        setupQuickActions();
        setupRecentBlessings();
        setupFloatingActionButton();
        setupParticleEffect();
        startPeriodicUpdates();
    }
    @Override
    protected void initObserver() {
        // 观察祈福数据变化
        mViewModel.isLoading.observe(getViewLifecycleOwner(), this::handleLoadingState);

        mViewModel.errorMessage.observe(getViewLifecycleOwner(), this::handleErrorMessage);

        // 观察祈福记录变化
        mViewModel.recentBlessings.observe(getViewLifecycleOwner(), blessings -> {
            if (blessings != null && recentBlessingsAdapter != null) {
                recentBlessingsAdapter.updateBlessings(blessings);
                updateBlessingStats(blessings.size());
            }
        });

        // 观察学生数据变化
        mViewModel.studentCount.observe(getViewLifecycleOwner(), this::updateStudentStats);

        // 观察用户会话变化，更新头像
        userSessionManager.getCurrentUserLiveData().observe(getViewLifecycleOwner(), this::updateUserAvatar);
    }

    @Override
    protected void initData() {
        // 加载祈福数据
        mViewModel.loadBlessingData();
        mViewModel.loadStudentData();
    }    /**
     * 设置顶部导航栏
     */
    private void setupTopNavigation() {
        // 设置个人头像容器点击事件
        mBinding.profileAvatarContainer.setOnClickListener(v -> {
            addClickAnimation(v);
            // 检查用户是否已登录
            User currentUser = userSessionManager.getCurrentUser();
            if (currentUser != null) {
                // 已登录，跳转到个人中心
                navigateToProfile();
            } else {
                // 未登录，跳转到登录页面
                navigateToAuth();
            }
        });
        // 设置通知图标点击事件
        mBinding.notificationContainer.setOnClickListener(v -> {
            addClickAnimation(v);
            showNotifications();
        });

        // 更新标题
        mBinding.appTitle.setText("高考祈福");

        // 加载当前用户头像
        loadCurrentUserAvatar();
    }

    /**
     * 设置欢迎区域
     */
    private void setupWelcomeArea() {
        updateWelcomMessage();
        updateCountdownTimer();
        // 设置欢迎卡片点击效果
        mBinding.welcomeCard.setOnClickListener(v -> {
            addCardClickAnimation(v);
            showDetailedStats();
        });
    }

    /**
     * 设置快速操作按钮
     */
    private void setupQuickActions() {
        // 创建祈福
        mBinding.cardCreateBlessing.setOnClickListener(v -> {
            addCardClickAnimation(v);
            createBlessing();
        });
        // 快速祈福
        mBinding.cardBlessingWall.setOnClickListener(v -> {
            addCardClickAnimation(v);
            showQuickBlessingDialog();
        });

        // 管理考生
        mBinding.cardManageStudents.setOnClickListener(v -> {
            addCardClickAnimation(v);
            navigateToStudentManagement();
        });

        // 查看统计
        mBinding.cardViewStats.setOnClickListener(v -> {
            addCardClickAnimation(v);
            navigateToStatistics();
        });
    }

    /**
     * 设置最近祈福记录列表
     */
    private void setupRecentBlessings() {
        recentBlessingsAdapter = new RecentBlessingsAdapter();
        mBinding.rvRecentBlessings.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvRecentBlessings.setAdapter(recentBlessingsAdapter);

        // 设置查看更多点击事件
        mBinding.tvViewMore.setOnClickListener(v -> {
            navigateToBlessingWall();
        });
    }
    /**
     * 设置浮动操作按钮
     */
    private void setupFloatingActionButton() {
        mBinding.fabQuick.setOnClickListener(v -> {
            addFabClickAnimation(v);
            showQuickBlessingDialog();
        });
    }

    /**
     * 设置浮动粒子效果
     */
    private void setupParticleEffect() {
        // 创建粒子视图并添加到背景容器
        particleView = new FloatingParticleView(getContext());
        mBinding.backgroundParticles.addView(particleView);
    }

    /**
     * 更新欢迎消息
     */
    private void updateWelcomMessage() {
        String timeGreeting = getTimeBasedGreeting();
        mBinding.tvWelcomeTitle.setText(timeGreeting + "，祈福开始！");

        // 计算高考倒计时
        long daysToExam = calculateDaysToExam();
        mBinding.tvWelcomeSubtitle.setText(
                String.format("距离高考还有%d天，一起为考生加油吧！✨", daysToExam)
        );
    }

    /**
     * 开始定期更新
     */
    private void startPeriodicUpdates() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                updateCountdownTimer();
                updateWelcomMessage();
                // 每分钟更新一次
                uiHandler.postDelayed(this, 60000);
            }
        });

        // 启动3D背景粒子效果
        initParticleBackground();
    }

    /**
     * 初始化3D背景浮动粒子效果
     */
    private void initParticleBackground() {
        // 创建多个浮动粒子
        for (int i = 0; i < 8; i++) {
            createFloatingParticle(i);
        }
    }

    /**
     * 创建浮动粒子
     */
    private void createFloatingParticle(int index) {
        if (getContext() == null) return;

        View particle = new View(getContext());
        int size = (int) (8 + Math.random() * 16); // 8-24dp大小
        particle.setLayoutParams(new RelativeLayout.LayoutParams(size, size));
        particle.setBackgroundResource(R.drawable.floating_particle);
        particle.setAlpha(0.3f + (float) (Math.random() * 0.4f)); // 0.3-0.7透明度

        // 随机位置
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) particle.getLayoutParams();
        params.leftMargin = (int) (Math.random() * 400);
        params.topMargin = (int) (Math.random() * 600);

        mBinding.backgroundParticles.addView(particle);

        // 启动浮动动画
        startParticleAnimation(particle, index);
    }

    /**
     * 启动粒子浮动动画
     */
    private void startParticleAnimation(View particle, int index) {
        // 垂直浮动动画
        ObjectAnimator floatingY = ObjectAnimator.ofFloat(particle, "translationY",
                0f, -30f - (float) (Math.random() * 20f), 0f);
        floatingY.setDuration(3000 + (int) (Math.random() * 2000)); // 3-5秒
        floatingY.setRepeatCount(ObjectAnimator.INFINITE);
        floatingY.setRepeatMode(ObjectAnimator.REVERSE);

        // 水平微动画
        ObjectAnimator floatingX = ObjectAnimator.ofFloat(particle, "translationX",
                0f, 10f - (float) (Math.random() * 20f), 0f);
        floatingX.setDuration(2000 + (int) (Math.random() * 3000)); // 2-5秒
        floatingX.setRepeatCount(ObjectAnimator.INFINITE);
        floatingX.setRepeatMode(ObjectAnimator.REVERSE);

        // 旋转动画
        ObjectAnimator rotation = ObjectAnimator.ofFloat(particle, "rotation", 0f, 360f);
        rotation.setDuration(8000 + (int) (Math.random() * 4000)); // 8-12秒
        rotation.setRepeatCount(ObjectAnimator.INFINITE);

        // 透明度闪烁
        ObjectAnimator alpha = ObjectAnimator.ofFloat(particle, "alpha",
                particle.getAlpha(), particle.getAlpha() * 0.5f, particle.getAlpha());
        alpha.setDuration(1500 + (int) (Math.random() * 1500)); // 1.5-3秒
        alpha.setRepeatCount(ObjectAnimator.INFINITE);
        alpha.setRepeatMode(ObjectAnimator.REVERSE);

        // 错开动画开始时间
        uiHandler.postDelayed(() -> {
            floatingY.start();
            floatingX.start();
            rotation.start();
            alpha.start();
        }, index * 200);
    }

    /**
     * 处理加载状态
     */
    private void handleLoadingState(Boolean isLoading) {
        if (isLoading) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 处理错误消息
     */
    private void handleErrorMessage(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showToast(errorMessage);
            mViewModel.clearError();
        }
    }
    /**
     * 更新祈福统计
     */
    private void updateBlessingStats(int blessingCount) {
        // 更新欢迎区域的统计信息
        mBinding.tvWelcomeSubtitle.setText(
                String.format("已有%d位考生收到祝福，一起为考生加油吧！✨", blessingCount + 128)
        );
    }
    /**
     * 更新学生统计
     */
    private void updateStudentStats(Integer studentCount) {
        if (studentCount != null) {
            // 这里可以添加学生统计相关的UI更新
            // 目前布局中没有专门的学生统计显示区域
        }
    }

    /**
     * 添加点击动画
     */
    private void addClickAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.95f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.95f, 1.0f)
        );
        animator.setDuration(200);
        animator.start();
    }

    /**
     * 添加卡片点击动画
     */
    private void addCardClickAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.98f, 1.02f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.98f, 1.02f, 1.0f)
        );
        animator.setDuration(300);
        animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }

    /**
     * 添加FAB点击动画
     */
    private void addFabClickAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.1f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.1f, 1.0f),
                PropertyValuesHolder.ofFloat("rotation", 0f, 180f, 360f)
        );
        animator.setDuration(400);
        animator.start();
    }

    // 辅助方法
    private String getTimeBasedGreeting() {
        int hour = new Date().getHours();
        if (hour < 6) return "凌晨好";
        else if (hour < 12) return "上午好";
        else if (hour < 18) return "下午好";
        else return "晚上好";
    }

    private long calculateDaysToExam() {
        // 高考日期：2025年6月7日
        long examTime = new Date(2025 - 1900, 5, 7).getTime(); // 月份从0开始
        long currentTime = System.currentTimeMillis();
        return Math.max(0, (examTime - currentTime) / (1000 * 60 * 60 * 24));
    }
    private void updateCountdownTimer() {
        long days = calculateDaysToExam();
        // 更新欢迎区域的倒计时信息，已在updateWelcomMessage中处理
    }// 导航方法
    private void navigateToProfile() {
        // 切换到个人中心页面
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchToProfile();
        }
    }

    private void navigateToAuth() {
        // 跳转到登录页面
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        startActivity(intent);
    }

    private void showNotifications() {
        // 显示通知列表
        if (getContext() != null) {
            Toast.makeText(getContext(), "暂无新通知", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetailedStats() {
        // 显示详细统计信息
        if (getContext() != null) {
            Toast.makeText(getContext(), "详细统计功能开发中...", Toast.LENGTH_SHORT).show();
        }
    }

    private void createBlessing() {
        // 创建祈福
        if (getContext() != null) {
            Toast.makeText(getContext(), "创建祈福功能开发中...", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToStudentManagement() {
        // 导航到考生管理
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchToStudent();
        }
    }

    private void navigateToStatistics() {
        // 导航到统计页面
        if (getContext() != null) {
            Toast.makeText(getContext(), "统计功能开发中...", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToBlessingWall() {
        // 导航到祈福墙
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchToWall();
        }
    }

    /**
     * 显示快速祈福对话框
     */
    private void showQuickBlessingDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_quick_blessing, null);

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(dialogView)
                .create();

        // 设置祈福模板适配器
        RecyclerView rvTemplates = dialogView.findViewById(R.id.rvBlessingTemplates);
        BlessingTemplateAdapter templateAdapter = new BlessingTemplateAdapter();
        rvTemplates.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTemplates.setAdapter(templateAdapter);

        // 设置预定义模板
        templateAdapter.setTemplates(getPredefinedTemplates());

        EditText etCustomBlessing = dialogView.findViewById(R.id.etCustomBlessing);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSendBlessing = dialogView.findViewById(R.id.btnSendBlessing);

        // 模板选择监听
        templateAdapter.setOnTemplateSelectedListener(template -> {
            etCustomBlessing.setText(template);
        });

        // 取消按钮
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // 发送祈福按钮
        btnSendBlessing.setOnClickListener(v -> {
            String blessingContent = etCustomBlessing.getText().toString().trim();
            if (blessingContent.isEmpty()) {
                Toast.makeText(getContext(), "请输入祈福内容", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建祈福记录
            mViewModel.createBlessing("考生", blessingContent, "快速祈福");

            dialog.dismiss();

            Toast.makeText(getContext(), "祈福发送成功！", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    /**
     * 获取预定义祈福模板
     */
    private List<String> getPredefinedTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("愿你考试顺利，金榜题名！🎓");
        templates.add("保持冷静，发挥最佳水平！💪");
        templates.add("相信自己，你一定能取得好成绩！⭐");
        templates.add("愿你带着平常心走进考场！🙏");
        templates.add("所有的努力都会有回报！🌟");
        return templates;
    }

    /**
     * 加载当前用户头像
     */
    private void loadCurrentUserAvatar() {
        User currentUser = userSessionManager.getCurrentUser();
        updateUserAvatar(currentUser);
    }

    /**
     * 更新用户头像
     */    private void updateUserAvatar(User user) {
        // 确保头像视图可见
        mBinding.ivAvatar.setVisibility(View.VISIBLE);

        if (user != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            // 加载用户自定义头像
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(mBinding.ivAvatar);
        } else {
            // 使用默认头像
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(mBinding.ivAvatar);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uiHandler != null) {
            uiHandler.removeCallbacksAndMessages(null);
        }
        if (particleView != null) {
            particleView.stopAnimation();
        }
    }
}
