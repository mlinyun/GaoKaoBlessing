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
import com.mlinyun.gaokaoblessing.databinding.FragmentBlessingBinding;
import com.mlinyun.gaokaoblessing.data.model.User;
import com.mlinyun.gaokaoblessing.manager.UserSessionManager;
import com.mlinyun.gaokaoblessing.ui.auth.AuthActivity;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.BlessingTemplateAdapter;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.RecentBlessingsAdapter;
import com.mlinyun.gaokaoblessing.ui.blessing.model.BlessingRecord;
import com.mlinyun.gaokaoblessing.ui.blessing.model.RecentBlessing;
import com.mlinyun.gaokaoblessing.ui.main.MainActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 祈福主页Fragment - 按照UI原型图实现
 * 包含顶部导航、欢迎区域、快速操作、最近祈福记录等功能
 */
public class BlessingFragment extends BaseFragment<FragmentBlessingBinding, BlessingViewModel> {

    private RecentBlessingsAdapter recentBlessingsAdapter;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private UserSessionManager userSessionManager;

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
        mBinding.ivNotification.setOnClickListener(v -> {
            addClickAnimation(v);
            showNotifications();
        });

        // 更新标题
        mBinding.tvTitle.setText("高考祈福");

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
        mBinding.cardWelcome.setOnClickListener(v -> {
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
        mBinding.cardQuickBlessing.setOnClickListener(v -> {
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
    }
    /**
     * 显示快速祈福对话框
     */
    private void showQuickBlessingDialog() {
        if (getContext() == null) return;

        // 预设的祈福模板
        String[] blessingTemplates = {
                "愿你高考顺利，金榜题名！🎓",
                "祝愿你考试发挥出色，心想事成！✨",
                "愿你文思如泉涌，答题如神助！📝",
                "祝你高考大捷，前程似锦！🌟",
                "愿你超常发挥，录取理想大学！🏫",
                "祝你考场上从容不迫，马到成功！🐎"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_GaoKaoBlessing_Dialog);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_quick_blessing, null);

        // 获取对话框中的控件
        RecyclerView rvTemplates = dialogView.findViewById(R.id.rvBlessingTemplates);
        EditText etCustomBlessing = dialogView.findViewById(R.id.etCustomBlessing);
        Button btnSend = dialogView.findViewById(R.id.btnSendBlessing);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // 设置模板列表
        BlessingTemplateAdapter templateAdapter = new BlessingTemplateAdapter(blessingTemplates, template -> {
            etCustomBlessing.setText(template);
            etCustomBlessing.setSelection(template.length());
        });
        rvTemplates.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTemplates.setAdapter(templateAdapter);

        AlertDialog dialog = builder.setView(dialogView).create();

        // 发送按钮点击事件
        btnSend.setOnClickListener(v -> {
            String blessingText = etCustomBlessing.getText().toString().trim();
            if (blessingText.isEmpty()) {
                showToast("请输入祈福内容");
                return;
            }

            // 发送祈福
            sendQuickBlessing(blessingText);
            dialog.dismiss();
        });

        // 取消按钮点击事件
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * 发送快速祈福
     */
    private void sendQuickBlessing(String blessingText) {
        if (getContext() == null) return;

        // 显示发送成功动画
        showToast("祈福已发送！愿心想事成！🙏");

        // 这里可以添加实际的发送逻辑，比如保存到数据库
        // mViewModel.sendBlessing(blessingText);

        // 模拟添加到最近祈福记录
        if (recentBlessingsAdapter != null) {
            RecentBlessing newBlessing = new RecentBlessing();
            newBlessing.setTitle(blessingText);
            newBlessing.setTime(System.currentTimeMillis());
            newBlessing.setStatus("已发送");

            // 添加到列表顶部
            recentBlessingsAdapter.addBlessing(newBlessing);

            // 更新统计数字
            updateBlessingStats(recentBlessingsAdapter.getItemCount());
        }
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
        mBinding.tvBlessingCount.setText(String.valueOf(blessingCount + 128)); // 基础数量
    }

    /**
     * 更新学生统计
     */
    private void updateStudentStats(Integer studentCount) {
        if (studentCount != null) {
            mBinding.tvStudentCount.setText(String.valueOf(studentCount));
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
        mBinding.tvCountdown.setText(String.format("%d天", days));
    }    // 导航方法
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
    }
}
