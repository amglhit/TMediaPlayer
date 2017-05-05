package com.amgl.tmediaplayer;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/5.
 */

public class TApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
