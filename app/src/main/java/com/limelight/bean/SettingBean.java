package com.limelight.bean;

import java.util.ArrayList;
import java.util.List;

public class SettingBean {
    public int iconId = 0;
    public String groupName = "";
    public Boolean hasArrow = false;
    public String desc = "";
    public List<SettingItem> itemList = new ArrayList<>();
    public int selectItemValue = 0;//该项所选的值

    public SettingBean(int iconId, String groupName, boolean hasArrow){
        this.iconId = iconId;
        this.groupName = groupName;
        this.hasArrow = hasArrow;
    }
}
