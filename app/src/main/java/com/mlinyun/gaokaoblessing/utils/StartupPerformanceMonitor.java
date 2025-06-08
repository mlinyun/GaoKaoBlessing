package com.mlinyun.gaokaoblessing.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * 启动页面性能监控工具类
 * 提供性能指标收集、分析和优化建议
 */
public class StartupPerformanceMonitor {

    private static final String TAG = "StartupPerformance";

    private Context context;
    private long startTime;
    private long initTime;
    private long animationStartTime;
    private long animationEndTime;

    // 性能指标
    private PerformanceMetrics metrics;

    /**
     * 性能指标数据类
     */
    public static class PerformanceMetrics {
        public long totalStartupTime;       // 总启动时间
        public long initializationTime;     // 初始化时间
        public long animationDuration;      // 动画总时长
        public long memoryUsage;            // 内存使用量(MB)
        public int memoryLeakCount;         // 内存泄漏计数
        public float cpuUsage;              // CPU使用率
        public int frameDropCount;          // 丢帧计数
        public String deviceInfo;           // 设备信息
        public int performanceLevel;        // 性能等级

        @Override
        public String toString() {
            return String.format(
                    "性能报告:\n" +
                            "总启动时间: %dms\n" +
                            "初始化时间: %dms\n" +
                            "动画时长: %dms\n" +
                            "内存使用: %dMB\n" +
                            "性能等级: %d\n" +
                            "设备信息: %s",
                    totalStartupTime, initializationTime, animationDuration,
                    memoryUsage, performanceLevel, deviceInfo
            );
        }
    }

    public StartupPerformanceMonitor(Context context) {
        this.context = context.getApplicationContext();
        this.metrics = new PerformanceMetrics();
        this.startTime = System.currentTimeMillis();

        // 收集设备信息
        collectDeviceInfo();
    }

    /**
     * 标记初始化完成
     */
    public void markInitializationComplete() {
        this.initTime = System.currentTimeMillis();
        this.metrics.initializationTime = initTime - startTime;
        Log.d(TAG, "初始化完成，耗时: " + metrics.initializationTime + "ms");
    }

    /**
     * 标记动画开始
     */
    public void markAnimationStart() {
        this.animationStartTime = System.currentTimeMillis();
        Log.d(TAG, "动画开始");
    }

    /**
     * 标记动画结束
     */
    public void markAnimationEnd() {
        this.animationEndTime = System.currentTimeMillis();
        this.metrics.animationDuration = animationEndTime - animationStartTime;
        this.metrics.totalStartupTime = animationEndTime - startTime;

        Log.d(TAG, "动画完成，动画时长: " + metrics.animationDuration + "ms");
        Log.d(TAG, "总启动时间: " + metrics.totalStartupTime + "ms");
    }

    /**
     * 收集内存使用情况
     */
    public void collectMemoryUsage() {
        try {
            // 获取应用内存使用情况
            Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(memInfo);

            // 总内存使用量 (PSS)
            metrics.memoryUsage = memInfo.getTotalPss() / 1024; // 转换为MB

            Log.d(TAG, "当前内存使用: " + metrics.memoryUsage + "MB");

            // 检测内存泄漏风险
            if (metrics.memoryUsage > 150) {
                Log.w(TAG, "⚠️ 内存使用偏高，可能存在内存泄漏风险");
                metrics.memoryLeakCount++;
            }

        } catch (Exception e) {
            Log.e(TAG, "内存信息收集失败", e);
        }
    }

    /**
     * 收集设备信息
     */
    private void collectDeviceInfo() {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memInfo);

            long totalMem = memInfo.totalMem / (1024 * 1024); // MB
            int cpuCores = Runtime.getRuntime().availableProcessors();

            metrics.deviceInfo = String.format(
                    "设备: %s %s, Android: %s, 内存: %dMB, CPU核心: %d",
                    Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE,
                    totalMem, cpuCores
            );

            // 计算性能等级
            if (totalMem >= 6 * 1024 && cpuCores >= 8) {
                metrics.performanceLevel = 2; // 高性能
            } else if (totalMem >= 3 * 1024 && cpuCores >= 4) {
                metrics.performanceLevel = 1; // 中等性能
            } else {
                metrics.performanceLevel = 0; // 低性能
            }

