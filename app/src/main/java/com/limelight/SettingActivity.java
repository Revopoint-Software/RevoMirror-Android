package com.limelight;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.limelight.bean.LanguageSettingItem;
import com.limelight.bean.SettingBean;
import com.limelight.bean.SettingItem;
import com.limelight.ui.activity.BaseActivity;
import com.limelight.ui.adapter.SettingItemAdapter;
import com.limelight.utils.EventHub;
import com.limelight.utils.LanguageUtils;
import com.limelight.utils.ScreenUtil;
import com.limelight.utils.SharedPreferenceUtil;
import com.limelight.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends BaseActivity {
    private View layoutLanguage, layoutAbout;
    private View layoutContentLanguage, layoutContentAbout;
    private RecyclerView rvLanguageItem;
    private SettingItemAdapter languageItemAdapter;
    private TextView tvBack, tvSetting, tvLanguage, tvAbout, tvSelectedLanguage, tvVersion, tvUserRule, tvPrivacyPolicy, tvGplv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        initView();
        registeObserver();
        initData();
    }

    private void initView() {
        ScreenUtil.adapterDisplayCutout(findViewById(R.id.contentView));

        tvBack = findViewById(R.id.tvBack);
        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSetting = findViewById(R.id.tvSetting);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvAbout = findViewById(R.id.tvAbout);

        layoutLanguage = findViewById(R.id.layoutLanguage);
        layoutLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLanguage.setSelected(true);
                layoutAbout.setSelected(false);
                layoutContentLanguage.setVisibility(View.VISIBLE);
                layoutContentAbout.setVisibility(View.GONE);
            }
        });
        layoutAbout = findViewById(R.id.layoutAbout);
        layoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLanguage.setSelected(false);
                layoutAbout.setSelected(true);
                layoutContentLanguage.setVisibility(View.GONE);
                layoutContentAbout.setVisibility(View.VISIBLE);
            }
        });
        layoutContentLanguage = findViewById(R.id.layoutContentLanguage);
        layoutContentAbout = findViewById(R.id.layoutContentAbout);

        layoutLanguage.setSelected(true);
        layoutAbout.setSelected(false);

        //多语言
        tvSelectedLanguage = findViewById(R.id.tvSelectedLanguage);
        rvLanguageItem = findViewById(R.id.rvLanguageItem);
        rvLanguageItem.setLayoutManager(new LinearLayoutManager(this));
        languageItemAdapter = new SettingItemAdapter();
        rvLanguageItem.setAdapter(languageItemAdapter);
        languageItemAdapter.setItemClickCallback(new SettingItemAdapter.SettingItemCallback() {
            @Override
            public void onCallback(SettingItem item) {
                if (item instanceof LanguageSettingItem) {
                    applySelectLanguage((LanguageSettingItem) item);
                    tvSelectedLanguage.setText(item.getTitle());
                }
            }
        });

        //版本号
        tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(Utils.getAppVersionName());

        //用户协议
        tvUserRule = findViewById(R.id.tvUserRule);
        tvUserRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialActivity.startWebView(
                        SettingActivity.this,
                        TutorialActivity.TYPE_USER_LISENCE,
                        LanguageUtils.getString(R.string.UserPolicy)
                );
            }
        });

        //隐私政策
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialActivity.startWebView(
                        SettingActivity.this,
                        TutorialActivity.TYPE_PRIVACY_POLICY,
                        LanguageUtils.getString(R.string.PrivatePolicy)
                );
            }
        });

        //隐私政策
        tvGplv3 = findViewById(R.id.tvGplv3);
        tvGplv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialActivity.startWebView(
                        SettingActivity.this,
                        TutorialActivity.TYPE_GPLV3,
                        LanguageUtils.getString(R.string.Gplv3)
                );
            }
        });
    }

    private void registeObserver() {
        EventHub.liveDataSettingList.observe(this, new Observer<List<SettingBean>>() {
            @Override
            public void onChanged(List<SettingBean> settingBeans) {
                languageItemAdapter.itemValueSelect = getItemSelectValue(0);
                List<SettingItem> itemList = settingBeans.get(0).itemList;
                languageItemAdapter.setList(itemList);
                for (SettingItem item : itemList) {
                    if (languageItemAdapter.itemValueSelect == item.getValue()) {
                        tvSelectedLanguage.setText(item.getTitle());
                    }
                }
            }
        });
        EventHub.liveData_changeLanguage.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                tvBack.setText(LanguageUtils.getString(R.string.Back));
                tvSetting.setText(LanguageUtils.getString(R.string.Setting));
                tvLanguage.setText(LanguageUtils.getString(R.string.Language));
                tvAbout.setText(LanguageUtils.getString(R.string.About));

                tvUserRule.setText(LanguageUtils.getString(R.string.UserPolicy));
                tvPrivacyPolicy.setText(LanguageUtils.getString(R.string.PrivatePolicy));
                requestSettingList();
            }
        });
    }

    private void initData() {
        requestSettingList();
    }

    private void requestSettingList() {
        List<SettingBean> list = new ArrayList<>();
        SettingBean settingBean = new SettingBean(R.drawable.ic_setting_language, LanguageUtils.getString(R.string.Language), true);
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getString(R.string.AutoSystem), LanguageUtils.LanguageType.SYS));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType.ZH_CN), LanguageUtils.LanguageType.ZH_CN));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType.ZH_TW), LanguageUtils.LanguageType.ZH_TW));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._EN), LanguageUtils.LanguageType._EN));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._JA), LanguageUtils.LanguageType._JA));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._ES), LanguageUtils.LanguageType._ES));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._FR), LanguageUtils.LanguageType._FR));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._DE), LanguageUtils.LanguageType._DE));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._KO), LanguageUtils.LanguageType._KO));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._IT), LanguageUtils.LanguageType._IT));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._RU), LanguageUtils.LanguageType._RU));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._PT), LanguageUtils.LanguageType._PT));
        settingBean.itemList.add(new LanguageSettingItem(LanguageUtils.getLanguaDesc(LanguageUtils.LanguageType._TR), LanguageUtils.LanguageType._TR));
        list.add(settingBean);

        settingBean = new SettingBean(R.drawable.ic_setting_about, LanguageUtils.getString(R.string.About), true);
        settingBean.itemList.add(new SettingItem(0, LanguageUtils.getString(R.string.About)));
        list.add(settingBean);

        EventHub.liveDataSettingList.postValue(list);
    }

    private void applySelectLanguage(LanguageSettingItem languageSettingItem) {
        SharedPreferenceUtil.setLanguageLocal(languageSettingItem.getLanguageType().name());
        LanguageUtils.changeAppLanguage(this);
        EventHub.liveData_changeLanguage.postValue(null);
    }

    private int getItemSelectValue(int groupPos) {
        if (groupPos == 0) {
            //语言设置
            return LanguageUtils.LanguageType.get(SharedPreferenceUtil.getLanguageLocal()).ordinal();
        }
        return 0;
    }
}
