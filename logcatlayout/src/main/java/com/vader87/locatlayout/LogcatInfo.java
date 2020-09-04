package com.vader87.locatlayout;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class LogcatInfo {
    private final int IGNORE_DEPTH = 0;

    private int _logType = 0;
    private boolean _extend = false;
    private String _miniLog = "";
    private String _shortLog = "";
    private ArrayList<String> _fullLogs = null;

    public LogcatInfo(Context context, int logType, String tag, String msg) {
        _logType = logType;

        Date date = Calendar.getInstance().getTime();
        _miniLog = tag + ": " + msg;
        _shortLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(date) + " "
                + android.os.Process.myPid() + "/" + context.getApplicationContext().getPackageName() + " "
                + getLogTypeToString(context, _logType) + "/" + tag + ": " + msg;

        _fullLogs = new ArrayList<String>();
        _fullLogs.add(_shortLog);

        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        // Ignore depth not working
        for (int i = IGNORE_DEPTH; i < stacks.length; i++) {
            _fullLogs.add(stacks[i].toString());
        }
    }

    private String getLogTypeToString(Context context, int logType) {
        switch (logType) {
            case Log.DEBUG:
                return context.getString(R.string.single_log_type_string_debug);
            case Log.WARN:
                return context.getString(R.string.single_log_type_string_warn);
            case Log.ERROR:
                return context.getString(R.string.single_log_type_string_error);
            default:
                break;
        }
        return "";
    }

    public String getMiniLog() {
        return _miniLog;
    }

    public String getShortLog() {
        return _shortLog;
    }

    public ArrayList<String> getFullLogs() {
        return _fullLogs;
    }

    public void setExtend(boolean extend) {
        _extend = extend;
    }

    public boolean isExtend() {
        return _extend;
    }

    public int getLogType() { return _logType; }
}
