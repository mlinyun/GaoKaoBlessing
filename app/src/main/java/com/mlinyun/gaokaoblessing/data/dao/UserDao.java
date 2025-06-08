package com.mlinyun.gaokaoblessing.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mlinyun.gaokaoblessing.data.entity.User;

import java.util.List;

/**
 * 用户数据访问对象
 */
@Dao
public interface UserDao {

    /**
     * 插入用户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);

    /**
     * 更新用户
     */
    @Update
    int updateUser(User user);

    /**
     * 删除用户
     */
    @Delete
    int deleteUser(User user);

    /**
     * 根据用户ID查询用户
     */
    @Query("SELECT * FROM user WHERE user_id = :userId LIMIT 1")
    LiveData<User> getUserByUserId(String userId);

    /**
     * 根据用户名查询用户
     */
    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    LiveData<User> getUserByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    @Query("SELECT * FROM user WHERE phone = :phone LIMIT 1")
    LiveData<User> getUserByPhone(String phone);

    /**
     * 获取所有用户
     */
    @Query("SELECT * FROM user ORDER BY created_at DESC")
    LiveData<List<User>> getAllUsers();

    /**
     * 获取VIP用户列表
     */
    @Query("SELECT * FROM user WHERE is_vip = 1 ORDER BY created_at DESC")
    LiveData<List<User>> getVipUsers();

    /**
     * 更新用户金币余额
     */
    @Query("UPDATE user SET coin_balance = :balance, updated_at = :updatedAt WHERE user_id = :userId")
    int updateUserCoinBalance(String userId, int balance, long updatedAt);

    /**
     * 更新用户VIP状态
     */
    @Query("UPDATE user SET is_vip = :isVip, updated_at = :updatedAt WHERE user_id = :userId")
    int updateUserVipStatus(String userId, boolean isVip, long updatedAt);

    /**
     * 检查用户名是否存在
     */
    @Query("SELECT COUNT(*) FROM user WHERE username = :username")
    int checkUsernameExists(String username);

    /**
     * 检查手机号是否存在
     */
    @Query("SELECT COUNT(*) FROM user WHERE phone = :phone")
    int checkPhoneExists(String phone);

    /**
     * 清空所有用户数据
     */
    @Query("DELETE FROM user")
    int deleteAllUsers();
}
