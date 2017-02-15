package com.kazyle.hgclient.ui;

import android.app.Application;

//import com.beardedhen.androidbootstrap.TypefaceProvider;

import com.tencent.smtt.sdk.QbSdk;

import org.xutils.x;

import im.fir.sdk.FIR;

/**
 * Created by Kazyle on 2016/8/25.
 */
public class HGApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        TypefaceProvider.registerDefaultIconSets();
        x.Ext.init(this);
        FIR.init(this);
        QbSdk.initX5Environment(this, null);
    }
}
