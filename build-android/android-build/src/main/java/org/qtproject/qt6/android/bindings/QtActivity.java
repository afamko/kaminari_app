package org.qtproject.qt6.android.bindings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class QtActivity extends Activity
{
    private static final String NATIVE_LIBRARIES_KEY = "native.libraries";
    private static final String MAIN_LIBRARY_KEY = "main.library";
    private static final String ENVIRONMENT_VARIABLES_KEY = "environment.variables";
    private static final String APPLICATION_PARAMETERS_KEY = "application.parameters";
    private static final String BUNDLED_LIBRARIES_KEY = "bundled.libraries";
    private static final String BUNDLED_IN_LIB_RESOURCE_ID_KEY = "android.app.bundled_in_lib_resource_id";
    private static final String BUNDLED_IN_ASSETS_RESOURCE_ID_KEY = "android.app.bundled_in_assets_resource_id";
    private static final String MAIN_LIBRARY_PATH_KEY = "android.app.lib_name";
    private static final String STATIC_INIT_CLASSES_KEY = "static.init.classes";
    private static final String NECESSITAS_API_LEVEL_KEY = "necessitas.api.level";
    private static final String EXTRACT_STYLE_KEY = "extract.android.style";
    private static final String PUBLIC_LIBRARIES_KEY = "public.libraries";
    private static final String TAG = "KAMINARI_APP";

    /// Mandatory parameters
    private String m_mainLib;
    private String[] m_sources;
    private ArrayList<String> m_qtLibs;
    private ArrayList<String> m_bundledLibs = new ArrayList<String>();
    private ArrayList<String> m_libraryPaths = new ArrayList<String>();

    /// Optional parameters
    // Loader class name for static init
    private static final String m_loaderClassName = "org.qtproject.qt6.android.QtNative";
    // Activity specific parameters
    private String[] m_environment = null;
    private String[] m_applicationParameters = null;
    private ActivityInfo m_activityInfo = null;
    private String m_extractStyleFromTheme = null;

    // Reserved
    private String m_mainLibPath = null;
    
    private boolean m_started = false;
    private boolean m_qtActivityInit = false;
    private native boolean qtActivityInit(String paramString, Object paramObject);
    private native void qtActivityDestroy();
    private native boolean qtActivityResult(int requestCode, int resultCode, Intent data);
    private native void qtActivityOnPause();
    private native void qtActivityOnResume();
    private native void qtActivityOnNewIntent(Intent data);
    private native void qtActivityOnStop();
    private native void qtActivityOnStart();
    private native void qtActivityOnWindowFocusChanged(boolean focused);
    private native void qtActivityOnConfigurationChanged(Configuration newConfig);

    // Static initialization block to load core libraries
    static {
        try {
            System.loadLibrary("c++_shared");
            Log.d(TAG, "✅ Static loader: Successfully loaded c++_shared");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ Static loader: Failed to load c++_shared: " + e.getMessage());
        }

        try {
            System.loadLibrary("kaminari_app");
            Log.d(TAG, "✅ Static loader: Successfully loaded kaminari_app");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ Static loader: Failed to load kaminari_app: " + e.getMessage());
        }
    }

    public String APPLICATION_PARAMETERS_KEY(){
        return APPLICATION_PARAMETERS_KEY;
    }

    public String ENVIRONMENT_VARIABLES_KEY(){
        return ENVIRONMENT_VARIABLES_KEY;
    }

    public String BUNDLED_LIBRARIES_KEY(){
        return BUNDLED_LIBRARIES_KEY;
    }

    public String BUNDLED_IN_LIB_RESOURCE_ID_KEY(){
        return BUNDLED_IN_LIB_RESOURCE_ID_KEY;
    }

    public String NATIVE_LIBRARIES_KEY(){
        return NATIVE_LIBRARIES_KEY;
    }

    public String loaderClassName(){
        return m_loaderClassName;
    }

    private boolean loadQtLibraries() {
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
                } else {
                    Log.e(TAG, "❌ File does not exist: " + path);
                }
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "❌ Failed to load " + lib + ": " + e.getMessage());
                // Continue with other libraries even if this one fails
            }
        }
        return true;
    }

    private void loadApplication(final Bundle loaderParams)
    {
        // Pre-load Qt libraries to ensure native functions are available
        loadQtLibraries();
        
        // Set the proper Qt lib path
        if (loaderParams.containsKey(MAIN_LIBRARY_PATH_KEY)) {
            m_mainLibPath = loaderParams.getString(MAIN_LIBRARY_PATH_KEY);
            if (m_mainLibPath.startsWith("/"))
                m_mainLibPath = m_mainLibPath.substring(1);
        }

        try {
            final int resourceId = loaderParams.getInt(BUNDLED_IN_LIB_RESOURCE_ID_KEY);
            if (resourceId != 0) {
                m_sources = getResources().getStringArray(resourceId);
                if (m_sources.length > 0) {
                    String[] libraries = new String[m_sources.length];
                    for (int i = 0; i < m_sources.length; i++) {
                        String source = m_sources[i];
                        int hash = source.lastIndexOf('=');
                        if (hash >= 0) {
                            libraries[i] = source.substring(0, hash);
                            m_sources[i] = source.substring(hash + 1);
                        } else {
                            libraries[i] = source;
                        }
                    }
                    loaderParams.putStringArray(NATIVE_LIBRARIES_KEY, libraries);
                }
            } else {
                final int resourceId2 = loaderParams.getInt(BUNDLED_IN_ASSETS_RESOURCE_ID_KEY);
                if (resourceId2 != 0)
                    m_sources = getResources().getStringArray(resourceId2);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (loaderParams.containsKey(STATIC_INIT_CLASSES_KEY)) {
            for (String className : loaderParams.getStringArray(STATIC_INIT_CLASSES_KEY)) {
                if (className.length() == 0)
                    continue;

                try {
                    final Class<?> initClass = Class.forName(className);
                    final Method initMethod = initClass.getMethod("init", Context.class, String.class);
                    initMethod.invoke(null, getApplicationContext(), loaderClassName());
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Extract bundled plugins and libs to the app's libs directory
        if (m_sources != null) {
            boolean res = true;

            String libsDir = getApplicationInfo().nativeLibraryDir;

            for (int i = 0; i < m_sources.length; i++) {
                // For each lib:
                //   1. Extract the lib from assets directory
                //   2. Chmod it to ensure we have execution permission

                String srcFile = m_sources[i];
                if (srcFile.endsWith(".jar")) {
                    if (extractFile(srcFile))
                        m_libraryPaths.add(srcFile);
                    continue;
                }

                String dstFile = libsDir + "/" + srcFile.substring(srcFile.lastIndexOf('/') + 1);
                res &= extractFile(srcFile, dstFile);
            }

            if (!res)
                throw new RuntimeException("Can't extract Qt libraries");
        }

        // Initialize the loader
        try {
            // Load the loader class
            final Class<?> loaderClass = Class.forName(m_loaderClassName);

            // Get the necessary method handles
            final Method loadApplication = loaderClass.getMethod("loadApplication", Activity.class, ClassLoader.class, Bundle.class);
            final Method startApplication = loaderClass.getMethod("startApplication", String[].class, String[].class);

            // Call the methods to load and start the Qt application
            if ((Boolean)loadApplication.invoke(null, this, getClass().getClassLoader(), loaderParams)) {
                // Add bundled libraries to the library paths
                for (String bundledImportPath : m_bundledLibs)
                    m_libraryPaths.add(bundledImportPath);

                // Convert library paths list to a string array
                String[] libraryPaths = new String[m_libraryPaths.size()];
                m_libraryPaths.toArray(libraryPaths);

                // Start the Qt application
                startApplication.invoke(null, m_applicationParameters, libraryPaths);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load QtActivity", e);
        }
    }

    private boolean extractFile(String sourceFile)
    {
        // Don't extract the file if it already exists
        File destinationFile = new File(sourceFile);
        if (destinationFile.exists())
            return true;

        // Create the destination directory if it doesn't exist
        if (!destinationFile.getParentFile().exists()) {
            if (!destinationFile.getParentFile().mkdirs())
                return false;
        }

        // Copy the asset file to the destination
        try {
            InputStream inputStream = getAssets().open(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            
            // Make the file executable
            if (destinationFile.canExecute() || destinationFile.setExecutable(true, false))
                return true;
            
            // If setting executable failed, try chmod directly
            Process process = Runtime.getRuntime().exec("/system/bin/chmod 755 " + destinationFile.getAbsolutePath());
            return process.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean extractFile(String sourceFile, String destinationFile)
    {
        // Don't extract the file if it already exists
        File file = new File(destinationFile);
        if (file.exists())
            return true;

        // Copy the asset file to the destination
        try {
            InputStream inputStream = getAssets().open(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            
            // Make the file executable
            if (file.canExecute() || file.setExecutable(true, false))
                return true;
            
            // If setting executable failed, try chmod directly
            Process process = Runtime.getRuntime().exec("/system/bin/chmod 755 " + file.getAbsolutePath());
            return process.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get activity info
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            if (ai != null)
                m_activityInfo = ai;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (m_activityInfo == null || m_activityInfo.metaData == null) {
            return;
        }

        // Extract parameters from AndroidManifest metadata
        Bundle metadata = m_activityInfo.metaData;
        String mainLib = metadata.getString(MAIN_LIBRARY_KEY);
        if (mainLib == null || mainLib.length() == 0) {
            Log.e("Qt", "No main library specified");
            return;
        }
        m_mainLib = mainLib;

        // Process potential bundled libraries
        if (metadata.containsKey(BUNDLED_LIBRARIES_KEY))
            addBundledLibs(metadata.getString(BUNDLED_LIBRARIES_KEY).split(":"));

        if (metadata.containsKey(EXTRACT_STYLE_KEY))
            m_extractStyleFromTheme = metadata.getString(EXTRACT_STYLE_KEY);

        // Create the loader params bundle
        Bundle loaderParams = new Bundle();
        loaderParams.putString(MAIN_LIBRARY_KEY, m_mainLib);
        
        // Add native libraries
        if (metadata.containsKey(NATIVE_LIBRARIES_KEY)) {
            String[] libs = metadata.getString(NATIVE_LIBRARIES_KEY).split(":");
            loaderParams.putStringArray(NATIVE_LIBRARIES_KEY, libs);
            
            // Store Qt libraries for later use
            m_qtLibs = new ArrayList<String>();
            for (String lib : libs) {
                if (lib.startsWith("lib"))
                    lib = lib.substring(3);
                if (lib.endsWith(".so"))
                    lib = lib.substring(0, lib.length() - 3);
                if (!m_qtLibs.contains(lib))
                    m_qtLibs.add(lib);
            }
        }

        // Add bundled libraries resource ID
        if (metadata.containsKey(BUNDLED_IN_LIB_RESOURCE_ID_KEY))
            loaderParams.putInt(BUNDLED_IN_LIB_RESOURCE_ID_KEY, metadata.getInt(BUNDLED_IN_LIB_RESOURCE_ID_KEY));
        
        if (metadata.containsKey(BUNDLED_IN_ASSETS_RESOURCE_ID_KEY))
            loaderParams.putInt(BUNDLED_IN_ASSETS_RESOURCE_ID_KEY, metadata.getInt(BUNDLED_IN_ASSETS_RESOURCE_ID_KEY));

        // Add environment variables
        if (metadata.containsKey(ENVIRONMENT_VARIABLES_KEY)) {
            m_environment = metadata.getString(ENVIRONMENT_VARIABLES_KEY).split(":");
            loaderParams.putStringArray(ENVIRONMENT_VARIABLES_KEY, m_environment);
        }

        // Add application parameters
        if (metadata.containsKey(APPLICATION_PARAMETERS_KEY)) {
            m_applicationParameters = metadata.getString(APPLICATION_PARAMETERS_KEY).split(":");
            loaderParams.putStringArray(APPLICATION_PARAMETERS_KEY, m_applicationParameters);
        }

        // Add static initialization classes
        if (metadata.containsKey(STATIC_INIT_CLASSES_KEY)) {
            String[] classes = metadata.getString(STATIC_INIT_CLASSES_KEY).split(":");
            loaderParams.putStringArray(STATIC_INIT_CLASSES_KEY, classes);
        }

        // Add the main library path if specified
        if (metadata.containsKey(MAIN_LIBRARY_PATH_KEY))
            loaderParams.putString(MAIN_LIBRARY_PATH_KEY, metadata.getString(MAIN_LIBRARY_PATH_KEY));

        // Load the application
        loadApplication(loaderParams);
    }

    private void addBundledLibs(String[] bundledLibraries)
    {
        for (String bundledLibrary : bundledLibraries) {
            if (bundledLibrary.length() > 0 && !m_bundledLibs.contains(bundledLibrary))
                m_bundledLibs.add(bundledLibrary);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (!m_qtActivityInit)
            return false;

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (!m_qtActivityInit)
            return false;

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyUp(keyCode, event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return m_qtActivityInit;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        return m_qtActivityInit;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!m_qtActivityInit || !qtActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        if (!m_qtActivityInit)
            return;

        qtActivityOnNewIntent(intent);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (m_qtActivityInit)
            qtActivityOnPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (m_qtActivityInit)
            qtActivityOnResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (m_qtActivityInit)
            qtActivityOnStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (m_qtActivityInit)
            qtActivityDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (m_qtActivityInit)
            qtActivityOnConfigurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (m_qtActivityInit)
            qtActivityOnWindowFocusChanged(hasFocus);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (m_qtActivityInit)
            qtActivityOnStart();
        m_started = true;
    }
}