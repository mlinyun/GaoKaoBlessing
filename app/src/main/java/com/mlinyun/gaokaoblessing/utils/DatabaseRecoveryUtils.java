package com.mlinyun.gaokaoblessing.utils;

import android.content.Context;
import android.util.Log;

import com.mlinyun.gaokaoblessing.data.database.AppDatabase;

/**
 * 数据库错误恢复工具
 * 用于处理Room数据库的schema验证失败等问题
 */
public class DatabaseRecoveryUtils {

    private static final String TAG = "DatabaseRecovery";

    /**
     * 检查并修复数据库错误
     *
     * @param context 应用上下文
     * @return 是否成功修复
     */
    public static boolean checkAndFixDatabaseErrors(Context context) {
        try {
            Log.d(TAG, "开始检查数据库状态...");

            // 尝试获取数据库实例
            AppDatabase database = AppDatabase.getInstance(context);

            // 尝试访问数据库以触发可能的验证错误
            database.userDao().getAllUsers();

            Log.d(TAG, "数据库状态正常");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "数据库验证失败: " + e.getMessage());

            // 检查是否是schema验证错误
            if (isSchemaValidationError(e)) {
                Log.w(TAG, "检测到schema验证错误，开始重建数据库...");
                return recreateDatabase(context);
            }

            Log.e(TAG, "未知数据库错误", e);
            return false;
        }
    }

    /**
     * 检查是否是schema验证错误
     */
    private static boolean isSchemaValidationError(Exception e) {
        String message = e.getMessage();
        return message != null && (
                message.contains("Room cannot verify the data integrity") ||
                        message.contains("Migration didn't properly handle") ||
                        message.contains("Expected:") ||
                        message.contains("Found:")
        );
    }

    /**
     * 重建数据库
     */
    private static boolean recreateDatabase(Context context) {
        try {
            Log.i(TAG, "开始重建数据库...");

            // 强制重建数据库
            AppDatabase.forceRecreateDatabase(context);

            // 验证重建后的数据库
            AppDatabase database = AppDatabase.getInstance(context);
            database.userDao().getAllUsers();

            Log.i(TAG, "数据库重建成功");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "数据库重建失败", e);
            return false;
        }
    }

    /**
     * 清理数据库缓存
     */
    public static void clearDatabaseCache(Context context) {
        try {
            Log.d(TAG, "清理数据库缓存...");
            AppDatabase.destroyInstance();
            Log.d(TAG, "数据库缓存清理完成");
        } catch (Exception e) {
            Log.e(TAG, "清理数据库缓存失败", e);
        }
    }

    /**
     * 获取数据库状态信息
     */
    public static String getDatabaseStatus(Context context) {
        try {
            AppDatabase database = AppDatabase.getInstance(context);

            // 获取基本信息
            int userCount = database.userDao().getAllUsers().getValue() != null ?
                    database.userDao().getAllUsers().getValue().size() : 0;

            return String.format("数据库状态正常，用户数量: %d", userCount);

        } catch (Exception e) {
            return "数据库状态异常: " + e.getMessage();
        }
    }
}
