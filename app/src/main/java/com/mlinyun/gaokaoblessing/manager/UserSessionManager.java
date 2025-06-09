package com.mlinyun.gaokaoblessing.manager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.data.model.User;

/**
 * 用户会话管理器
 * 负责管理当前登录用户的会话状态
 */
public class UserSessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static UserSessionManager instance;
    private SharedPreferences preferences;
    private MutableLiveData<User> currentUserLiveData;
    private User currentUser;

    private UserSessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserLiveData = new MutableLiveData<>();
        loadCurrentUser();
    }

    public static synchronized UserSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserSessionManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 设置当前登录用户
     */
    public void setCurrentUser(User user) {
        if (user != null) {
            currentUser = user;

            // 保存到SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(KEY_USER_ID, user.getId());
            editor.putString(KEY_USERNAME, user.getUsername());
            editor.putString(KEY_AVATAR_URL, user.getAvatarUrl());
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();
        } else {
            currentUser = null;
            clearSession();
        }

        currentUserLiveData.setValue(currentUser);
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 获取当前用户的LiveData
     */
    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    /**
     * 检查用户是否已登录
     */
    public boolean isLoggedIn() {
        return currentUser != null && preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 获取当前用户ID
     */
    public long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() :
                preferences.getLong(KEY_USER_ID, -1);
    }

    /**
     * 获取当前用户名
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() :
                preferences.getString(KEY_USERNAME, "");
    }

    /**
     * 获取当前用户头像URL
     */
    public String getCurrentUserAvatarUrl() {
        return currentUser != null ? currentUser.getAvatarUrl() :
                preferences.getString(KEY_AVATAR_URL, "");
    }

    /**
     * 更新用户头像URL
     */
    public void updateAvatarUrl(String avatarUrl) {
        if (currentUser != null) {
            currentUser.setAvatarUrl(avatarUrl);
            preferences.edit().putString(KEY_AVATAR_URL, avatarUrl).apply();
            currentUserLiveData.setValue(currentUser);
        }
    }

    /**
     * 清除会话
     */
    public void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        currentUser = null;
        currentUserLiveData.setValue(null);
    }

    /**
     * 登出
     */
    public void logout() {
        clearSession();
    }

    /**
     * 从SharedPreferences加载当前用户信息
     */
    private void loadCurrentUser() {
        if (preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            long userId = preferences.getLong(KEY_USER_ID, -1);
            if (userId != -1) {
                User user = new User();
                user.setId(userId);
                user.setUsername(preferences.getString(KEY_USERNAME, ""));
                user.setAvatarUrl(preferences.getString(KEY_AVATAR_URL, ""));
                currentUser = user;
                currentUserLiveData.setValue(currentUser);
            }
        }
    }
}
