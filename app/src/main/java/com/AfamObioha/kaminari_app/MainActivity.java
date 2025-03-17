package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Correct QtActivityDelegate import (no bindings!)
import org.qtproject.qt.android.QtActivityDelegate;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI to show loading status
        TextView textView = new TextView(this);
        textView.setPadding(30, 30, 30, 30);
        textView.setText("Kaminari App - Qt Libraries Status\n\nLoading libraries...");
        setContentView(textView);

        Log.d(TAG, "📂 Native Library Path: " + getApplicationInfo().nativeLibraryDir);
        listFilesInDir(getApplicationInfo().nativeLibraryDir);

        boolean librariesLoaded = loadQtLibraries();
        if (librariesLoaded) {
            Toast.makeText(this, "✅ Qt libraries loaded!", Toast.LENGTH_LONG).show();
            textView.append("\n\n✅ Successfully loaded Qt libraries!");

            File dir = new File(getApplicationInfo().nativeLibraryDir);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().contains("Qt6") || file.getName().contains("c++_shared")) {
                        textView.append("\n• " + file.getName());
                    }
                }
            }

            textView.append("\n\n🚀 Starting Qt application...");
            startQtApplication(textView);
        } else {
            Toast.makeText(this, "❌ Failed to load Qt libs", Toast.LENGTH_LONG).show();
            textView.append("\n\n❌ Failed to load some Qt libraries.");
        }
    }

    private void startQtApplication(TextView textView) {
        try {
            Log.d(TAG, "🚀 Preparing to start Qt application...");

            // Load native library
            try {
                System.loadLibrary("kaminari_app");
                Log.d(TAG, "✅ Loaded native lib: kaminari_app");
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "⚠️ App lib not found: " + e.getMessage());
            }

            // Create instance of QtActivityDelegate and start app
            Log.d(TAG, "📢 Instantiating QtActivityDelegate...");
            QtActivityDelegate delegate = new QtActivityDelegate(); // ← Must instantiate
            delegate.startApplication(); // ← Correct instance method call

            Log.d(TAG, "✅ QtActivityDelegate.startApplication() called.");
            Toast.makeText(this, "QtApplication started!", Toast.LENGTH_SHORT).show();
            textView.append("\n\n✅ QtApplication started!");
        } catch (Throwable e) {
            Log.e(TAG, "❌ Qt UI start failed", e);
            Toast.makeText(this, "Qt UI failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void listFilesInDir(String dirPath) {
        File dir = new File(dirPath);
        Log.d(TAG, "📂 Listing files in: " + dirPath);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    Log.d(TAG, "📄 Found: " + file.getName());
                }
            }
        }
    }

    private boolean loadQtLibraries() {
        List<String> failedLibs = new ArrayList<>();
        List<String> successLibs = new ArrayList<>();

        try {
            System.loadLibrary("c++_shared");
            Log.d(TAG, "✅ Loaded: c++_shared");
            successLibs.add("c++_shared");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ Failed: c++_shared");
            failedLibs.add("c++_shared");
        }

        String[] qtLibs = {
                "Qt6Core_arm64-v8a", "Qt6Network_arm64-v8a", "Qt6Gui_arm64-v8a",
                "Qt6OpenGL_arm64-v8a", "Qt6ShaderTools_arm64-v8a", "Qt6Qml_arm64-v8a",
                "Qt6QmlModels_arm64-v8a", "Qt6QmlWorkerScript_arm64-v8a", "Qt6Quick_arm64-v8a",
                "Qt6QuickTemplates2_arm64-v8a", "Qt6QuickControls2_arm64-v8a",
                "Qt6QuickControls2Impl_arm64-v8a", "Qt6QuickControls2BasicStyleImpl_arm64-v8a",
                "Qt6QuickControls2Basic_arm64-v8a", "Qt6QuickControls2MaterialStyleImpl_arm64-v8a",
                "Qt6QuickControls2Material_arm64-v8a", "Qt6QuickControls2FusionStyleImpl_arm64-v8a",
                "Qt6QuickControls2Fusion_arm64-v8a", "Qt6QuickControls2ImagineStyleImpl_arm64-v8a",
                "Qt6QuickControls2Imagine_arm64-v8a", "Qt6QuickControls2UniversalStyleImpl_arm64-v8a",
                "Qt6QuickControls2Universal_arm64-v8a", "Qt6Widgets_arm64-v8a", "Qt6QuickParticles_arm64-v8a"
        };

        for (String lib : qtLibs) {
            try {
                String path = getApplicationInfo().nativeLibraryDir + "/lib" + lib + ".so";
                File libFile = new File(path);
                if (libFile.exists()) {
                    Log.d(TAG, "🔄 Loading: " + path);
                    System.load(path);
                    Log.d(TAG, "✅ Loaded: " + lib);
                    successLibs.add(lib);
                } else {
                    Log.e(TAG, "❌ Missing file: " + path);
                    failedLibs.add(lib);
                }
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "❌ Failed to load " + lib + ": " + e.getMessage());
                failedLibs.add(lib);
            }
        }

        Log.d(TAG, "✅ Loaded " + successLibs.size() + " Qt libraries");
        if (!failedLibs.isEmpty()) {
            Log.e(TAG, "❌ Failed libs: " + failedLibs);
            return failedLibs.size() < qtLibs.length / 2;
        }

        return true;
    }
}