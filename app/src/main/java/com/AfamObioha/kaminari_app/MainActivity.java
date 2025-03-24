package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String TAG = "KAMINARI_APP";

    // Direct JNI calls to C++
    private static native int startKaminariApp(String[] args);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force log at the very beginning to see if logs are captured at all
        Log.e(TAG, "🚨🚨🚨 onCreate START - FORCED LOG 🚨🚨🚨");
        fileLog("onCreate START - App initializing");

        try {
            super.onCreate(savedInstanceState);

            TextView textView = new TextView(this);
            textView.setPadding(30, 30, 30, 30);
            textView.setText("Kaminari App - Loading...\n\nPlease wait while the C++ application initializes.");
            setContentView(textView);

            Log.d(TAG, "📂 Native Library Path: " + getApplicationInfo().nativeLibraryDir);
            fileLog("Native Library Path: " + getApplicationInfo().nativeLibraryDir);
            listFilesInDir(getApplicationInfo().nativeLibraryDir);

            boolean librariesLoaded = loadQtLibraries();
            if (librariesLoaded) {
                String message = "✅ Qt libraries loaded successfully!";
                Log.d(TAG, message);
                fileLog(message);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                textView.append("\n\n" + message);

                // Load your main application library last
                try {
                    Log.d(TAG, "🔄 Loading kaminari_app.so library");
                    fileLog("Loading kaminari_app.so library");
                    System.loadLibrary("kaminari_app");
                    Log.d(TAG, "✅ Loaded native lib: kaminari_app");
                    fileLog("Successfully loaded native lib: kaminari_app");
                } catch (UnsatisfiedLinkError e) {
                    Log.e(TAG, "⚠️ App lib not found: " + e.getMessage(), e);
                    fileLog("App lib not found: " + e.getMessage());
                    textView.append("\n❌ App lib not found: " + e.getMessage());
                    return;
                }

                // IMPORTANT: Skip the QtActivityDelegate approach entirely
                // and directly call the C++ code through JNI
                textView.append("\n\n🔥 Starting C++ application directly...");
                directStartCppApp(textView);
            } else {
                String message = "❌ Failed to load Qt libraries";
                Log.e(TAG, message);
                fileLog(message);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                textView.append("\n\n" + message);
            }
        } catch (Exception e) {
            Log.e(TAG, "💥 FATAL ERROR in onCreate: " + e.getMessage(), e);
            fileLog("FATAL ERROR in onCreate: " + e.getMessage());
            // Continue to throw so we can see if crashes are being logged
            throw new RuntimeException("💥 FATAL ERROR in MainActivity.onCreate", e);
        }
    }

    private void directStartCppApp(TextView textView) {
        try {
            Log.d(TAG, "🔥 Starting C++ application directly through JNI");
            fileLog("Starting C++ application directly through JNI");

            // Create an array with a single argument - our app name
            String[] args = new String[] { "kaminari_app" };

            // Directly call the C++ main function
            Log.d(TAG, "🔄 Calling startKaminariApp with " + args.length + " arguments");
            fileLog("Calling C++ main function directly");

            int result = startKaminariApp(args);

            String message = "✅ C++ application started with result: " + result;
            Log.d(TAG, message);
            fileLog(message);
            textView.append("\n" + message);
        } catch (UnsatisfiedLinkError e) {
            String message = "❌ Failed to call C++ application: " + e.getMessage();
            Log.e(TAG, message, e);
            fileLog(message);
            textView.append("\n" + message);
        } catch (Exception e) {
            String message = "❌ Exception during C++ application start: " + e.getMessage();
            Log.e(TAG, message, e);
            fileLog(message);
            textView.append("\n" + message);
        }
    }

    private void listFilesInDir(String dirPath) {
        File dir = new File(dirPath);
        Log.d(TAG, "📂 Listing files in: " + dirPath);
        fileLog("Listing files in: " + dirPath);

        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    Log.d(TAG, "📄 Found: " + file.getName());
                    fileLog("Found file: " + file.getName());
                }
            } else {
                Log.e(TAG, "❌ No files found or unable to list directory");
                fileLog("No files found or unable to list directory");
            }
        } else {
            Log.e(TAG, "❌ Directory does not exist: " + dirPath);
            fileLog("Directory does not exist: " + dirPath);
        }
    }

    private boolean loadQtLibraries() {
        List<String> failedLibs = new ArrayList<>();
        List<String> successLibs = new ArrayList<>();

        try {
            System.loadLibrary("c++_shared");
            Log.d(TAG, "✅ Loaded: c++_shared");
            fileLog("Loaded: c++_shared");
            successLibs.add("c++_shared");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ Failed to load c++_shared: " + e.getMessage(), e);
            fileLog("Failed to load c++_shared: " + e.getMessage());
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
                    fileLog("Loading: " + path);
                    System.load(path);
                    Log.d(TAG, "✅ Loaded: " + lib);
                    fileLog("Successfully loaded: " + lib);
                    successLibs.add(lib);
                } else {
                    Log.e(TAG, "❌ File does not exist: " + path);
                    fileLog("File does not exist: " + path);
                    failedLibs.add(lib);
                }
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "❌ Failed to load " + lib + ": " + e.getMessage(), e);
                fileLog("Failed to load " + lib + ": " + e.getMessage());
                failedLibs.add(lib);
            }
        }

        Log.d(TAG, "📊 Loaded " + successLibs.size() + " of " + (qtLibs.length + 1) + " libraries");
        fileLog("Loaded " + successLibs.size() + " of " + (qtLibs.length + 1) + " libraries");

        if (!failedLibs.isEmpty()) {
            Log.e(TAG, "❌ Failed to load libraries: " + failedLibs);
            fileLog("Failed to load libraries: " + failedLibs);
            // Consider it a success if we loaded at least half the libraries
            return failedLibs.size() < qtLibs.length / 2;
        }

        return true;
    }

    // Write to a log file in case Android's logging system is not capturing our
    // logs
    private void fileLog(String message) {
        try {
            File logDir = getExternalFilesDir(null);
            if (logDir == null) {
                logDir = new File(Environment.getExternalStorageDirectory(), "kaminari_logs");
                logDir.mkdirs();
            }

            File logFile = new File(logDir, "kaminari_log.txt");
            FileOutputStream fos = new FileOutputStream(logFile, true);

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(new Date());
            String logLine = timestamp + " | " + message + "\n";

            fos.write(logLine.getBytes());
            fos.close();
        } catch (IOException e) {
            // Can't log the error since logging is the issue
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}