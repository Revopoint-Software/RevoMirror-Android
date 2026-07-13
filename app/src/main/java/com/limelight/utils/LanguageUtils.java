package com.limelight.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.limelight.App;
import com.limelight.R;

import java.util.Locale;


public class LanguageUtils {
    private static final String TAG = "LanguageUtils";

    public enum LanguageType {
        ZH_CN,
        ZH_TW,
        _DE,
        _ES,
        _FR,
        _IT,
        _JA,
        _EN,
        _KO,
        _PT,
        _RU,
        _TR,
        SYS;

        public static LanguageType get(String name) {
            LanguageType languageType = SYS;
            if (!TextUtils.isEmpty(name)) {
                try {
                    languageType = LanguageType.valueOf(name);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return languageType;
        }
    }

    private static final String ZH_CN = "ZH_CN";
    private static final String ZH_TW = "ZH_TW";
    private static final String _DE = "_DE";
    private static final String _ES = "_ES";
    private static final String _FR = "_FR";
    private static final String _IT = "_IT";
    private static final String _JA = "_JA";
    private static final String _EN = "_EN";
    private static final String _KO = "_KO";
    private static final String _PT = "_PT";
    private static final String _RU = "_RU";
    private static final String _TR = "_TR";
    private static final String SYS = "SYS";

    /**
     * 旧版本多语言设置,新版本要做兼容
     */
    public static final String[] oldVersionLangs = {
            LanguageType.ZH_CN.name(), LanguageType.ZH_TW.name(), LanguageType._EN.name(), LanguageType._DE.name(),
            LanguageType._ES.name(), LanguageType._FR.name(), LanguageType._IT.name(), LanguageType._JA.name(),
            LanguageType._KO.name(), LanguageType.SYS.name()
    };

    private static LocaleList sLocaleList;

    static {
        //由于API仅支持7.0，需要判断，否则程序会crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sLocaleList = LocaleList.getDefault();
        }
    }

    public static void changeAppLanguage(Context context) {
        String lang = SharedPreferenceUtil.getLanguageLocal();
        Locale sysLocale = getSysPreferredLocale();
        Log.e(TAG, "lang000----=" + lang + ";----SYS=" + (sysLocale.getLanguage() + "_" + sysLocale.getCountry()).toUpperCase());
        if (!TextUtils.isEmpty(lang)) {
            LanguageType languageType = LanguageType.get(lang);
            Locale locale;
            switch (languageType) {
                case _EN:
                    locale = Locale.ENGLISH;
                    break;
                case ZH_CN:
                    locale = Locale.CHINA;
                    break;
                case ZH_TW:
                    locale = Locale.TRADITIONAL_CHINESE;
                    break;
                case _JA:
                    locale = Locale.JAPAN;
                    break;
                case _FR:
                    locale = Locale.FRANCE;
                    break;
                case _ES:
                    locale = new Locale("ES");
                    break;
                case _KO:
                    //韩国
                    locale = new Locale("KO");
                    break;
                case _IT:
                    //意大利
                    locale = new Locale("IT");
                    break;
                case _PT:
                    //葡萄牙
                    locale = new Locale("PT");
                    break;
                case _DE:
                    //德国
                    locale = Locale.GERMAN;
                    break;
                case _RU:
                    //俄语
                    locale = new Locale("RU");
                    break;
                case _TR:
                    //土耳其语
                    locale = new Locale("TR");
                    break;
                default:
                    locale = sLocaleList.get(0);
                    Log.e(TAG, "lang222=" + ";SYS=" + (locale.getLanguage() + "_" + locale.getCountry()));
                    if (locale.getLanguage().equals("ar")) {
                        locale = Locale.ENGLISH;
                    }
                    break;
            }
            Log.e(TAG, "lang111=" + ";SYS=" + (locale.getLanguage() + "_" + locale.getCountry()).toUpperCase());
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(locale);
            res.updateConfiguration(conf, dm);
        } else {
//            for(String language:langs){
//                if(language.equals((sysLocale.getLanguage()+"_"+sysLocale.getCountry()).toUpperCase()) ||
//                        language.equals(("_"+sysLocale.getCountry()).toUpperCase())){
////                    Log.e(TAG,"lang111="+language+";SYS="+(sysLocale.getLanguage()+"_"+sysLocale.getCountry()).toUpperCase());
//                    SharedPreferenceUtil.setLanguageLocal(language);
//                    changeAppLanguage(context);
//                    break;
//                }
//            }
//            if(TextUtils.isEmpty(SharedPreferenceUtil.getLanguageLocal())){
////                Log.e(TAG,"lang2222-EN----=");
//                SharedPreferenceUtil.setLanguageLocal(_EN);
//                changeAppLanguage(context);
//            }
        }
    }

