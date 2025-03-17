#include "mainwindow.h"
#include <QApplication>
#include <QDebug>
#include <QtGlobal>
#include <jni.h>

int runMain(int argc, char *argv[])
{
    qInfo() << "Qt version:" << QT_VERSION_STR;
    QApplication app(argc, argv);

    MainWindow w;
    w.show();

    QString styleSheet = "QMainWindow { "
                         "background-color: rgb(239, 239, 239); "
                         "} QLabel { color: rgb(0, 0, 0); }";
    w.setStyleSheet(styleSheet);

    return app.exec();
}

extern "C" JNIEXPORT jint JNICALL
Java_org_qtproject_qt_android_QtActivityDelegate_startQtApp(JNIEnv *env, jclass clazz, jobject context)
{
    Q_UNUSED(env);
    Q_UNUSED(clazz);
    Q_UNUSED(context);

    int argc = 1;
    char *argv[] = {strdup("kaminari_app")};
    return runMain(argc, argv);
}