package com.mlinyun.gaokaoblessing.ui.blessing.model;

import java.util.Date;

/**
 * 祈福记录数据模型
 */
public class BlessingRecord {
    private String id;
    private String studentName;
    private String content;
    private String type;
    private Date createTime;
    private String fromUser;

    public BlessingRecord() {
    }

    public BlessingRecord(String studentName, String content, String type) {
        this.studentName = studentName;
        this.content = content;
        this.type = type;
        this.createTime = new Date();
        this.id = String.valueOf(System.currentTimeMillis());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
}
