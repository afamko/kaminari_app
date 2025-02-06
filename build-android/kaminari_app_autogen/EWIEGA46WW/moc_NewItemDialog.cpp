/****************************************************************************
** Meta object code from reading C++ file 'NewItemDialog.h'
**
** Created by: The Qt Meta Object Compiler version 68 (Qt 6.6.3)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../../../NewItemDialog.h"
#include <QtCore/qmetatype.h>

#if __has_include(<QtCore/qtmochelpers.h>)
#include <QtCore/qtmochelpers.h>
#else
QT_BEGIN_MOC_NAMESPACE
#endif


#include <memory>

#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'NewItemDialog.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 68
#error "This file was generated using the moc from 6.6.3. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

#ifndef Q_CONSTINIT
#define Q_CONSTINIT
#endif

QT_WARNING_PUSH
QT_WARNING_DISABLE_DEPRECATED
QT_WARNING_DISABLE_GCC("-Wuseless-cast")
namespace {

#ifdef QT_MOC_HAS_STRINGDATA
struct qt_meta_stringdata_CLASSNewItemDialogENDCLASS_t {};
constexpr auto qt_meta_stringdata_CLASSNewItemDialogENDCLASS = QtMocHelpers::stringData(
    "NewItemDialog",
    "newFolderRequested",
    "",
    "folderName",
    "newSheetRequested",
    "onNewFolder",
    "onNewSheet"
);
#else  // !QT_MOC_HAS_STRING_DATA
struct qt_meta_stringdata_CLASSNewItemDialogENDCLASS_t {
    uint offsetsAndSizes[14];
    char stringdata0[14];
    char stringdata1[19];
    char stringdata2[1];
    char stringdata3[11];
    char stringdata4[18];
    char stringdata5[12];
    char stringdata6[11];
};
#define QT_MOC_LITERAL(ofs, len) \
    uint(sizeof(qt_meta_stringdata_CLASSNewItemDialogENDCLASS_t::offsetsAndSizes) + ofs), len 
Q_CONSTINIT static const qt_meta_stringdata_CLASSNewItemDialogENDCLASS_t qt_meta_stringdata_CLASSNewItemDialogENDCLASS = {
    {
        QT_MOC_LITERAL(0, 13),  // "NewItemDialog"
        QT_MOC_LITERAL(14, 18),  // "newFolderRequested"
        QT_MOC_LITERAL(33, 0),  // ""
        QT_MOC_LITERAL(34, 10),  // "folderName"
        QT_MOC_LITERAL(45, 17),  // "newSheetRequested"
        QT_MOC_LITERAL(63, 11),  // "onNewFolder"
        QT_MOC_LITERAL(75, 10)   // "onNewSheet"
    },
    "NewItemDialog",
    "newFolderRequested",
    "",
    "folderName",
    "newSheetRequested",
    "onNewFolder",
    "onNewSheet"
};
#undef QT_MOC_LITERAL
#endif // !QT_MOC_HAS_STRING_DATA
} // unnamed namespace

Q_CONSTINIT static const uint qt_meta_data_CLASSNewItemDialogENDCLASS[] = {

 // content:
      12,       // revision
       0,       // classname
       0,    0, // classinfo
       4,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       2,       // signalCount

 // signals: name, argc, parameters, tag, flags, initial metatype offsets
       1,    1,   38,    2, 0x06,    1 /* Public */,
       4,    0,   41,    2, 0x06,    3 /* Public */,

 // slots: name, argc, parameters, tag, flags, initial metatype offsets
       5,    0,   42,    2, 0x08,    4 /* Private */,
       6,    0,   43,    2, 0x08,    5 /* Private */,

 // signals: parameters
    QMetaType::Void, QMetaType::QString,    3,
    QMetaType::Void,

 // slots: parameters
    QMetaType::Void,
    QMetaType::Void,

       0        // eod
};

Q_CONSTINIT const QMetaObject NewItemDialog::staticMetaObject = { {
    QMetaObject::SuperData::link<QDialog::staticMetaObject>(),
    qt_meta_stringdata_CLASSNewItemDialogENDCLASS.offsetsAndSizes,
    qt_meta_data_CLASSNewItemDialogENDCLASS,
    qt_static_metacall,
    nullptr,
    qt_incomplete_metaTypeArray<qt_meta_stringdata_CLASSNewItemDialogENDCLASS_t,
        // Q_OBJECT / Q_GADGET
        QtPrivate::TypeAndForceComplete<NewItemDialog, std::true_type>,
        // method 'newFolderRequested'
        QtPrivate::TypeAndForceComplete<void, std::false_type>,
        QtPrivate::TypeAndForceComplete<const QString &, std::false_type>,
        // method 'newSheetRequested'
        QtPrivate::TypeAndForceComplete<void, std::false_type>,
        // method 'onNewFolder'
        QtPrivate::TypeAndForceComplete<void, std::false_type>,
        // method 'onNewSheet'
        QtPrivate::TypeAndForceComplete<void, std::false_type>
    >,
    nullptr
} };

void NewItemDialog::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        auto *_t = static_cast<NewItemDialog *>(_o);
        (void)_t;
        switch (_id) {
        case 0: _t->newFolderRequested((*reinterpret_cast< std::add_pointer_t<QString>>(_a[1]))); break;
        case 1: _t->newSheetRequested(); break;
        case 2: _t->onNewFolder(); break;
        case 3: _t->onNewSheet(); break;
        default: ;
        }
    } else if (_c == QMetaObject::IndexOfMethod) {
        int *result = reinterpret_cast<int *>(_a[0]);
        {
            using _t = void (NewItemDialog::*)(const QString & );
            if (_t _q_method = &NewItemDialog::newFolderRequested; *reinterpret_cast<_t *>(_a[1]) == _q_method) {
                *result = 0;
                return;
            }
        }
        {
            using _t = void (NewItemDialog::*)();
            if (_t _q_method = &NewItemDialog::newSheetRequested; *reinterpret_cast<_t *>(_a[1]) == _q_method) {
                *result = 1;
                return;
            }
        }
    }
}

const QMetaObject *NewItemDialog::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *NewItemDialog::qt_metacast(const char *_clname)
{
    if (!_clname) return nullptr;
    if (!strcmp(_clname, qt_meta_stringdata_CLASSNewItemDialogENDCLASS.stringdata0))
        return static_cast<void*>(this);
    return QDialog::qt_metacast(_clname);
}

int NewItemDialog::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QDialog::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 4)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 4;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 4)
            *reinterpret_cast<QMetaType *>(_a[0]) = QMetaType();
        _id -= 4;
    }
    return _id;
}

// SIGNAL 0
void NewItemDialog::newFolderRequested(const QString & _t1)
{
    void *_a[] = { nullptr, const_cast<void*>(reinterpret_cast<const void*>(std::addressof(_t1))) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
}

// SIGNAL 1
void NewItemDialog::newSheetRequested()
{
    QMetaObject::activate(this, &staticMetaObject, 1, nullptr);
}
QT_WARNING_POP
