package com.vader87.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Read logcat programmatically within application
// https://stackoverflow.com/questions/12692103/read-logcat-programmatically-within-application

// androidnerds/logger
// https://github.com/androidnerds/logger/blob/master/src/com/michaelrnovak/util/logger/service/LogProcessor.java
public class LogService extends Service {

    private static final String TAG = "LogService";

    private static Handler _handler = null;
    private boolean _runProcess = false;
    private boolean _killProcess = false;

    private RemoteCallbackList<ILogCallback> _callbacks = new RemoteCallbackList<ILogCallback>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable _runnableProcess = new Runnable() {
        @Override
        public void run() {
            runProcess();
            _runProcess = true;
        }
    };

    // Logcat 명령줄 도구
    // https://developer.android.com/studio/command-line/logcat
    private void runProcess() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("/system/bin/logcat -b all");
        } catch (IOException e) {
            message(e.getMessage());
        }

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while (!isReadyToKillProcess()) {
                onNewLog(bufferedReader.readLine());
            }

            bufferedReader.close();
            process.destroy();
            process = null;
            bufferedReader = null;
        } catch (IOException | RemoteException e) {
            message(e.getMessage());
        }
    }

    private void message(String msg) {
        Message.obtain(_handler, -1, msg).sendToTarget();
    }

    private synchronized void killProcess() {
        _killProcess = true;
    }

    private synchronized boolean isReadyToKillProcess() {
        return _killProcess;
    }

    private synchronized void onNewLog(String newLog) throws  RemoteException {
        int count = _callbacks.beginBroadcast();
        for (int i = 0; i < count; i++) {
            if (_callbacks.getBroadcastItem(i) != null)
                _callbacks.getBroadcastItem(i).onNewLog(newLog);
        }
        _callbacks.finishBroadcast();
    }

    public static void setHandler(Handler handler) {
        _handler = handler;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        killProcess();
        stopSelf();
        return false;
    }

    private final ILogBinder.Stub _binder = new ILogBinder.Stub() {

        @Override
        public void reset() throws RemoteException {
            killProcess();
            while (!_runProcess) {
                try {
                    Log.d(TAG, "waiting...");
                } catch (Exception e) {
                    Log.d(TAG, "obj has been interrupted!");
                }
                _killProcess = false;
                run();
            }
        }

        @Override
        public void run() throws RemoteException {
            Thread thread = new Thread(_runnableProcess);
            thread.start();
        }

        @Override
        public void restart() throws RemoteException {
            killProcess();
            while (!_runProcess) {
                try {
                    Log.d(TAG, "waiting...");
                } catch (Exception e) {
                    Log.d(TAG, "we have an exception");
                }
            }
            _killProcess = false;
            run();
        }

        @Override
        public void stop() throws RemoteException {
            killProcess();
            stopSelf();
        }

        @Override
        public void registerLogCallback(ILogCallback callback) throws RemoteException {
            _callbacks.register(callback);
        }

        @Override
        public void unregisterCallback(ILogCallback callback) throws RemoteException {
            _callbacks.unregister(callback);
        }
    };
}
