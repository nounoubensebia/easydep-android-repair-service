package com.example.nouno.easydep_repairservice;

import android.app.Application;
import android.content.Context;

/**
 * Created by nouno on 23/05/2017.
 */

public class App extends Application {
    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}
