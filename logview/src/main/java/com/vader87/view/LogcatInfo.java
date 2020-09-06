package com.vader87.view;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LogcatInfo {
    private static final String TAG = "LogcatInfo";

    public static class Counter {
        public static void clear() {
            Debug = 0;
            Warn = 0;
            Error = 0;
        }

        public static int getCount(int logType) {
            switch (logType) {
                case Log.DEBUG:
                    return Debug;
                case Log.ERROR:
                    return Error;
                case Log.WARN:
                    return Warn;
                default:
                    break;
            }
            return 0;
        }

        public static int Debug = 0;
        public static int Warn = 0;
        public static int Error = 0;
    }

    private int _logType = 0;
    private String _summary = "";
    private String _log = "";

    public LogcatInfo(String msg) {
        _log = msg;

        String regexDate = "\\d{2}-\\d{2}";
        String regexTime = "\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}";
        String regexLogType = "[A-Z] ";
        String regexPackage = "[A-Z] .*:";
        String regexBegin = "[A-Z]";

        Pattern patternPackage = Pattern.compile(regexBegin);
        Matcher matcherPackage = patternPackage.matcher(_log);
        if (matcherPackage.find() == true) {
            switch (matcherPackage.group()) {
                case "E":
                    _logType = Log.ERROR;
                    Counter.Error++;
                    break;
                case "W":
                    _logType = Log.WARN;
                    Counter.Warn++;
                    break;
                case "D":
                default:
                    _logType = Log.DEBUG;
                    Counter.Debug++;
                    break;
            }
            int index = _log.indexOf(":", matcherPackage.end());
            _summary = _log.substring(index + 2);
        }
    }

    public String getLog() {
        return _log;
    }

    public String getSummary() {
        return _summary;
    }

    public int getLogType() { return _logType; }
}