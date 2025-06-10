package com.mlinyun.gaokaoblessing.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * 祈福实体类
 */
@Entity(tableName = "blessing")
public class BlessingEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "student_id")
    private long studentId;

    @ColumnInfo(name = "student_name")
    private String studentName;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "template_category")
    private String templateCategory;

    @ColumnInfo(name = "blessing_type")
    private String blessingType; // 快速祈福、完整祈福等

    @ColumnInfo(name = "effect_type")
    private String effectType; // 特效类型

    @ColumnInfo(name = "is_public")
    private boolean isPublic = true; // 是否公开显示在祈福墙

    @ColumnInfo(name = "like_count")
    private int likeCount = 0;

    @ColumnInfo(name = "comment_count")
    private int commentCount = 0;

    @ColumnInfo(name = "author_id")
    private long authorId; // 创建者ID

    @ColumnInfo(name = "author_name")
    private String authorName; // 创建者姓名

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // 构造函数
    public BlessingEntity() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplateCategory() {
        return templateCategory;
    }

    public void setTemplateCategory(String templateCategory) {
        this.templateCategory = templateCategory;
    }

    public String getBlessingType() {
        return blessingType;
    }

    public void setBlessingType(String blessingType) {
        this.blessingType = blessingType;
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
