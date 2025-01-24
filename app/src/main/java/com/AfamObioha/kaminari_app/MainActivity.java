package com.AfamObioha.kaminari_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import org.qtproject.qt.android.QtActivityDelegate;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private QtActivityDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Log application context
        Log.d(TAG, "Application Context: " + getApplicationContext());

        // Verify native library loading before initializing the delegate
        try {
            System.loadLibrary("Qt6Core");
            System.loadLibrary("Qt6Gui");
            System.loadLibrary("Qt6Widgets");
            System.loadLibrary("Qt6AndroidExtras");
            Log.d(TAG, "Native libraries loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load native libraries: ", e);
            return; // Abort further initialization as libraries are critical
        }

        // Attempt to initialize the delegate
        try {
            delegate = new QtActivityDelegate();
            Log.d(TAG, "Delegate initialized successfully: " + delegate);
            delegate.onCreate(savedInstanceState);
        } catch (Exception e) {
            Log.e(TAG, "Delegate initialization failed: ", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if (delegate != null) {
            try {
                delegate.onResume();
                Log.d(TAG, "Delegate onResume executed successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Delegate onResume failed: ", e);
            }
        } else {
            Log.w(TAG, "Delegate is null in onResume.");
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