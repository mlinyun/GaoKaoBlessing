package com.mlinyun.gaokaoblessing.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.base.BaseRepository;
import com.mlinyun.gaokaoblessing.data.dao.UserDao;
import com.mlinyun.gaokaoblessing.data.model.User;
import com.mlinyun.gaokaoblessing.utils.DatabaseRecoveryUtils;
import com.mlinyun.gaokaoblessing.utils.PasswordUtils;

import java.util.List;

/**
 * 用户数据仓库
 * 负责用户注册、登录、自动登录等功能的数据操作
 */
public class UserRepository extends BaseRepository {

    private final UserDao userDao;
    private final Context context;

    public UserRepository(UserDao userDao, Context context) {
        this.userDao = userDao;
        this.context = context;
    }

    /**
     * 执行数据库操作，带错误恢复机制
     */
    private <T> T executeWithRecovery(DatabaseOperation<T> operation) throws Exception {
        try {
            return operation.execute();
        } catch (Exception e) {
            // 检查是否是数据库schema错误
            if (isSchemaError(e)) {
                // 尝试恢复数据库
                boolean recovered = DatabaseRecoveryUtils.checkAndFixDatabaseErrors(context);
                if (recovered) {
                    // 重试操作
                    return operation.execute();
                }
            }
            throw e;
        }
    }

    /**
     * 检查是否是schema相关错误
     */
    private boolean isSchemaError(Exception e) {
        String message = e.getMessage();
        return message != null && (
                message.contains("Room cannot verify the data integrity") ||
                        message.contains("Migration didn't properly handle") ||
                        message.contains("no such table") ||
                        message.contains("no such column")
        );
    }

