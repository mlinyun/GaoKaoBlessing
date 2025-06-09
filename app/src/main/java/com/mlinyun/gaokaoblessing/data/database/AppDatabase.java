package com.mlinyun.gaokaoblessing.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mlinyun.gaokaoblessing.data.dao.StudentDao;
import com.mlinyun.gaokaoblessing.data.dao.UserDao;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.data.model.User;

/**
 * 高考祈福应用数据库
 */
@Database(
        entities = {User.class, Student.class},
        version = 2,  // 增加版本号，因为User表结构发生了变化
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "gaokao_blessing_db";
    private static volatile AppDatabase INSTANCE;

    /**
     * 获取用户DAO
     */
    public abstract UserDao userDao();

    /**
     * 获取学生DAO
     */
    public abstract StudentDao studentDao();

    /**
     * 获取数据库实例（单例模式）
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration() // 版本升级时删除重建
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 销毁数据库实例
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
