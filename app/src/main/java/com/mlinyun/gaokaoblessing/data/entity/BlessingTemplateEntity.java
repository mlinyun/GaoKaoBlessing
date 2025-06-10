package com.mlinyun.gaokaoblessing.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 祈福模板实体类
 */
@Entity(tableName = "blessing_template")
public class BlessingTemplateEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "category")
    private String category; // 模板分类：学业、健康、平安、成功等

    @ColumnInfo(name = "content")
    private String content; // 模板内容

    @ColumnInfo(name = "tags")
    private String tags; // 标签，以逗号分隔

    @ColumnInfo(name = "rating")
    private float rating = 5.0f; // 评分

    @ColumnInfo(name = "usage_count")
    private int usageCount = 0; // 使用次数
    @ColumnInfo(name = "is_default")
    private boolean isDefault = true; // 是否为默认模板

    @ColumnInfo(name = "effect")
    private String effect = "none"; // 特效类型：golden, star, rainbow, flower等

    // 构造函数
    public BlessingTemplateEntity() {
    }

    @androidx.room.Ignore
    public BlessingTemplateEntity(String category, String content, String tags) {
        this.category = category;
        this.content = content;
        this.tags = tags;
    }

    @androidx.room.Ignore
    public BlessingTemplateEntity(String content, String category, float rating, int usageCount, String tags, String effect) {
        this.content = content;
        this.category = category;
        this.rating = rating;
        this.usageCount = usageCount;
        this.tags = tags;
        this.effect = effect;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
