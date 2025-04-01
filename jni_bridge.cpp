#include <jni.h>
#include <android/log.h>
#include <stdlib.h> // For free()
#include <string.h> // For strdup

#define LOG_TAG "KAMINARI_JNI"

// Declare main function
extern "C" int qt_main(int argc, char *argv[]);

// JNI_OnLoad
extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void * /* reserved */)
{
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "JNI_OnLoad called");

    JNIEnv *env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK)
    {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to get JNI environment");
        return JNI_ERR;
    }

    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "JNI_OnLoad successful");
    return JNI_VERSION_1_6;
}

// First method Qt calls
extern "C" JNIEXPORT jboolean JNICALL
Java_org_qtproject_qt6_android_QtNative_loadApplication(
    JNIEnv *env, jclass /* cls */, jobject activity,
    jobject classLoader, jobject bundle)
{
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "QtNative.loadApplication called");
    return JNI_TRUE;
}

// Second method Qt calls after loadApplication
extern "C" JNIEXPORT jboolean JNICALL
Java_org_qtproject_qt6_android_QtNative_startApplication(
    JNIEnv *env, jclass /* cls */, jobjectArray jargv,
    jobjectArray jenv)
{
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "QtNative.startApplication called");

    // Convert Java string arrays to C strings
    int argc = env->GetArrayLength(jargv);
    char **argv = new char *[argc + 1];

    for (int i = 0; i < argc; i++)
    {
        jstring jstr = (jstring)env->GetObjectArrayElement(jargv, i);
        const char *str = env->GetStringUTFChars(jstr, nullptr);
        argv[i] = strdup(str); // Make a copy
        env->ReleaseStringUTFChars(jstr, str);
    }
    argv[argc] = nullptr; // Null-terminate

    // Call our main Qt function
    int result = qt_main(argc, argv);

    // Clean up
    for (int i = 0; i < argc; i++)
    {
        free(argv[i]);
    }
    delete[] argv;

    return JNI_TRUE;
}

int qt_main(int argc, char *argv[])
{
    return 0;
}
