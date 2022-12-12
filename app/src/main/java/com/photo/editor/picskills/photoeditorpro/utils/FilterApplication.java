package com.photo.editor.picskills.photoeditorpro.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;

public class FilterApplication extends MultiDexApplication {

    public static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        FilterApplication.context = context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        setContext(getApplicationContext());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        FirebaseApp.initializeApp(this);
    }
}
