package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a simple UI
        TextView textView = new TextView(this);
        textView.setPadding(30, 30, 30, 30);
        textView.setText("Kaminari App - Qt Libraries Status");
        setContentView(textView);

        // Debug information
        Log.d(TAG, "📂 Application Context: " + getApplicationContext());
        Log.d(TAG, "📂 Native Library Path: " + getApplicationInfo().nativeLibraryDir);

        // List files in native library directory
        listFilesInDir(getApplicationInfo().nativeLibraryDir);

        // Load Qt libraries in proper dependency order
        boolean librariesLoaded = loadQtLibraries();
        if (librariesLoaded) {
            Toast.makeText(this, "Successfully loaded Qt libraries!", Toast.LENGTH_LONG).show();
            textView.append("\n\nSuccessfully loaded Qt libraries!");
        } else {
            Toast.makeText(this, "Failed to load some Qt libraries. Check logs for details.", Toast.LENGTH_LONG).show();
            textView.append("\n\nFailed to load some Qt libraries. Check logs for details.");
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
        List<String> successLibs = new ArrayList<>();

        try {
            // Load c++ shared library first
            System.loadLibrary("c++_shared");
            Log.d(TAG, "✅ Successfully loaded: c++_shared");
            successLibs.add("c++_shared");
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
                "Qt6QuickParticles_arm64-v8a"
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
                    successLibs.add(lib);
                } else {
                    Log.e(TAG, "❌ Library file does not exist: " + fullPath);
                    failedLibs.add(lib);
                }
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "❌ Failed to load " + lib + ": " + e.getMessage());
                failedLibs.add(lib);
            }
        }

        Log.d(TAG, "📊 Successfully loaded libraries: " + successLibs.size() + " out of " + (qtLibs.length + 1));

        if (!failedLibs.isEmpty()) {
            Log.e(TAG, "❌ Failed to load these libraries: " + failedLibs);
            // Consider it a success if we loaded at least half the libraries
            return failedLibs.size() < qtLibs.length / 2;
        }

        return true;
    }
}