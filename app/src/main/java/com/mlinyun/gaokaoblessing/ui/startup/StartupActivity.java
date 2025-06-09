package com.mlinyun.gaokaoblessing.ui.startup;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.base.BaseActivity;
import com.mlinyun.gaokaoblessing.databinding.ActivityStartupBinding;
import com.mlinyun.gaokaoblessing.ui.auth.AuthActivity;
import com.mlinyun.gaokaoblessing.utils.StartupPerformanceMonitor;

/**
 * 启动页Activity
 * 展示应用Logo、祝福语轮播和登录选项
 * 支持性能自适应动画系统
 */
public class StartupActivity extends BaseActivity<ActivityStartupBinding, StartupViewModel> {

    private static final String TAG = "StartupActivity";

    // 性能等级常量
    private static final int PERFORMANCE_HIGH = 2;
    private static final int PERFORMANCE_MEDIUM = 1;
    private static final int PERFORMANCE_LOW = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int currentBlessingIndex = 0;
    private String[] blessings = {
            "愿所有努力都开花结果 🌸",
            "金榜题名，前程似锦 ✨",
            "每一份祝福都有回音 🙏"
    };
    // 设备性能等级
    private int devicePerformanceLevel = PERFORMANCE_HIGH;

    // 动画配置
    private AnimationConfig animationConfig;

    // 性能监控器
    private StartupPerformanceMonitor performanceMonitor;

    /**
     * 动画配置类
     */
    private static class AnimationConfig {
        long logoAnimationDuration;
        long textAnimationDuration;
        long shimmerDuration;
        long blessingRotationInterval;
        boolean enableParticleAnimation;
        boolean enableBreathingAnimation;
        float animationScale;

        static AnimationConfig forPerformanceLevel(int level) {
            AnimationConfig config = new AnimationConfig();
            switch (level) {
                case PERFORMANCE_HIGH:
                    config.logoAnimationDuration = 1000;
                    config.textAnimationDuration = 800;
                    config.shimmerDuration = 2000;
                    config.blessingRotationInterval = 4500;
                    config.enableParticleAnimation = true;
                    config.enableBreathingAnimation = true;
                    config.animationScale = 1.0f;
                    break;
                case PERFORMANCE_MEDIUM:
                    config.logoAnimationDuration = 800;
                    config.textAnimationDuration = 600;
                    config.shimmerDuration = 1500;
                    config.blessingRotationInterval = 5000;
                    config.enableParticleAnimation = false;
                    config.enableBreathingAnimation = true;
                    config.animationScale = 0.8f;
                    break;
                case PERFORMANCE_LOW:
                default:
                    config.logoAnimationDuration = 600;
                    config.textAnimationDuration = 400;
                    config.shimmerDuration = 1000;
                    config.blessingRotationInterval = 6000;
                    config.enableParticleAnimation = false;
                    config.enableBreathingAnimation = false;
                    config.animationScale = 0.6f;
                    break;
            }
            return config;
        }
    }

