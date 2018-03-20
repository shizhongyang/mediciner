package com.elink.health.mediciner.app;

import android.app.Application;

import com.mob.MobSDK;

import io.rong.imkit.RongIM;

/**
 * Created by TT on 2018-01-15.
 */

public class MobApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this);
        RongIM.init(this);
    }
}
