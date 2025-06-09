package com.mlinyun.gaokaoblessing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 用户实体类
 * Room数据库用户表
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "username", index = true)
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    @ColumnInfo(name = "last_login")
    private long lastLogin;

    @ColumnInfo(name = "is_remember_password")
    private boolean rememberPassword;

    @ColumnInfo(name = "auto_login_expire")
    private long autoLoginExpire; // 7天自动登录过期时间

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

    @ColumnInfo(name = "nickname")
    private String nickname;

    public User() {
    }

    @Ignore
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
        this.rememberPassword = false;
        this.autoLoginExpire = 0;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isRememberPassword() {
        return rememberPassword;
    }

    public void setRememberPassword(boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }

    public long getAutoLoginExpire() {
        return autoLoginExpire;
    }

    public void setAutoLoginExpire(long autoLoginExpire) {
        this.autoLoginExpire = autoLoginExpire;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 检查自动登录是否过期
     */
    public boolean isAutoLoginExpired() {
        return autoLoginExpire == 0 || System.currentTimeMillis() > autoLoginExpire;
    }

    /**
     * 设置7天自动登录
     */
    public void setAutoLoginFor7Days() {
        this.autoLoginExpire = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", rememberPassword=" + rememberPassword +
                ", autoLoginExpire=" + autoLoginExpire +
                '}';
    }
}