    /**
     * 数据库操作接口
     */
    private interface DatabaseOperation<T> {
        T execute() throws Exception;
    }

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码（明文）
     * @param email 邮箱
     * @return LiveData包装的注册结果
     */
    public LiveData<Resource<User>> register(String username, String password, String email) {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);
        executeInBackground(() -> {
            try {
                // 使用错误恢复机制执行注册操作
                User newUser = executeWithRecovery(() -> {
                    // 检查用户名是否已存在
                    if (userDao.checkUsernameExists(username) > 0) {
                        throw new RuntimeException("用户名已存在");
                    }

                    // 检查邮箱是否已存在
                    if (userDao.checkEmailExists(email) > 0) {
                        throw new RuntimeException("邮箱已被注册");
                    }

                    // 创建新用户
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(PasswordUtils.hashPassword(password)); // 加密密码
                    user.setEmail(email);
                    user.setCreatedAt(System.currentTimeMillis());
                    user.setUpdatedAt(System.currentTimeMillis());

                    // 保存到数据库
                    long userId = userDao.insertUser(user);
                    if (userId > 0) {
                        user.setId(userId);
                        return user;
                    } else {
                        throw new RuntimeException("注册失败，请重试");
                    }
                });

                setSuccess(result, newUser);

            } catch (Exception e) {
                setError(result, "注册失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码（明文）
     * @param rememberPassword 是否记住密码
     * @return LiveData包装的登录结果
     */
    public LiveData<Resource<User>> login(String username, String password, boolean rememberPassword) {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);
        executeInBackground(() -> {
            try {
                // 使用错误恢复机制执行登录操作
                User user = executeWithRecovery(() -> {
                    // 获取用户信息
                    User u = userDao.getUserByUsername(username);
                    if (u == null) {
                        throw new RuntimeException("用户名不存在");
                    }

                    // 验证密码
                    if (!PasswordUtils.verifyPassword(password, u.getPassword())) {
                        throw new RuntimeException("密码错误");
                    }

                    // 更新登录信息
                    long currentTime = System.currentTimeMillis();
                    userDao.updateLastLogin(u.getId(), currentTime);

                    // 处理记住密码
                    if (rememberPassword) {
                        u.setAutoLoginFor7Days();
                        userDao.updateLoginSettings(u.getId(), true, u.getAutoLoginExpire());
                        // 清除其他用户的自动登录状态
                        userDao.clearAllAutoLogin();
                        userDao.updateLoginSettings(u.getId(), true, u.getAutoLoginExpire());
                    } else {
                        userDao.updateLoginSettings(u.getId(), false, 0);
                    }

                    u.setLastLogin(currentTime);
                    u.setRememberPassword(rememberPassword);
                    return u;
                });

                setSuccess(result, user);

            } catch (Exception e) {
                setError(result, "登录失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 检查自动登录
     * @return LiveData包装的自动登录用户
     */
    public LiveData<Resource<User>> checkAutoLogin() {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);
        executeInBackground(() -> {
            try {
                // 使用错误恢复机制检查自动登录
                User user = executeWithRecovery(() -> {
                    long currentTime = System.currentTimeMillis();
                    User u = userDao.getAutoLoginUser(currentTime);

                    if (u != null) {
                        // 更新最后登录时间
                        userDao.updateLastLogin(u.getId(), currentTime);
                        u.setLastLogin(currentTime);
                    }
                    return u;
                });

                setSuccess(result, user); // 可能为null，表示没有有效的自动登录用户

            } catch (Exception e) {
                setError(result, "自动登录检查失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return LiveData包装的修改结果
     */
    public LiveData<Resource<Boolean>> changePassword(long userId, String oldPassword, String newPassword) {
        MutableLiveData<Resource<Boolean>> result = createResourceLiveData();
        setLoading(result);
        executeInBackground(() -> {
            try {
                // 使用错误恢复机制执行密码修改
                Boolean success = executeWithRecovery(() -> {
                    User user = userDao.getUserById(userId);
                    if (user == null) {
                        throw new RuntimeException("用户不存在");
                    }

                    // 验证旧密码
                    if (!PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
                        throw new RuntimeException("旧密码错误");
                    }

                    // 更新密码
                    String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
                    int affected = userDao.updatePassword(userId, hashedNewPassword);

                    if (affected > 0) {
                        return true;
                    } else {
                        throw new RuntimeException("密码修改失败");
                    }
                });

                setSuccess(result, success);

            } catch (Exception e) {
                setError(result, "密码修改失败：" + e.getMessage());
            }
        });

        return result;
    }
    /**
     * 登出（清除自动登录状态）
     * @param userId 用户ID
     */
    public void logout(long userId) {
        executeInBackground(() -> {
            try {
                executeWithRecovery(() -> {
                    userDao.updateLoginSettings(userId, false, 0);
                    return null;
                });
            } catch (Exception e) {
                // 静默处理登出错误
            }
        });
    }

    /**
     * 获取所有用户（管理员功能）
     * @return LiveData包装的用户列表
     */
    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return LiveData包装的用户信息
     */
    public LiveData<Resource<User>> getUserById(long userId) {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);
        executeInBackground(() -> {
            try {
                // 使用错误恢复机制获取用户信息
                User user = executeWithRecovery(() -> userDao.getUserById(userId));
                setSuccess(result, user);
            } catch (Exception e) {
                setError(result, "获取用户信息失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return LiveData包装的更新结果
     */
    public LiveData<Resource<Boolean>> updateUser(User user) {
        MutableLiveData<Resource<Boolean>> result = createResourceLiveData();
        setLoading(result);
        executeInBackground(() -> {
            try {
                // 使用错误恢复机制更新用户信息
                Boolean success = executeWithRecovery(() -> {
                    user.setUpdatedAt(System.currentTimeMillis());
                    int affected = userDao.updateUser(user);

                    if (affected > 0) {
                        return true;
                    } else {
                        throw new RuntimeException("用户信息更新失败");
                    }
                });

                setSuccess(result, success);

            } catch (Exception e) {
                setError(result, "用户信息更新失败：" + e.getMessage());
            }
        });

        return result;
    }
}
