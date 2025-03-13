package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.qtproject.qt.android.QtNative;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Debug information
        Log.d(TAG, "📂 Application Context: " + getApplicationContext());
        Log.d(TAG, "📂 Native Library Path: " + getApplicationInfo().nativeLibraryDir);

        // List files in native library directory
        listFilesInDir(getApplicationInfo().nativeLibraryDir);

        // Load Qt libraries in proper dependency order
        boolean librariesLoaded = loadQtLibraries();
        if (!librariesLoaded) {
            Toast.makeText(this, "Failed to load Qt libraries. Check logs for details.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Add a slight delay before starting Qt to ensure all libraries are properly
        // initialized
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startQtApplication();
            }
        }, 500); // 500ms delay
    }

    private void startQtApplication() {
        try {
            Log.d(TAG, "🚀 Starting Qt application...");

            // Try to load the app's native library if it exists
            try {
                String appName = "kaminari_app"; // Your app's native library name without "lib" prefix or ".so" suffix
                System.loadLibrary(appName);
                Log.d(TAG, "✅ Successfully loaded application library: " + appName);
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "⚠️ Failed to load application library, continuing anyway: " + e.getMessage());
            }

            // Start Qt application
            QtNative.startApplication(getApplicationInfo().sourceDir, getApplicationInfo().dataDir);
            Log.d(TAG, "✅ Qt application started.");
        } catch (Throwable e) {
            Log.e(TAG, "❌ Failed to start Qt application", e);
            Toast.makeText(this, "Failed to start Qt application: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void listFilesInDir(String dirPath) {
        File dir = new File(dirPath);
        Log.d(TAG, "📂 Listing files in: " + dirPath);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    Log.d(TAG, "📄 Found: " + file.getName());
                }
            } else {
                Log.e(TAG, "❌ No files found in directory");
            }
        } else {
            Log.e(TAG, "❌ Directory does not exist or is not a directory");
        }
    }

    private boolean loadQtLibraries() {
        List<String> failedLibs = new ArrayList<>();

        try {
            // Load c++ shared library first
            System.loadLibrary("c++_shared");
            Log.d(TAG, "✅ Successfully loaded: c++_shared");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ Failed to load c++_shared: " + e.getMessage());
            failedLibs.add("c++_shared");
        }

        // Libraries in correct dependency order WITH the arm64-v8a suffix
        String[] qtLibs = {
                "Qt6Core_arm64-v8a",
                "Qt6Network_arm64-v8a",
                "Qt6Gui_arm64-v8a",
                "Qt6OpenGL_arm64-v8a",
                "Qt6ShaderTools_arm64-v8a",
                "Qt6Qml_arm64-v8a",
                "Qt6QmlModels_arm64-v8a",
                "Qt6QmlWorkerScript_arm64-v8a",
                "Qt6Quick_arm64-v8a",
                "Qt6QuickTemplates2_arm64-v8a",
                "Qt6QuickControls2_arm64-v8a",
                "Qt6QuickControls2Impl_arm64-v8a",
                "Qt6QuickControls2BasicStyleImpl_arm64-v8a",
                "Qt6QuickControls2Basic_arm64-v8a",
                "Qt6QuickControls2MaterialStyleImpl_arm64-v8a",
                "Qt6QuickControls2Material_arm64-v8a",
                "Qt6QuickControls2FusionStyleImpl_arm64-v8a",
                "Qt6QuickControls2Fusion_arm64-v8a",
                "Qt6QuickControls2ImagineStyleImpl_arm64-v8a",
                "Qt6QuickControls2Imagine_arm64-v8a",
                "Qt6QuickControls2UniversalStyleImpl_arm64-v8a",
                "Qt6QuickControls2Universal_arm64-v8a",
                "Qt6Widgets_arm64-v8a",
                "Qt6QuickParticles_arm64-v8a",
                // Try to load "QtAndroidPlugin" if it exists - may be needed for
                // QtNative.startQtAndroidPlugin
                "QtAndroidPlugin_arm64-v8a"
        };

        // Try to load each library using full paths
        for (String lib : qtLibs) {
            try {
                String fullPath = getApplicationInfo().nativeLibraryDir + "/lib" + lib + ".so";
                File libFile = new File(fullPath);
                if (libFile.exists()) {
                    Log.d(TAG, "🔄 Loading: " + fullPath);
                    System.load(fullPath);
                    Log.d(TAG, "✅ Successfully loaded: " + lib);
                } else {
                    Log.e(TAG, "❌ Library file does not exist: " + fullPath);
                    failedLibs.add(lib);
                }
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "❌ Failed to load " + lib + ": " + e.getMessage());
                failedLibs.add(lib);
            }
        }

        // Look for additional Qt libraries in the lib directory
        try {
            File libDir = new File(getApplicationInfo().nativeLibraryDir);
            if (libDir.exists() && libDir.isDirectory()) {
                File[] files = libDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        // Try to load any Qt-related libraries we might have missed
                        if (name.startsWith("libQt") && name.endsWith(".so") && !name.contains("_arm64-v8a")) {
                            try {
                                Log.d(TAG, "🔄 Loading additional Qt library: " + file.getAbsolutePath());
                                System.load(file.getAbsolutePath());
                                Log.d(TAG, "✅ Successfully loaded additional: " + name);
                            } catch (UnsatisfiedLinkError e) {
                                Log.e(TAG, "❌ Failed to load additional: " + name + ": " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error while looking for additional libraries: " + e.getMessage());
        }

        if (!failedLibs.isEmpty()) {
            Log.e(TAG, "❌ Failed to load these libraries: " + failedLibs);
            // Allow continuing even with some failures
            return failedLibs.size() < qtLibs.length / 2;
        }

        return true;
    }
}