            Log.d(TAG, metrics.deviceInfo);

        } catch (Exception e) {
            Log.e(TAG, "设备信息收集失败", e);
            metrics.deviceInfo = "未知设备";
        }
    }

    /**
     * 开始CPU使用率监控
     */
    public void startCpuMonitoring() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 简单的CPU监控（这里可以集成更复杂的CPU监控逻辑）
                if (System.currentTimeMillis() - startTime < 10000) { // 监控前10秒
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    /**
     * 生成性能报告
     */
    public PerformanceMetrics generateReport() {
        // 最终收集内存使用情况
        collectMemoryUsage();

        Log.i(TAG, "\n" + "=".repeat(50));
        Log.i(TAG, "📊 启动页面性能报告");
        Log.i(TAG, "=".repeat(50));
        Log.i(TAG, metrics.toString());
        Log.i(TAG, "=".repeat(50));

        // 生成优化建议
        generateOptimizationSuggestions();

        return metrics;
    }

    /**
     * 生成优化建议
     */
    private void generateOptimizationSuggestions() {
        Log.i(TAG, "\n🔧 优化建议:");

        // 启动时间优化建议
        if (metrics.totalStartupTime > 3000) {
            Log.i(TAG, "⚠️ 启动时间较长，建议:");
            Log.i(TAG, "   - 减少初始化操作");
            Log.i(TAG, "   - 延迟加载非关键资源");
            Log.i(TAG, "   - 使用异步加载");
        } else if (metrics.totalStartupTime < 1500) {
            Log.i(TAG, "✅ 启动时间表现优秀");
        }

        // 内存使用优化建议
        if (metrics.memoryUsage > 100) {
            Log.i(TAG, "⚠️ 内存使用偏高，建议:");
            Log.i(TAG, "   - 检查内存泄漏");
            Log.i(TAG, "   - 优化图片资源");
            Log.i(TAG, "   - 及时释放动画资源");
        } else {
            Log.i(TAG, "✅ 内存使用表现良好");
        }

        // 动画性能建议
        if (metrics.animationDuration > 5000) {
            Log.i(TAG, "⚠️ 动画时长较长，建议:");
            Log.i(TAG, "   - 减少同时运行的动画");
            Log.i(TAG, "   - 使用硬件加速");
            Log.i(TAG, "   - 优化动画插值器");
        } else {
            Log.i(TAG, "✅ 动画性能表现良好");
        }

        // 设备性能建议
        if (metrics.performanceLevel == 0) {
            Log.i(TAG, "📱 低性能设备优化建议:");
            Log.i(TAG, "   - 简化动画效果");
            Log.i(TAG, "   - 降低动画帧率");
            Log.i(TAG, "   - 禁用粒子效果");
        }
    }

    /**
     * 检查是否需要性能降级
     */
    public boolean shouldDowngradePerformance() {
        return metrics.performanceLevel == 0 ||
                metrics.memoryUsage > 120 ||
                metrics.totalStartupTime > 4000;
    }

    /**
     * 获取推荐的动画配置
     */
    public AnimationConfig getRecommendedAnimationConfig() {
        AnimationConfig config = new AnimationConfig();

        switch (metrics.performanceLevel) {
            case 2: // 高性能
                config.animationDuration = 1000;
                config.enableComplexAnimations = true;
                config.maxConcurrentAnimations = 5;
                config.animationScale = 1.0f;
                break;
            case 1: // 中等性能
                config.animationDuration = 800;
                config.enableComplexAnimations = true;
                config.maxConcurrentAnimations = 3;
                config.animationScale = 0.8f;
                break;
            case 0: // 低性能
            default:
                config.animationDuration = 600;
                config.enableComplexAnimations = false;
                config.maxConcurrentAnimations = 2;
                config.animationScale = 0.6f;
                break;
        }

        return config;
    }

    /**
     * 动画配置类
     */
    public static class AnimationConfig {
        public long animationDuration;
        public boolean enableComplexAnimations;
        public int maxConcurrentAnimations;
        public float animationScale;
    }
}
