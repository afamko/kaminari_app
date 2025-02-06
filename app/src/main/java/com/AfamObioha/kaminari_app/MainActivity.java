package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import org.qtproject.qt.android.QtActivityDelegate;
import org.qtproject.qt.android.QtNative;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private QtActivityDelegate delegate;
    private Handler handler = new Handler();
    private static final int RETRY_INTERVAL_MS = 1000; // Retry every 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Application Context: " + getApplicationContext());
        Log.d(TAG, "Native Library Path: " + getApplicationInfo().nativeLibraryDir);

        // Load native libraries
        try {
            System.loadLibrary("Qt6Core_arm64-v8a");
            System.loadLibrary("Qt6Gui_arm64-v8a");
            System.loadLibrary("Qt6Widgets_arm64-v8a");
            System.loadLibrary("Qt6Qml_arm64-v8a");
            System.loadLibrary("Qt6Quick_arm64-v8a");
            System.loadLibrary("Qt6Network_arm64-v8a");
            System.loadLibrary("Qt6QuickTemplates2_arm64-v8a");
            System.loadLibrary("Qt6QuickControls2Basic_arm64-v8a");
            System.loadLibrary("Qt6QmlModels_arm64-v8a");
            System.loadLibrary("Qt6QuickParticles_arm64-v8a");
            System.loadLibrary("c++_shared");

            Log.d(TAG, "Native libraries loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load native libraries: ", e);
            return;
        }

        handler.postDelayed(this::initializeQtDelegate, RETRY_INTERVAL_MS); // Start retry loop
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        if (delegate == null) {
            handler.postDelayed(this::initializeQtDelegate, RETRY_INTERVAL_MS);
        } else {
            try {
                delegate.onResume();
                Log.d(TAG, "Delegate onResume executed successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Delegate onResume failed: ", e);
            }
        }
    }

    private void initializeQtDelegate() {
        if (delegate != null)
            return;

        if (QtNative.activity() == null) {
            Log.e(TAG, "QtNative.activity() is still null. Retrying in " + RETRY_INTERVAL_MS + " ms...");
            handler.postDelayed(this::initializeQtDelegate, RETRY_INTERVAL_MS);
            return;
        }

        try {
            delegate = new QtActivityDelegate();
            Log.d(TAG, "Delegate initialized successfully.");
            delegate.onCreate(null);
            delegate.onResume();
        } catch (Exception e) {
            Log.e(TAG, "Delegate initialization failed: ", e);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause called");
        if (delegate != null) {
            try {
                delegate.onPause();
                Log.d(TAG, "Delegate onPause executed successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Delegate onPause failed: ", e);
            }
        } else {
            Log.w(TAG, "Delegate is null in onPause.");
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        if (delegate != null) {
            try {
                delegate.onDestroy();
                Log.d(TAG, "Delegate onDestroy executed successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Delegate onDestroy failed: ", e);
            }
        } else {
            Log.w(TAG, "Delegate is null in onDestroy.");
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult called");
        if (delegate != null) {
            try {
                delegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
                Log.d(TAG, "Delegate onRequestPermissionsResult executed successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Delegate onRequestPermissionsResult failed: ", e);
            }
        } else {
            Log.w(TAG, "Delegate is null in onRequestPermissionsResult.");
        }
    }
}