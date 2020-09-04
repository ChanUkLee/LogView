package com.vader87.locatlayout;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

// Creating a ListView with custom list items programmatically in Android - no xml list item layout
// https://stackoverflow.com/questions/12784695/creating-a-listview-with-custom-list-items-programmatically-in-android-no-xml
class LogcatAdpater extends ArrayAdapter<LogcatInfo> {

    private int _infoCnt = 0;
    private int _warnCnt = 0;
    private int _errorCnt = 0;

    public LogcatAdpater(Context context, ArrayList<LogcatInfo> resource) {
        super(context, -1, -1, resource);
    }

    @Override
    public void add(LogcatInfo object) {
        super.add(object);

        switch (object.getLogType()) {
            case Log.ERROR:
                _errorCnt++;
                break;
            case Log.WARN:
                _warnCnt++;
                break;
            case Log.DEBUG:
            default:
                _infoCnt++;
                break;
        }
    }

    public int getLogTypeCount(int logType) {
        switch (logType) {
            case Log.ERROR:
                return _errorCnt;
            case Log.WARN:
                return _warnCnt;
            case Log.DEBUG:
            default:
                break;
        }
        return _infoCnt;
    }

    @Override
    public void clear() {
        super.clear();

        _infoCnt = 0;
        _warnCnt = 0;
        _errorCnt = 0;
    }

    private int getLogTypeIcon(int logType) {
        switch (logType) {
            case Log.ERROR:
                return R.drawable.small_icon_log_error;
            case Log.WARN:
                return R.drawable.small_icon_log_warn;
            case Log.DEBUG:
            default:
                break;
        }
        return R.drawable.small_icon_log_info;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogcatInfo logcatInfo = super.getItem(position);

        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootLayout.setBackgroundColor((position % 2 == 0) ? Color.GRAY : Color.DKGRAY);

        if (logcatInfo.isExtend()) {
            rootLayout.setOrientation(LinearLayout.VERTICAL);

            // Java - Stack trace 출력하는 방법 (Throwable, Exception)
            // https://codechacha.com/ko/java-print-stack-trace/
            for (int i = 0; i < logcatInfo.getFullLogs().size(); i++) {
                LinearLayout singleLineLayout = new LinearLayout(getContext());
                singleLineLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                singleLineLayout.setOrientation(LinearLayout.HORIZONTAL);
                rootLayout.addView(singleLineLayout);

                //HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
                //horizontalScrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                //singleLineLayout.addView(horizontalScrollView);

                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText(logcatInfo.getFullLogs().get(i));
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setSingleLine(true);
                if (i == 0)
                    textView.setCompoundDrawablesWithIntrinsicBounds(getLogTypeIcon(logcatInfo.getLogType()), 0, 0, 0);
                //horizontalScrollView.addView(textView);
                singleLineLayout.addView(textView);
            }
        } else {
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(logcatInfo.getMiniLog());
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setSingleLine(true);
            textView.setCompoundDrawablesWithIntrinsicBounds(getLogTypeIcon(logcatInfo.getLogType()), 0, 0, 0);
            rootLayout.addView(textView);
        }

        return rootLayout;
    }
}
