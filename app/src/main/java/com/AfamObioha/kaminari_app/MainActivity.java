package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import java.io.File;
import java.lang.System;

import org.qtproject.qt.android.QtNative;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "📂 Application Context: " + getApplicationContext());
        Log.d(TAG, "📂 Native Library Path: " + getApplicationInfo().nativeLibraryDir);
        Log.d(TAG, "📂 Source APK Path: " + getApplicationInfo().sourceDir);
        Log.d(TAG, "📂 Data Directory: " + getApplicationInfo().dataDir);

        // ✅ Ensure Qt libraries are correctly loaded
        loadQtLibraries();

        // 🚀 Start Qt Application
        try {
            Log.d(TAG, "🚀 Starting Qt application...");
            QtNative.startApplication(getApplicationInfo().sourceDir, getApplicationInfo().dataDir);
            Log.d(TAG, "✅ Qt application started.");
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to start Qt application", e);
        }
    }

    private void loadQtLibraries() {
        // ✅ Directly load libraries from nativeLibraryDir
        String libPath = getApplicationInfo().nativeLibraryDir;

        String[] qtLibs = {
                "c++_shared",
                "Qt6Core",
                "Qt6Gui",
                "Qt6Widgets",
                "Qt6Qml",
                "Qt6Quick",
                "Qt6Network",
                "Qt6QuickTemplates2",
                "Qt6QuickControls2Basic",
                "Qt6QmlModels",
                "Qt6QuickParticles"
        };

        for (String lib : qtLibs) {
            try {
                System.load(libPath + "/lib" + lib + ".so");
                Log.d(TAG, "✅ Loaded " + lib);
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "❌ Failed to load " + lib, e);
            }
        }
    }
}