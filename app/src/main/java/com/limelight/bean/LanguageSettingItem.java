package com.limelight.bean;

import com.limelight.utils.LanguageUtils;

public class LanguageSettingItem extends SettingItem{
    private LanguageUtils.LanguageType languageType = LanguageUtils.LanguageType.SYS;

    public LanguageSettingItem(String title, LanguageUtils.LanguageType languageType){
        super(languageType.ordinal(), title);
        this.languageType = languageType;
    }

    public LanguageUtils.LanguageType getLanguageType() {
        return languageType;
    }

    public void setLanguageType(LanguageUtils.LanguageType languageType) {
        this.languageType = languageType;
    }
}
