#include "mainwindow.h"
#include <QApplication>
#include <QDebug>
#include <QtGlobal>
#include <android/log.h>
#include <QLabel>
#include <QVBoxLayout>
#include <QPushButton>
#include <jni.h>

// Helper macro for Android logging
#define ANDROID_LOG(priority, tag, ...) \
    __android_log_print(ANDROID_LOG_##priority, tag, __VA_ARGS__)

// Forward declaration of main function for JNI
extern "C" JNIEXPORT jint JNICALL
Java_com_AfamObioha_kaminari_1app_MainActivity_startKaminariApp(
    JNIEnv *env, jobject /* thiz */, jobjectArray jargs);

int main(int argc, char *argv[])
{
    // Use both Qt logging and Android direct logging for maximum visibility
    qInfo() << "🔥 Qt C++ main() triggered!";
    ANDROID_LOG(INFO, "KAMINARI_CPP", "🔥 Qt C++ main() triggered!");

    qInfo() << "✅ Entered main";
    ANDROID_LOG(INFO, "KAMINARI_CPP", "✅ Entered main");

    qInfo() << "Qt version:" << QT_VERSION_STR;
    ANDROID_LOG(INFO, "KAMINARI_CPP", "Qt version: %s", QT_VERSION_STR);

    // Create QApplication
    QApplication app(argc, argv);
    qInfo() << "✅ QApplication created";
    ANDROID_LOG(INFO, "KAMINARI_CPP", "✅ QApplication created");

    // Create main window
    MainWindow w;
    qInfo() << "✅ MainWindow constructed";
    ANDROID_LOG(INFO, "KAMINARI_CPP", "✅ MainWindow constructed");

    // Add a very visible control to ensure we can see something
    QLabel *testLabel = new QLabel("📱 KAMINARI APP RUNNING IN C++! 📱");
    testLabel->setStyleSheet("font-size: 24px; color: red; background-color: yellow;");
    testLabel->setAlignment(Qt::AlignCenter);

    QPushButton *testButton = new QPushButton("Click Me - I'm from C++!");
    testButton->setStyleSheet("font-size: 18px; background-color: #4CAF50; color: white; padding: 10px;");
    QObject::connect(testButton, &QPushButton::clicked, []()
                     { ANDROID_LOG(INFO, "KAMINARI_CPP", "🎯 Button clicked!"); });

    QVBoxLayout *layout = new QVBoxLayout();
    layout->addWidget(testLabel);
    layout->addWidget(testButton);

    QWidget *centralWidget = new QWidget();
    centralWidget->setLayout(layout);
    w.setCentralWidget(centralWidget);

    // Set main window styles
    w.setStyleSheet("QMainWindow { background-color: rgb(239, 239, 239); } "
                    "QLabel { color: rgb(0, 0, 0); }");

    // Show the window
    w.show();
    w.resize(800, 600); // Make sure it's big enough to be visible
    qInfo() << "✅ MainWindow shown";
    ANDROID_LOG(INFO, "KAMINARI_CPP", "✅ MainWindow shown");

    qInfo() << "✅ Style applied";
    ANDROID_LOG(INFO, "KAMINARI_CPP", "✅ Style applied");

    // Enter event loop
    ANDROID_LOG(INFO, "KAMINARI_CPP", "⏳ Starting app.exec()");
    int execResult = app.exec();

    qInfo() << "✅ app.exec() returned:" << execResult;
    ANDROID_LOG(INFO, "KAMINARI_CPP", "✅ app.exec() returned: %d", execResult);

    return execResult;
}

// JNI Bridge to call our main function directly from Java
extern "C" JNIEXPORT jint JNICALL
Java_com_AfamObioha_kaminari_1app_MainActivity_startKaminariApp(
    JNIEnv *env, jobject /* thiz */, jobjectArray jargs)
{

    ANDROID_LOG(INFO, "KAMINARI_JNI", "🔥 JNI Bridge called directly!");

    // Convert Java string array to C++ argc/argv
    int argc = env->GetArrayLength(jargs);
    char **argv = new char *[argc + 1]; // +1 for null terminator

    for (int i = 0; i < argc; i++)
    {
        jstring jstr = (jstring)env->GetObjectArrayElement(jargs, i);
        const char *cstr = env->GetStringUTFChars(jstr, nullptr);
        argv[i] = strdup(cstr); // Need to use strdup to copy string
        env->ReleaseStringUTFChars(jstr, cstr);
    }

    // Null-terminate the argv array
    argv[argc] = nullptr;

    // Call main function directly
    ANDROID_LOG(INFO, "KAMINARI_JNI", "🚀 Calling C++ main function directly...");
    int result = main(argc, argv);
    ANDROID_LOG(INFO, "KAMINARI_JNI", "✅ C++ main returned: %d", result);

    // Clean up
    for (int i = 0; i < argc; i++)
    {
        free(argv[i]);
    }
    delete[] argv;

    return result;
}