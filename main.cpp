#include "mainwindow.h"
#include <QApplication>
#include <QDebug>
#include <QtGlobal>
#include <QLabel>
#include <QVBoxLayout>
#include <QPushButton>
#include <QWidget>
#include <android/log.h>

// This is the main function that will be called from the JNI bridge
// No JNI code in this file for cleaner separation
extern "C" int qt_main(int argc, char *argv[])
{
// Helper macro for Android logging
#define ANDROID_LOG(priority, tag, ...) \
    __android_log_print(ANDROID_LOG_##priority, tag, __VA_ARGS__)

    // Start logging all operations for diagnostic purposes
    ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 qt_main() function started!");

    try
    {
        // Create QApplication
        QApplication app(argc, argv);
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 QApplication created successfully");

        // SIMPLE UI TEST: Create a basic window with bright colors to confirm UI rendering works
        QWidget testWidget;
        testWidget.setWindowTitle("Kaminari Test Window");
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 Test widget created");

        QVBoxLayout *layout = new QVBoxLayout(&testWidget);
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 Layout created");

        QLabel *testLabel = new QLabel("⚠️ TEST WINDOW - KAMINARI APP ⚠️");
        testLabel->setStyleSheet(
            "font-size: 24px; "
            "color: red; "
            "background-color: yellow; "
            "padding: 20px; "
            "border: 5px solid black;");
        testLabel->setAlignment(Qt::AlignCenter);
        layout->addWidget(testLabel);
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 Test label added");

        QPushButton *testButton = new QPushButton("This is a test button");
        testButton->setStyleSheet(
            "font-size: 18px; "
            "background-color: #4CAF50; "
            "color: white; "
            "padding: 15px; "
            "min-height: 60px; "
            "border-radius: 10px;");
        QObject::connect(testButton, &QPushButton::clicked, []()
                         { ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 TEST BUTTON CLICKED!"); });
        layout->addWidget(testButton);
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 Test button added");

        testWidget.setMinimumSize(800, 600);
        testWidget.resize(800, 600);
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 Widget size set to 800x600");

        testWidget.show();
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 Widget shown");

        // This is a critical step - exec() starts the event loop and shows the UI
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 Starting event loop with app.exec()");
        int result = app.exec();
        ANDROID_LOG(INFO, "KAMINARI_CPP", "🔵 app.exec() returned %d", result);

        return result;
    }
    catch (const std::exception &e)
    {
        ANDROID_LOG(ERROR, "KAMINARI_CPP", "❌ Exception in Qt application: %s", e.what());
        return -1;
    }
    catch (...)
    {
        ANDROID_LOG(ERROR, "KAMINARI_CPP", "❌ Unknown exception in Qt application");
        return -2;
    }
}