package com.mlinyun.gaokaoblessing;

import android.app.Application;
import android.util.Log;

import com.mlinyun.gaokaoblessing.data.database.AppDatabase;
import com.mlinyun.gaokaoblessing.utils.DatabaseRecoveryUtils;

/**
 * 高考祝福应用Application类
 */
public class GaoKaoBlessingApplication extends Application {

    private static final String TAG = "GaoKaoBlessingApp";
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "应用启动，初始化数据库...");

        // 检查并修复数据库问题
        boolean databaseHealthy = DatabaseRecoveryUtils.checkAndFixDatabaseErrors(this);

        if (databaseHealthy) {
            Log.i(TAG, "数据库初始化成功");
            database = AppDatabase.getInstance(this);
        } else {
            Log.e(TAG, "数据库初始化失败，尝试重新创建...");
            try {
                AppDatabase.forceRecreateDatabase(this);
                database = AppDatabase.getInstance(this);
                Log.i(TAG, "数据库重建成功");
            } catch (Exception e) {
                Log.e(TAG, "数据库重建失败", e);
                // 在这种情况下，应用可能需要退出或显示错误信息
            }
        }
    }

    public AppDatabase getDatabase() {
        return database;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "应用终止，清理数据库资源...");
        DatabaseRecoveryUtils.clearDatabaseCache(this);
    }
}
