package com.mlinyun.gaokaoblessing.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mlinyun.gaokaoblessing.data.model.User;

import java.util.List;

/**
 * 用户数据访问对象
 * 支持用户注册、登录、自动登录等功能
 */
@Dao
public interface UserDao {

    /**
     * 插入用户（注册）
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertUser(User user);

    /**
     * 更新用户信息
     */
    @Update
    int updateUser(User user);

    /**
     * 删除用户
     */
    @Delete
    int deleteUser(User user);

    /**
     * 根据用户名查询用户（用于登录验证）
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    /**
     * 根据用户名和密码查询用户（登录验证）
     */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    /**
     * 根据邮箱查询用户
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    /**
     * 检查用户名是否存在（注册时验证）
     */
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int checkUsernameExists(String username);

    /**
     * 检查邮箱是否存在（注册时验证）
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);

    /**
     * 更新用户最后登录时间
     */
    @Query("UPDATE users SET last_login = :lastLogin WHERE id = :userId")
    int updateLastLogin(long userId, long lastLogin);

    /**
     * 更新用户记住密码状态和自动登录过期时间
     */
    @Query("UPDATE users SET is_remember_password = :rememberPassword, auto_login_expire = :autoLoginExpire WHERE id = :userId")
    int updateLoginSettings(long userId, boolean rememberPassword, long autoLoginExpire);

    /**
     * 更新用户密码
     */
    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    int updatePassword(long userId, String newPassword);

    /**
     * 获取有效的自动登录用户（未过期）
     */
    @Query("SELECT * FROM users WHERE is_remember_password = 1 AND auto_login_expire > :currentTime LIMIT 1")
    User getAutoLoginUser(long currentTime);

    /**
     * 清除所有用户的自动登录状态
     */
    @Query("UPDATE users SET is_remember_password = 0, auto_login_expire = 0")
    int clearAllAutoLogin();

    /**
     * 获取所有用户
     */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    LiveData<List<User>> getAllUsers();

    /**
     * 根据ID查询用户
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(long userId);

    /**
     * 清空所有用户数据
     */
    @Query("DELETE FROM users")
    int deleteAllUsers();
}
