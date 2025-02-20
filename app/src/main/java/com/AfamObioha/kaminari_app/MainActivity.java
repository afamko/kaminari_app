package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import org.qtproject.qt.android.QtNative;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String LOG_FILE = "/storage/emulated/0/Android/data/com.AfamObioha.kaminari_app/files/crash_log.txt";

    private static final String[] LIBRARIES = {
            "libQt6Core_arm64-v8a.so",
            "libQt6Gui_arm64-v8a.so",
            "libQt6Widgets_arm64-v8a.so",
            "libQt6Qml_arm64-v8a.so",
            "libQt6Quick_arm64-v8a.so",
            "libQt6Network_arm64-v8a.so",
            "libQt6QuickTemplates2_arm64-v8a.so",
            "libQt6QuickControls2Basic_arm64-v8a.so",
            "libQt6QmlModels_arm64-v8a.so",
            "libQt6QuickParticles_arm64-v8a.so",
            "libc++_shared.so"
    };

    private static boolean qtInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logToFile("📂 Application Context: " + getApplicationContext());
        logToFile("📂 Native Library Path: " + getApplicationInfo().nativeLibraryDir);

        if (!qtInitialized) {
            logToFile("🚀 Initializing Qt...");
            extractAndLoadLibraries();
            qtInitialized = true;
        } else {
            logToFile("⚠️ Qt is already initialized. Skipping duplicate initialization.");
        }

        // 🔥 Corrected startApplication() call with try-catch
        try {
            logToFile("🚀 Starting Qt application...");
            QtNative.startApplication(getApplicationInfo().sourceDir, getApplicationInfo().dataDir);
            logToFile("✅ Qt application started.");
        } catch (Exception e) {
            logToFile("❌ Failed to start Qt application: " + Log.getStackTraceString(e));
        }
    }

    private void extractAndLoadLibraries() {
        File libDir = new File(getApplicationContext().getFilesDir(), "libs");
        if (!libDir.exists()) {
            libDir.mkdirs();
        }

        try (ZipFile apkZip = new ZipFile(getApplicationInfo().sourceDir)) {
            for (String libName : LIBRARIES) {
                File outFile = new File(libDir, libName);

                if (!outFile.exists()) {
                    String zipPath = "lib/arm64-v8a/" + libName;
                    ZipEntry entry = apkZip.getEntry(zipPath);
                    if (entry == null) {
                        logToFile("❌ " + zipPath + " not found in APK!");
                        continue;
                    }

                    try (InputStream inputStream = apkZip.getInputStream(entry);
                            FileOutputStream outputStream = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        logToFile("✅ Extracted " + libName);
                    }
                }

                // **Prevent QtQml.Models from registering twice**
                if (libName.contains("Qt6QmlModels")) {
                    logToFile("⚠️ Skipping reloading of Qt6QmlModels");
                    continue;
                }

                // Load extracted library
                System.load(outFile.getAbsolutePath());
                logToFile("✅ Loaded " + libName);
            }
        } catch (Exception e) {
            logToFile("❌ Failed to extract/load libraries: " + Log.getStackTraceString(e));
        }
    }

    private void logToFile(String message) {
        Log.d(TAG, message);
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(message);
        } catch (IOException e) {
            Log.e(TAG, "❌ Failed to write to log file", e);
        }
    }
}