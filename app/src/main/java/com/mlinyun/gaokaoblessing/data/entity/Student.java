package com.mlinyun.gaokaoblessing.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 学生信息实体类
 */
@Entity(tableName = "student")
public class Student {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "student_id")
    private String studentId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

    @ColumnInfo(name = "school")
    private String school;

    @ColumnInfo(name = "class_name")
    private String className;

    @ColumnInfo(name = "subject_type")
    private String subjectType; // 文科/理科

    @ColumnInfo(name = "graduation_year")
    private int graduationYear;

    @ColumnInfo(name = "exam_date")
    private String examDate;

    @ColumnInfo(name = "target_score")
    private int targetScore;

    @ColumnInfo(name = "target_university")
    private String targetUniversity;

    @ColumnInfo(name = "is_followed")
    private boolean isFollowed;

    @ColumnInfo(name = "owner_user_id")
    private String ownerUserId;

    @ColumnInfo(name = "blessing_count")
    private int blessingCount;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;    // 构造函数
    public Student() {}

    @Ignore
    public Student(String name, String school, String className, String subjectType, int graduationYear, String ownerUserId) {
        this.name = name;
        this.school = school;
        this.className = className;
        this.subjectType = subjectType;
        this.graduationYear = graduationYear;
        this.ownerUserId = ownerUserId;
        this.studentId = generateStudentId();
        this.isFollowed = false;
        this.blessingCount = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    private String generateStudentId() {
        return "STU" + System.currentTimeMillis();
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
    public String getGrade() {
        return className;
    }

    public void setGrade(String grade) {
        this.className = grade;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(int targetScore) {
        this.targetScore = targetScore;
    }

    public String getTargetUniversity() {
        return targetUniversity;
    }

    public void setTargetUniversity(String targetUniversity) {
        this.targetUniversity = targetUniversity;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public int getBlessingCount() {
        return blessingCount;
    }

    public void setBlessingCount(int blessingCount) {
        this.blessingCount = blessingCount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
