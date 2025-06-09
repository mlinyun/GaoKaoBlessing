package com.mlinyun.gaokaoblessing.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.base.BaseRepository;
import com.mlinyun.gaokaoblessing.data.dao.UserDao;
import com.mlinyun.gaokaoblessing.data.model.User;
import com.mlinyun.gaokaoblessing.utils.PasswordUtils;

import java.util.List;

/**
 * 用户数据仓库
 * 负责用户注册、登录、自动登录等功能的数据操作
 */
public class UserRepository extends BaseRepository {

    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码（明文）
     * @param email    邮箱
     * @return LiveData包装的注册结果
     */
    public LiveData<Resource<User>> register(String username, String password, String email) {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);

        executeInBackground(() -> {
            try {
                // 检查用户名是否已存在
                if (userDao.checkUsernameExists(username) > 0) {
                    setError(result, "用户名已存在");
                    return;
                }

                // 检查邮箱是否已存在
                if (userDao.checkEmailExists(email) > 0) {
                    setError(result, "邮箱已被注册");
                    return;
                }

                // 创建新用户
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(PasswordUtils.hashPassword(password)); // 加密密码
                newUser.setEmail(email);
                newUser.setCreatedAt(System.currentTimeMillis());
                newUser.setUpdatedAt(System.currentTimeMillis());

                // 保存到数据库
                long userId = userDao.insertUser(newUser);
                if (userId > 0) {
                    newUser.setId(userId);
                    setSuccess(result, newUser);
                } else {
                    setError(result, "注册失败，请重试");
                }

            } catch (Exception e) {
                setError(result, "注册失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 用户登录
     *
     * @param username         用户名
     * @param password         密码（明文）
     * @param rememberPassword 是否记住密码
     * @return LiveData包装的登录结果
     */
    public LiveData<Resource<User>> login(String username, String password, boolean rememberPassword) {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);

        executeInBackground(() -> {
            try {
                // 获取用户信息
                User user = userDao.getUserByUsername(username);
                if (user == null) {
                    setError(result, "用户名不存在");
                    return;
                }

                // 验证密码
                if (!PasswordUtils.verifyPassword(password, user.getPassword())) {
                    setError(result, "密码错误");
                    return;
                }

                // 更新登录信息
                long currentTime = System.currentTimeMillis();
                userDao.updateLastLogin(user.getId(), currentTime);

                // 处理记住密码
                if (rememberPassword) {
                    user.setAutoLoginFor7Days();
                    userDao.updateLoginSettings(user.getId(), true, user.getAutoLoginExpire());
                } else {
                    userDao.updateLoginSettings(user.getId(), false, 0);
                }

                // 清除其他用户的自动登录状态
                if (rememberPassword) {
                    userDao.clearAllAutoLogin();
                    userDao.updateLoginSettings(user.getId(), true, user.getAutoLoginExpire());
                }

                user.setLastLogin(currentTime);
                user.setRememberPassword(rememberPassword);
                setSuccess(result, user);

            } catch (Exception e) {
                setError(result, "登录失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 检查自动登录
     *
     * @return LiveData包装的自动登录用户
     */
    public LiveData<Resource<User>> checkAutoLogin() {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);

        executeInBackground(() -> {
            try {
                long currentTime = System.currentTimeMillis();
                User user = userDao.getAutoLoginUser(currentTime);

                if (user != null) {
                    // 更新最后登录时间
                    userDao.updateLastLogin(user.getId(), currentTime);
                    user.setLastLogin(currentTime);
                    setSuccess(result, user);
                } else {
                    setSuccess(result, null); // 没有有效的自动登录用户
                }

            } catch (Exception e) {
                setError(result, "自动登录检查失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return LiveData包装的修改结果
     */
    public LiveData<Resource<Boolean>> changePassword(long userId, String oldPassword, String newPassword) {
        MutableLiveData<Resource<Boolean>> result = createResourceLiveData();
        setLoading(result);

        executeInBackground(() -> {
            try {
                User user = userDao.getUserById(userId);
                if (user == null) {
                    setError(result, "用户不存在");
                    return;
                }

                // 验证旧密码
                if (!PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
                    setError(result, "旧密码错误");
                    return;
                }

                // 更新密码
                String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
                int affected = userDao.updatePassword(userId, hashedNewPassword);

                if (affected > 0) {
                    setSuccess(result, true);
                } else {
                    setError(result, "密码修改失败");
                }

            } catch (Exception e) {
                setError(result, "密码修改失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 登出（清除自动登录状态）
     *
     * @param userId 用户ID
     */
    public void logout(long userId) {
        executeInBackground(() -> {
            try {
                userDao.updateLoginSettings(userId, false, 0);
            } catch (Exception e) {
                // 静默处理登出错误
            }
        });
    }

    /**
     * 获取所有用户（管理员功能）
     *
     * @return LiveData包装的用户列表
     */
    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return LiveData包装的用户信息
     */
    public LiveData<Resource<User>> getUserById(long userId) {
        MutableLiveData<Resource<User>> result = createResourceLiveData();
        setLoading(result);

        executeInBackground(() -> {
            try {
                User user = userDao.getUserById(userId);
                setSuccess(result, user);
            } catch (Exception e) {
                setError(result, "获取用户信息失败：" + e.getMessage());
            }
        });

        return result;
    }

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return LiveData包装的更新结果
     */
    public LiveData<Resource<Boolean>> updateUser(User user) {
        MutableLiveData<Resource<Boolean>> result = createResourceLiveData();
        setLoading(result);

        executeInBackground(() -> {
            try {
                user.setUpdatedAt(System.currentTimeMillis());
                int affected = userDao.updateUser(user);

                if (affected > 0) {
                    setSuccess(result, true);
                } else {
                    setError(result, "用户信息更新失败");
                }

            } catch (Exception e) {
                setError(result, "用户信息更新失败：" + e.getMessage());
            }
        });

        return result;
    }
}
