package com.vader87.view.service;

import com.vader87.view.service.ILogCallback;

// Why can't a .aidl be placed into a library project?
// https://stackoverflow.com/questions/8845055/why-cant-a-aidl-be-placed-into-a-library-project

// AIDL 개요
// https://source.android.google.cn/devices/architecture/aidl/overview?hl=ko
interface ILogBinder {
    void reset();
    void run();
    void restart();
    void stop();
    void registerLogCallback(ILogCallback callback);
    void unregisterCallback(ILogCallback callback);
}
