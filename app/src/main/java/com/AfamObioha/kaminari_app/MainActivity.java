package com.AfamObioha.kaminari_app;

import org.qtproject.qt.android.QtActivity;
import android.util.Log;

public class MainActivity extends QtActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called successfully");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called successfully");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause called successfully");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called successfully");
        super.onDestroy();
    }
}