    /**
     * 获取系统首选语言
     * <p>
     * 注意：该方法获取的是用户实际设置的不经API调整的系统首选语言
     *
     * @return
     */
    public static Locale getSysPreferredLocale() {
        Locale locale;
        //7.0以下直接获取系统默认语言
        if (Build.VERSION.SDK_INT < 24) {
            // 等同于context.getResources().getConfiguration().locale;
            locale = Locale.getDefault();
            // 7.0以上获取系统首选语言
        } else {
            /*
             * 以下两种方法等价，都是获取经API调整过的系统语言列表（可能与用户实际设置的不同）
             * 1.context.getResources().getConfiguration().getLocales()
             * 2.LocaleList.getAdjustedDefault()
             */
            // 获取用户实际设置的语言列表
            locale = LocaleList.getDefault().get(0);
        }
        return locale;
    }


    public static String getStringByLocalId(int strId) {
        Resources res = App.getContext().getResources();
        if (res != null) {
            Resources resourcesLocal = getResourcesByLocale(res); //得到指定语言的资源
            return resourcesLocal.getString(strId);
        }
        return "";
    }

    public static String getStringByLocal(Context mContext, int strId) {
        Resources res = mContext.getResources();
        if (res != null) {
            Resources resourcesLocal = getResourcesByLocale(res); //得到指定语言的资源
            return resourcesLocal.getString(strId);
        }
        return "";
    }

    private static Resources getResourcesByLocale(Resources res) {
        Configuration conf = new Configuration(res.getConfiguration());
        String lang = SharedPreferenceUtil.getLanguageLocal();
        //Log.e(TAG,"lang------------="+lang);
        Locale locale;
        LanguageType languageType = LanguageType.get(lang);
        switch (languageType) {
            case ZH_CN:
                locale = Locale.CHINA;
                break;
            case ZH_TW:
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case _EN:
                locale = Locale.ENGLISH;
                break;
            case _JA:
                locale = Locale.JAPAN;
                break;
            case _FR:
                locale = Locale.FRANCE;
                break;
            case _ES:
                locale = new Locale("ES");
                break;
            case _KO:
                //韩国
                locale = new Locale("KO");
                break;
            case _IT:
                //意大利
                locale = new Locale("IT");
                break;
            case _PT:
                //葡萄牙
                locale = new Locale("PT");
                break;
            case _DE:
                //德国
                locale = Locale.GERMAN;
                break;
            case _RU:
                //俄语
                locale = new Locale("RU");
                break;
            case _TR:
                //土耳其语
                locale = new Locale("TR");
                break;
            default:
                locale = sLocaleList.get(0);
//              Log.e(TAG,"lang222="+";SYS="+(locale.getLanguage()+"_"+locale.getCountry()));
                if (locale.getLanguage().equals("ar")) {
                    locale = Locale.ENGLISH;
                }
                break;
        }
        conf.setLocale(locale);
        return new Resources(res.getAssets(), res.getDisplayMetrics(), conf);

    }


    /**
     * 系统默认描述
     *
     * @return
     */
    public static int getSysPreferredLocaleId() {
        Locale sysLocale = getSysPreferredLocale();
        String dataStr = (sysLocale.getLanguage() + "_" + sysLocale.getCountry()).toUpperCase();
        Log.e(TAG, "dataStr-" + dataStr);
        int locale;
        LanguageType languageType = LanguageType.get(dataStr);
        switch (languageType) {
            case ZH_CN:
                locale = R.string.lang_simple_chinese;
                break;
            case ZH_TW:
                locale = R.string.lang_tw_chinese;
                break;
            case _EN:
                locale = R.string.lang_en;
                break;
            case _JA:
                locale = R.string.lang_ja;
                break;
            case _FR:
                locale = R.string.lang_fr;
                break;
            case _ES:
                locale = R.string.lang_es;
                break;
            case _KO:
                //韩国
                locale = R.string.lang_ko;
                break;
            case _IT:
                //意大利
                locale = R.string.lang_it;
                break;
            case _PT:
                //葡萄牙
                locale = R.string.lang_pt;
                break;
            case _DE:
                //德国
                locale = R.string.lang_de;
                break;
            case _RU:
                //俄语
                locale = R.string.lang_ru;
                break;
            case _TR:
                //土耳其
                locale = R.string.lang_tr;
                break;
            default:
                locale = R.string.lang_en;
                break;
        }
        return locale;
    }

