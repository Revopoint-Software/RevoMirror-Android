package com.limelight.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.limelight.App;

import java.util.Set;

public class SharedPreferenceUtil {

    private static Context mcontext;

    public static void init(Context context) {
        mcontext = App.getContext();
    }

    static {
        mcontext = App.getContext();
    }

    private static final String SETTING = "setting";
    private static final String STORE_INFO = "storeInfo";

    public static void setStringValue(String key, String value) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void setIntValue(String key, int value) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static void setLongValue(String key, long value) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private static void setFloatValue(String key, float value) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    private static void setBoolValue(String key, boolean value) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static void setStringSet(String key, Set<String> value) {
        SharedPreferences storeInfo = mcontext.getSharedPreferences(STORE_INFO, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storeInfo.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    private static void setStoreString(String key, String value) {
        SharedPreferences storeInfo = mcontext.getSharedPreferences(STORE_INFO, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storeInfo.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setStoreBoolean(String key, boolean value) {
        SharedPreferences storeInfo = mcontext.getSharedPreferences(STORE_INFO, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storeInfo.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getBoolValue(String key, boolean def) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return userSettings.getBoolean(key, def);
    }

    private static int getIntValue(String key, int def) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return userSettings.getInt(key, def);
    }

    private static long getLongValue(String key, long def) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return userSettings.getLong(key, def);
    }

    private static float getFloatValue(String key, float def) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return userSettings.getFloat(key, def);
    }

    public static String getStringValue(String key, String def) {
        SharedPreferences userSettings = mcontext.getSharedPreferences(SETTING, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return userSettings.getString(key, def);
    }

    private static Set<String> getStringSet(String key, Set<String> values) {
        SharedPreferences storeInfo = mcontext.getSharedPreferences(STORE_INFO, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return storeInfo.getStringSet(key, values);
    }

    private static String getStoreString(String key, String values) {
        SharedPreferences storeInfo = mcontext.getSharedPreferences(STORE_INFO, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return storeInfo.getString(key, values);
    }

    public static boolean getStoreBoolean(String key, boolean value) {
        SharedPreferences storeInfo = mcontext.getSharedPreferences(STORE_INFO, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return storeInfo.getBoolean(key, value);
    }



    public static void setLanguageLocal(String language) {
        setStringValue("language", language);
    }

    public static String getLanguageLocal() {
        return getStringValue("language", "");
    }

    public static void setLastDeviceUuid(String deviceUuid) {
        setStringValue("deviceUuid", deviceUuid);
    }

    public static String getLastDeviceUuid() {
        return getStringValue("deviceUuid", null);
    }
}
