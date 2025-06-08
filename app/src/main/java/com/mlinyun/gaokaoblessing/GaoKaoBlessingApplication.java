package com.mlinyun.gaokaoblessing;

import android.app.Application;

import com.mlinyun.gaokaoblessing.data.database.AppDatabase;

/**
 * 高考祝福应用 Application 类
 */
public class GaoKaoBlessingApplication extends Application {

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化数据库
        database = AppDatabase.getInstance(this);
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
