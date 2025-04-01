#include "mainwindow.h"
#include <QApplication>
#include <QDebug>
#include <QtGlobal>
#include <android/log.h>
#include <QtCore>

// Don't include the QtAndroid private header, it's not available
// #include <QtCore/private/qandroidextras_p.h>

int main(int argc, char *argv[])
{
#ifdef Q_OS_ANDROID
    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_CPP", "🔵 main() function started!");
#endif

    qInfo() << "Qt version:" << QT_VERSION_STR;
    QApplication app(argc, argv);

#ifdef Q_OS_ANDROID
    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_CPP", "🔵 QApplication created");
#endif

    MainWindow w;

#ifdef Q_OS_ANDROID
    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_CPP", "🔵 MainWindow created");
#endif

    w.show();

#ifdef Q_OS_ANDROID
    __android_log_print(ANDROID_LOG_INFO, "KAMINARI_CPP", "🔵 MainWindow shown");
#endif

    // Apply stylesheet to the main window for admin bar and other widgets
    QString styleSheet = "QMainWindow { "
                         "background-color: rgb(239, 239, 239); " // Main window background
                         "} "
                         "QLabel { "
                         "color: rgb(0, 0, 0); " // Text color for labels
                         "}";
    w.setStyleSheet(styleSheet);

    return app.exec();
}