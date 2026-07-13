package com.limelight.bean;

public class SettingItem {
    private int value;
    private String title;

    public SettingItem(int value, String title) {
        this.value = value;
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
