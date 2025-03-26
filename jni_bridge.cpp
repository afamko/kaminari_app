#include <jni.h>
#include <android/log.h>
#include <string>

// Declare the main Qt function that we'll call from JNI
extern "C" int qt_main(int argc, char *argv[]);

// The JNI bridge function with standard JNI export macros
extern "C" JNIEXPORT jint JNICALL
Java_com_AfamObioha_kaminari_1app_MainActivity_startKaminariApp(
    JNIEnv *env, jobject thiz, jobjectArray jargs)
{
    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_JNI", "🔴 JNI Bridge function called from Java");

    // Convert Java string array to C++ argc/argv
    int argc = env->GetArrayLength(jargs);
    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_JNI", "🔴 Processing %d arguments", argc);

    char **argv = new char *[argc + 1]; // +1 for null terminator

    for (int i = 0; i < argc; i++)
    {
        jstring jstr = (jstring)env->GetObjectArrayElement(jargs, i);
        const char *cstr = env->GetStringUTFChars(jstr, nullptr);
        argv[i] = strdup(cstr); // Need to use strdup to copy string
        __android_log_print(ANDROID_LOG_INFO, "KAMINARI_JNI", "🔴 Arg %d: %s", i, cstr);
        env->ReleaseStringUTFChars(jstr, cstr);
    }

    // Null-terminate the argv array
    argv[argc] = nullptr;

    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_JNI", "🔴 Calling qt_main()");

    // Call our main Qt function
    int result = qt_main(argc, argv);

    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_JNI", "🔴 qt_main() returned %d", result);

    // Clean up
    for (int i = 0; i < argc; i++)
    {
        free(argv[i]);
    }
    delete[] argv;

    return result;
}

// Alternative function signature that Java might be looking for
extern "C" JNIEXPORT jint JNICALL
Java_com_AfamObioha_kaminari_1app_MainActivity_startKaminariApp___3Ljava_lang_String_2(
    JNIEnv *env, jobject thiz, jobjectArray jargs)
{
    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_JNI", "🔴 Alternative JNI signature called");

    // Just call our main implementation
    return Java_com_AfamObioha_kaminari_1app_MainActivity_startKaminariApp(env, thiz, jargs);
}