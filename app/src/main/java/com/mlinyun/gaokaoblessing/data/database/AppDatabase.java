package com.mlinyun.gaokaoblessing.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mlinyun.gaokaoblessing.data.dao.BlessingDao;
import com.mlinyun.gaokaoblessing.data.dao.BlessingTemplateDao;
import com.mlinyun.gaokaoblessing.data.dao.StudentDao;
import com.mlinyun.gaokaoblessing.data.dao.UserDao;
import com.mlinyun.gaokaoblessing.data.entity.BlessingEntity;
import com.mlinyun.gaokaoblessing.data.entity.BlessingTemplateEntity;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.data.model.User;
import com.mlinyun.gaokaoblessing.utils.DateConverter;

/**
 * 高考祈福应用数据库
 */
@Database(
        entities = {User.class, Student.class, BlessingEntity.class, BlessingTemplateEntity.class},
        version = 5,  // 增加版本号，添加effect字段到祈福模板表
        exportSchema = false
)
@TypeConverters({DateConverter.class})
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
     * 获取祈福DAO
     */
    public abstract BlessingDao blessingDao();

    /**
     * 获取祈福模板DAO
     */
    public abstract BlessingTemplateDao blessingTemplateDao();
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
                            .fallbackToDestructiveMigrationOnDowngrade() // 版本降级时删除重建
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取测试数据库实例（内存数据库）
     */
    public static AppDatabase getTestInstance(Context context) {
        return Room.inMemoryDatabaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class
                )
                .allowMainThreadQueries() // 测试时允许主线程查询
                .build();
    }

    /**
     * 强制清理并重建数据库实例
     * 用于解决schema验证失败的问题
     */
    public static void forceRecreateDatabase(Context context) {
        synchronized (AppDatabase.class) {
            if (INSTANCE != null) {
                INSTANCE.close();
                INSTANCE = null;
            }

            // 删除数据库文件
            context.deleteDatabase(DATABASE_NAME);

            // 重新创建实例
            getInstance(context);
        }
    }

    /**
     * 销毁数据库实例
     */
    public static void destroyInstance() {
        synchronized (AppDatabase.class) {
            if (INSTANCE != null) {
                INSTANCE.close();
                INSTANCE = null;
            }
        }
    }
}
