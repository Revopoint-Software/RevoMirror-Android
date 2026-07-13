package com.limelight;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.limelight.utils.LanguageUtils;

public class App extends Application {

    private static App app;

    public static App getContext() {
        return app;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public Handler getHandler() {
        return app.mHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                LanguageUtils.changeAppLanguage(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                LanguageUtils.changeAppLanguage(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

    }

}
