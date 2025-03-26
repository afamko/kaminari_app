package org.qtproject.qt6.android.bindings;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class QtApplication extends Application {
    private static final String TAG = "QtApplication";
    private static QtApplication m_instance;

    public QtApplication() {
        m_instance = this;
    }

    public static QtApplication getInstance() {
        return m_instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "QtApplication onCreate");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "QtApplication onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "QtApplication onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "QtApplication onTerminate");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG, "QtApplication attachBaseContext");
    }
}
