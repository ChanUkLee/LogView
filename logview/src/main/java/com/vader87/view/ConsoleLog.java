package com.vader87.view;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConsoleLog {
    private static final String TAG = "ConsoleLog";

    private int _logType = 0;
    private String _summary = "";
    private String _log = "";

    public ConsoleLog(String msg) {
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
                    break;
                case "W":
                    _logType = Log.WARN;
                    break;
                case "D":
                default:
                    _logType = Log.DEBUG;
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