package com.mlinyun.gaokaoblessing.ui.blessing.model;

/**
 * 最近祈福记录模型
 */
public class RecentBlessing {
    private String title;
    private long time;
    private String status;
    private String type;

    public RecentBlessing() {
    }

    public RecentBlessing(String title, long time, String status) {
        this.title = title;
        this.time = time;
        this.status = status;
        this.type = "祈福";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
