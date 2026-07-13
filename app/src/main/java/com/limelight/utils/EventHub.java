package com.limelight.utils;

import androidx.lifecycle.MutableLiveData;

import com.limelight.bean.SettingBean;

import java.util.List;


public class EventHub {

    public final static MutableLiveData<Void> liveData_changeLanguage = new MutableLiveData<>();
    public final static MutableLiveData<List<SettingBean>> liveDataSettingList = new MutableLiveData<>();

}