    /**
     * 系统默认描述
     *
     * @return
     */
    public static Locale getSysPreferredLocaleDefault() {
        String lang = SharedPreferenceUtil.getLanguageLocal();
        Locale locale;
        LanguageType languageType = LanguageType.get(lang);
        switch (languageType) {
            case ZH_CN:
                locale = Locale.CHINA;
                break;
            case ZH_TW:
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case _EN:
                locale = Locale.ENGLISH;
                break;
            case _JA:
                locale = Locale.JAPAN;
                break;
            case _FR:
                locale = Locale.FRANCE;
                break;
            case _ES:
                locale = new Locale("ES");
                break;
            case _KO:
                //韩国
                locale = new Locale("KO");
                break;
            case _IT:
                //意大利
                locale = new Locale("IT");
                break;
            case _PT:
                //葡萄牙
                locale = new Locale("PT");
                break;
            case _DE:
                //德国
                locale = Locale.GERMAN;
                break;
            case _RU:
                //俄语
                locale = new Locale("RU");
                break;
            case _TR:
                //土耳其语
                locale = new Locale("TR");
                break;
            default:
                locale = Locale.ENGLISH;
                break;
        }
        return locale;
    }

    public static void setSystemLocaleList(LocaleList localeList) {
        sLocaleList = localeList;
    }


    public static String getLangName() {
        String lang = SharedPreferenceUtil.getLanguageLocal();
        String localeStr;
        LanguageType languageType = LanguageType.get(lang);
        switch (languageType) {
            case ZH_CN:
                localeStr = "zh_CN";
                break;
            case ZH_TW:
                localeStr = "zh_HK";
                break;
            case _EN:
                localeStr = "en_US";
                break;
            case _JA:
                localeStr = "ja";
                break;
            case _FR:
                localeStr = "fr";
                break;
            case _ES:
                localeStr = "es";
                break;
            case _KO:
                //韩国
                localeStr = "ko";
                break;
            case _IT:
                //意大利
                localeStr = "it";
                break;
            case _PT:
                //葡萄牙
                localeStr = "pt";
                break;
            case _DE:
                //德国
                localeStr = "de";
                break;
            case _RU:
                //俄语
                localeStr = "ru";
                break;
            case _TR:
                //土耳其语
                localeStr = "tr";
                break;
            default:
                Locale locale = sLocaleList.get(0);
                if (locale.getLanguage().equals("ar")) {
                    localeStr = "en_US";
                } else {
                    localeStr = getLanNameFromLocal(locale);
                }
                break;
        }

        return localeStr;
    }

    private static String getLanNameFromLocal(Locale locale) {
        String localeStr;
        String str = locale.getLanguage() + "_" + locale.getCountry().toUpperCase();
//        Log.e(TAG,"getLanNameFromLocal---STR="+str);
        if (str.equals("zh_CN")) {
            localeStr = "zh_CN";
        } else if (str.equals("zh_TW")) {
            localeStr = "zh_HK";
        } else {
            String contray = "_" + locale.getLanguage().toUpperCase();
            switch (contray) {
                case LanguageUtils._EN:
                    localeStr = "en_US";
                    break;
                case LanguageUtils._JA:
                    localeStr = "ja_JP";
                    break;
                case LanguageUtils._FR:
                    localeStr = "fr_FR";
                    break;
                case LanguageUtils._ES:
                    localeStr = "es_ES";
                    break;
                case LanguageUtils._KO:
                    //韩国
                    localeStr = "ko_KR";
                    break;
                case LanguageUtils._IT:
                    //意大利
                    localeStr = "it_IT";
                    break;
                case LanguageUtils._PT:
                    //葡萄牙
                    localeStr = "pt_PT";
                    break;
                case LanguageUtils._DE:
                    //德国
                    localeStr = "de_DE";
                    break;
                case LanguageUtils._RU:
                    //俄罗斯
                    localeStr = "ru_RU";
                    break;
                case LanguageUtils._TR:
                    //土耳其
                    localeStr = "tr_TR";
                    break;
                default:
                    localeStr = "en_US";
                    break;
            }
        }
        return localeStr;
    }