    @Override
    protected ActivityStartupBinding getViewBinding() {
        return ActivityStartupBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<StartupViewModel> getViewModelClass() {
        return StartupViewModel.class;
    }
    @Override
    protected void initView() {
        // 初始化性能监控
        performanceMonitor = new StartupPerformanceMonitor(this);
        performanceMonitor.markAnimationStart();
        performanceMonitor.startCpuMonitoring();

        // 检测设备性能
        detectDevicePerformance();

        // 根据性能等级配置动画
        animationConfig = AnimationConfig.forPerformanceLevel(devicePerformanceLevel);

        // 记录启动时间
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "启动页初始化开始，设备性能等级: " + devicePerformanceLevel);

        // 设置所有动画元素的初始状态
        initAnimationViews();

        // 启动Logo动画
        startLogoAnimation();

        // 启动文字动画
        startTextAnimations();

        // 启动按钮动画
        startButtonAnimations();

        // 启动祝福语轮播
        startBlessingRotation();

        // 设置点击事件
        mBinding.btnStartJourney.setOnClickListener(this::onStartJourneyClick);
        mBinding.btnQuickLogin.setOnClickListener(this::onQuickLoginClick);

        // 根据性能等级决定是否启动粒子动画
        if (animationConfig.enableParticleAnimation) {
            startParticleAnimation();
        }

        // 标记初始化完成
        performanceMonitor.markInitializationComplete();

        // 记录初始化完成时间
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "启动页初始化完成，耗时: " + (endTime - startTime) + "ms");
        // 延迟标记动画结束，用于生成性能报告
        safePostDelayed(() -> {
            if (performanceMonitor != null) {
                performanceMonitor.markAnimationEnd();
                StartupPerformanceMonitor.PerformanceMetrics metrics = performanceMonitor.generateReport();

                // 根据性能报告动态调整
                if (performanceMonitor.shouldDowngradePerformance()) {
                    Log.w(TAG, "⚠️ 检测到性能问题，建议降级动画效果");
                }
            }
        }, 8000); // 8秒后生成报告，涵盖主要动画
    }

    @Override
    protected void initObserver() {
        // 观察ViewModel中的数据变化
        mViewModel.getNavigateToMain().observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Log.d(TAG, "接收到导航信号，准备跳转到主页面");
                navigateToAuthActivity();
            }
        });

        mViewModel.getShowToast().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                showToast(message);
            }
        });

        Log.d(TAG, "ViewModel观察者设置完成");
    }

    @Override
    protected void initData() {
        // 初始化启动页数据
        Log.d(TAG, "开始初始化启动页数据");

        // 检查用户登录状态
        mViewModel.checkUserStatus();

        // 预加载必要的应用数据
        preloadAppData();

        Log.d(TAG, "启动页数据初始化完成");
    }

    /**
     * 预加载应用数据
     */
    private void preloadAppData() {
        // 在后台线程预加载数据，避免阻塞UI
        new Thread(() -> {
            try {
                // 模拟预加载祝福语数据
                Thread.sleep(200);

                // 模拟预加载用户偏好设置
                Thread.sleep(100);

                Log.d(TAG, "应用数据预加载完成");
            } catch (InterruptedException e) {
                Log.w(TAG, "数据预加载被中断", e);
            }
        }).start();
    }
    /**
     * 检测设备性能等级
     */
    private void detectDevicePerformance() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memInfo);

            // 获取总内存（MB）
            long totalMem = memInfo.totalMem / (1024 * 1024);

            // 获取CPU核心数
            int cpuCores = Runtime.getRuntime().availableProcessors();

            // 根据内存和CPU判断性能等级
            if (totalMem >= 6 * 1024 && cpuCores >= 8) {
                devicePerformanceLevel = PERFORMANCE_HIGH;
            } else if (totalMem >= 3 * 1024 && cpuCores >= 4) {
                devicePerformanceLevel = PERFORMANCE_MEDIUM;
            } else {
                devicePerformanceLevel = PERFORMANCE_LOW;
            }

            // 考虑Android版本
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                devicePerformanceLevel = Math.min(devicePerformanceLevel, PERFORMANCE_MEDIUM);
            }

            Log.d(TAG, String.format("设备性能检测 - 内存: %dMB, CPU核心: %d, Android: %d, 性能等级: %d",
                    totalMem, cpuCores, Build.VERSION.SDK_INT, devicePerformanceLevel));

        } catch (Exception e) {
            Log.w(TAG, "性能检测失败，使用默认配置", e);
            devicePerformanceLevel = PERFORMANCE_MEDIUM;
        }
    }

    /**
     * 安全执行Handler延迟任务
     * @param runnable 要执行的任务
     * @param delayMillis 延迟时间（毫秒）
     */
    private void safePostDelayed(Runnable runnable, long delayMillis) {
        if (mHandler != null && !isFinishing() && !isDestroyed()) {
            mHandler.postDelayed(() -> {
                // 执行前再次检查Activity状态
                if (!isFinishing() && !isDestroyed() && mBinding != null) {
                    runnable.run();
                }
            }, delayMillis);
        }
    }

    /**
     * 初始化动画视图状态
     */
    private void initAnimationViews() {
        // 设置 Logo 容器初始状态
        mBinding.logoContainer.setAlpha(0f);
        mBinding.logoIcon.setAlpha(0f);
        mBinding.logoBackground.setAlpha(0f);

        // 设置文字初始状态
        mBinding.appTitle.setAlpha(0f);
        mBinding.appSubtitle.setAlpha(0f);

        // 设置按钮初始状态
        mBinding.buttonContainer.setAlpha(0f);

        // 设置祝福语初始状态
        mBinding.blessingText.setAlpha(0f);
    }

    /**
     * Logo出现动画（性能自适应）
     */
    private void startLogoAnimation() {
        // 容器整体动画
        Animation containerAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_container_appear);
        containerAnimation.setDuration((long) (animationConfig.logoAnimationDuration * animationConfig.animationScale));

        // 设置动画监听器，确保动画结束后元素保持可见
        containerAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 动画结束后确保容器可见
                mBinding.logoContainer.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mBinding.logoContainer.startAnimation(containerAnimation);

        // 根据性能等级调整延迟时间
        long iconDelay = devicePerformanceLevel >= PERFORMANCE_MEDIUM ? 300 : 150;
        long backgroundDelay = devicePerformanceLevel >= PERFORMANCE_MEDIUM ? 500 : 250;
        long shimmerDelay = devicePerformanceLevel >= PERFORMANCE_MEDIUM ? 2000 : 1000;
        // 延迟启动 Logo 图标动画
        safePostDelayed(() -> {
            Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_appear);
            logoAnimation.setDuration((long) (animationConfig.logoAnimationDuration * animationConfig.animationScale));
            logoAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mBinding != null && mBinding.logoIcon != null) {
                        mBinding.logoIcon.setAlpha(1f);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            if (mBinding != null && mBinding.logoIcon != null) {
                mBinding.logoIcon.startAnimation(logoAnimation);
            }
        }, iconDelay);

        // 延迟启动背景动画
        safePostDelayed(() -> {
            Animation backgroundAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_background_appear);
            backgroundAnimation.setDuration((long) (animationConfig.logoAnimationDuration * animationConfig.animationScale));
            backgroundAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mBinding != null && mBinding.logoBackground != null) {
                        mBinding.logoBackground.setAlpha(1f);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            if (mBinding != null && mBinding.logoBackground != null) {
                mBinding.logoBackground.startAnimation(backgroundAnimation);
            }
        }, backgroundDelay);

        // 延迟启动闪光效果
        safePostDelayed(() -> startLogoShimmerEffect(), shimmerDelay);
    }

    /**
     * 文字动画（性能自适应）
     */
    private void startTextAnimations() {
        // 应用标题动画
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.text_appear);
        titleAnimation.setDuration(animationConfig.textAnimationDuration);
        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.appTitle.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mBinding.appTitle.startAnimation(titleAnimation);

        // 应用副标题动画
        Animation subtitleAnimation = AnimationUtils.loadAnimation(this, R.anim.subtitle_appear);
        subtitleAnimation.setDuration(animationConfig.textAnimationDuration);
        subtitleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.appSubtitle.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mBinding.appSubtitle.startAnimation(subtitleAnimation);
        // 祝福语动画（延迟显示）
        long blessingDelay = devicePerformanceLevel >= PERFORMANCE_MEDIUM ? 2000 : 1500;
        safePostDelayed(() -> {
            if (mBinding != null && mBinding.blessingText != null) {
                ObjectAnimator blessingFadeIn = ObjectAnimator.ofFloat(mBinding.blessingText, "alpha", 0f, 1f);
                blessingFadeIn.setDuration(animationConfig.textAnimationDuration);
                blessingFadeIn.start();
            }
        }, blessingDelay);
    }
    /**
     * 按钮动画
     */
    private void startButtonAnimations() {
        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_appear);
        buttonAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.buttonContainer.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mBinding.buttonContainer.startAnimation(buttonAnimation);
    }

    /**
     * Logo光泽效果（性能自适应）
     */
    private void startLogoShimmerEffect() {
        // 显示闪光层
        mBinding.shimmerOverlay.setVisibility(View.VISIBLE);

        // 加载并应用闪光动画
        Animation shimmerAnimation = AnimationUtils.loadAnimation(this, R.anim.shimmer_effect);
        shimmerAnimation.setDuration(animationConfig.shimmerDuration);
        mBinding.shimmerOverlay.startAnimation(shimmerAnimation);

        // 根据性能配置决定是否启用呼吸动画
        if (animationConfig.enableBreathingAnimation) {
            Animation breathingAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_breathing);
            mBinding.logoIcon.startAnimation(breathingAnimation);

            // 为背景添加轻微的脉冲效果
            ValueAnimator backgroundPulse = ValueAnimator.ofFloat(0.95f, 1.0f);
            backgroundPulse.setDuration((long) (3000 * animationConfig.animationScale));
            backgroundPulse.setRepeatCount(ValueAnimator.INFINITE);
            backgroundPulse.setRepeatMode(ValueAnimator.REVERSE);
            backgroundPulse.setInterpolator(new AccelerateDecelerateInterpolator());

            backgroundPulse.addUpdateListener(animation -> {
                float scale = (float) animation.getAnimatedValue();
                mBinding.logoBackground.setScaleX(scale);
                mBinding.logoBackground.setScaleY(scale);
            });

            backgroundPulse.start();
        }
    }

    /**
     * 祝福语轮播动画（性能自适应）
     */
    private void startBlessingRotation() {
        Runnable blessingRunnable = new Runnable() {
            @Override
            public void run() {
                // 检查Activity和mBinding状态
                if (isFinishing() || isDestroyed() || mBinding == null || mBinding.blessingText == null) {
                    return;
                }

                // 使用 XML 动画资源进行淡出
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(StartupActivity.this, R.anim.blessing_fade_out);
                fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 再次检查状态
                        if (isFinishing() || isDestroyed() || mBinding == null || mBinding.blessingText == null) {
                            return;
                        }

                        // 更换文本
                        currentBlessingIndex = (currentBlessingIndex + 1) % blessings.length;
                        mBinding.blessingText.setText(blessings[currentBlessingIndex]);

                        // 使用 XML 动画资源进行淡入
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(StartupActivity.this, R.anim.blessing_fade_in);
                        mBinding.blessingText.startAnimation(fadeInAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                mBinding.blessingText.startAnimation(fadeOutAnimation);

                // 根据性能配置调整轮播间隔，使用安全的延迟方法
                safePostDelayed(this, animationConfig.blessingRotationInterval);
            }
        };

        // 设置初始文本
        if (mBinding != null && mBinding.blessingText != null) {
            mBinding.blessingText.setText(blessings[0]);
        }

        // 根据性能等级调整开始延迟
        long startDelay = devicePerformanceLevel >= PERFORMANCE_MEDIUM ? 3000 : 2000;
        safePostDelayed(blessingRunnable, startDelay);
    }

    /**
     * 粒子动画效果
     */
    private void startParticleAnimation() {
        // 启动自定义粒子视图动画
        if (mBinding != null && mBinding.particleView != null) {
            // 延迟启动粒子动画，让 Logo 动画先完成
            safePostDelayed(() -> {
                // 这里可以调用 ParticleView 的动画方法
                // if (mBinding != null && mBinding.particleView != null) {
                //     mBinding.particleView.startAnimation();
                // }
            }, 1000);
        }

        // 创建背景浮动星星效果
        createFloatingStars();
    }

    /**
     * 创建浮动星星效果
     */
    private void createFloatingStars() {
        // 为了演示，这里创建一个简单的背景闪烁效果
        ValueAnimator starTwinkle = ValueAnimator.ofFloat(0.3f, 0.9f);
        starTwinkle.setDuration(1500);
        starTwinkle.setRepeatCount(ValueAnimator.INFINITE);
        starTwinkle.setRepeatMode(ValueAnimator.REVERSE);
        starTwinkle.setInterpolator(new AccelerateDecelerateInterpolator());

        starTwinkle.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            // 这里可以控制背景元素的透明度变化
            // 比如底部装饰星星的闪烁效果
        });

        starTwinkle.start();
    }
    /**
     * 开始祈福之旅按钮点击
     */
    private void onStartJourneyClick(View view) {
        // 播放按钮按下动画
        Animation pressAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press);
        pressAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 播放按钮释放动画
                Animation releaseAnimation = AnimationUtils.loadAnimation(StartupActivity.this, R.anim.button_release);
                releaseAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 执行跳转
                        mViewModel.startJourney();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                view.startAnimation(releaseAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(pressAnimation);
    }

    /**
     * 微信快速登录按钮点击
     */
    private void onQuickLoginClick(View view) {
        // 播放按钮按下动画
        Animation pressAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press);
        pressAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 播放按钮释放动画
                Animation releaseAnimation = AnimationUtils.loadAnimation(StartupActivity.this, R.anim.button_release);
                releaseAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 执行微信登录
                        mViewModel.quickLogin();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                view.startAnimation(releaseAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(pressAnimation);
    }

    /**
     * 跳转到认证页面
     */
    private void navigateToAuthActivity() {
        // 检查Activity是否还在有效状态
        if (isFinishing() || isDestroyed()) {
            Log.w(TAG, "Activity已经被销毁或正在销毁，取消导航");
            return;
        }

        // 停止所有动画
        stopAllAnimations();

        // 播放退出动画
        Animation exitAnimation = AnimationUtils.loadAnimation(this, R.anim.page_exit);
        exitAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 再次检查Activity状态
                if (isFinishing() || isDestroyed()) {
                    Log.w(TAG, "动画结束时Activity已被销毁，取消跳转");
                    return;
                }

                // 动画结束后进行页面跳转
                Intent intent = new Intent(StartupActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();

                // 添加页面切换动画
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // 对主容器应用退出动画
        findViewById(android.R.id.content).startAnimation(exitAnimation);
    }
    /**
     * 停止所有动画
     */
    private void stopAllAnimations() {
        // 清除所有待执行的任务
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        // 检查mBinding是否为null，避免空指针异常
        if (mBinding != null) {
            // 停止所有视图动画
            if (mBinding.logoContainer != null) {
                mBinding.logoContainer.clearAnimation();
            }
            if (mBinding.logoIcon != null) {
                mBinding.logoIcon.clearAnimation();
            }
            if (mBinding.logoBackground != null) {
                mBinding.logoBackground.clearAnimation();
            }
            if (mBinding.shimmerOverlay != null) {
                mBinding.shimmerOverlay.clearAnimation();
            }
            if (mBinding.appTitle != null) {
                mBinding.appTitle.clearAnimation();
            }
            if (mBinding.appSubtitle != null) {
                mBinding.appSubtitle.clearAnimation();
            }
            if (mBinding.buttonContainer != null) {
                mBinding.buttonContainer.clearAnimation();
            }
            if (mBinding.blessingText != null) {
                mBinding.blessingText.clearAnimation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 首先停止所有动画，避免在销毁过程中继续执行
        stopAllAnimations();

        // 清理Handler
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        // 生成最终性能报告（如果还没生成）
        if (performanceMonitor != null) {
            Log.d(TAG, "Activity销毁，生成最终性能报告");
            try {
                performanceMonitor.generateReport();
            } catch (Exception e) {
                Log.w(TAG, "性能报告生成异常", e);
            }
            performanceMonitor = null;
        }

        // 调用父类的onDestroy
        super.onDestroy();
    }
}