    public static String getLanguaDesc(LanguageType languageType) {
        String localeStr = "";
        switch (languageType) {
            case ZH_CN:
                localeStr = App.getContext().getString(R.string.lang_simple_chinese);
                break;
            case ZH_TW:
                localeStr = App.getContext().getString(R.string.lang_tw_chinese);
                break;
            case _EN:
                localeStr = App.getContext().getString(R.string.lang_en);
                break;
            case _JA:
                localeStr = App.getContext().getString(R.string.lang_ja);
                break;
            case _FR:
                localeStr = App.getContext().getString(R.string.lang_fr);
                break;
            case _ES:
                localeStr = App.getContext().getString(R.string.lang_es);
                break;
            case _KO:
                //韩国
                localeStr = App.getContext().getString(R.string.lang_ko);
                break;
            case _IT:
                //意大利
                localeStr = App.getContext().getString(R.string.lang_it);
                break;
            case _PT:
                //葡萄牙
                localeStr = App.getContext().getString(R.string.lang_pt);
                break;
            case _DE:
                //德国
                localeStr = App.getContext().getString(R.string.lang_de);
                break;
            case _RU:
                //俄罗斯
                localeStr = App.getContext().getString(R.string.lang_ru);
                break;
            case _TR:
                //土耳其
                localeStr = App.getContext().getString(R.string.lang_tr);
                break;
        }
        return localeStr;
    }

    public static String getString(int stringId) {
//        if(ActivtiyLifecycleUtil.getInstance().currentActivity() != null){
//            return ActivtiyLifecycleUtil.getInstance().currentActivity().getString(stringId);
//        }
//        return "";
        Resources res = App.getContext().getResources();
        if (res != null) {
            Resources resourcesLocal = getResourcesByLocale(res); //得到指定语言的资源
            return resourcesLocal.getString(stringId);
        }
        return "";
    }

    public static String getString(Context context, int stringId) {
        Resources res = context.getApplicationContext().getResources();
        if (res != null) {
            Resources resourcesLocal = getResourcesByLocale(res); //得到指定语言的资源
            return resourcesLocal.getString(stringId);
        }
        return "";
    }

    public static String getString(int stringId, Object... formatArgs) {
//        if(ActivtiyLifecycleUtil.getInstance().currentActivity() != null){
//            return ActivtiyLifecycleUtil.getInstance().currentActivity().getString(stringId);
//        }
//        return "";
        Resources res = App.getContext().getResources();
        if (res != null) {
            Resources resourcesLocal = getResourcesByLocale(res); //得到指定语言的资源
            return resourcesLocal.getString(stringId, formatArgs);
        }
        return "";
    }

    /**
     * 判断是否中国大陆地区
     *
     * @return
     */
    public static boolean checkIsChinaInlandArea() {
        Locale sysLocale = getSysPreferredLocale();
        String dataStr = (sysLocale.getLanguage() + "_" + sysLocale.getCountry()).toUpperCase();
        if (TextUtils.equals(dataStr, ZH_CN)) {
            return true;
        }
        return false;
    }

    /**
     * 当前应用设置语言是否中文或者跟随系统
     *
     * @return
     */
    public static boolean checkAppIsChinaLanguageOrDefault() {
        String lan = SharedPreferenceUtil.getLanguageLocal();
        if (TextUtils.equals(lan.toUpperCase(), ZH_CN) || TextUtils.equals(lan.toUpperCase(), "") || TextUtils.equals(lan.toUpperCase(), SYS)) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前真实的语言类型，如果当前是跟随系统，会获取系统当前的语言类型
     * @return
     */
    public static LanguageType getCurLanguageType() {
        LanguageType languageType = LanguageType.get(SharedPreferenceUtil.getLanguageLocal());
        if (languageType == LanguageType.SYS) {
            Locale sysLocale = LanguageUtils.getSysPreferredLocale();
            String dataStr = (sysLocale.getLanguage() + "_" + sysLocale.getCountry()).toUpperCase();
            languageType = LanguageType.get(dataStr);
        }
        return languageType;
    }
}
