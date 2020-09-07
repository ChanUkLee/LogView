package com.vader87.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

class ConsoleHeaderView extends RelativeLayout {

    private static final String TAG = "ConsoleHeaderView";

    private TextView _textViewError = null;
    private TextView _textViewWarn = null;
    private TextView _textViewDebug = null;

    public ConsoleHeaderView(Context context) {
        super(context);
        initView();
    }

    public ConsoleHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ConsoleHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        initView();
    }

    public ConsoleHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.view_console_header, this);

        _textViewDebug = (TextView)view.findViewById(R.id.textview_console_header_debug);
        _textViewWarn = (TextView)view.findViewById(R.id.textview_console_header_warn);
        _textViewError = (TextView)view.findViewById(R.id.textview_console_header_error);
    }

    public void initCounts() {
        setDebugCount(0);
        setWarnCount(0);
        setErrorCount(0);
    }

    public void setCount(int logType, int count) {
        switch (logType) {
            case Log.DEBUG:
                setDebugCount(count);
                break;
            case Log.WARN:
                setWarnCount(count);
                break;
            case Log.ERROR:
                setErrorCount(count);
                break;
            default:
                break;
        }
    }

    private void setDebugCount(int count) {
        _textViewDebug.setText(String.valueOf(count));
    }

    private void setWarnCount(int count) {
        _textViewWarn.setText(String.valueOf(count));
    }

    private void setErrorCount(int count) {
        _textViewError.setText(String.valueOf(count));
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        _textViewDebug.setOnClickListener(onClickListener);
        _textViewWarn.setOnClickListener(onClickListener);
        _textViewError.setOnClickListener(onClickListener);
    }

    public void setIgnoreDebug(boolean ignore) {
        setTextViewColor(_textViewDebug, ignore);
    }

    public void setIgnoreWarn(boolean ignore) {
        setTextViewColor(_textViewWarn, ignore);
    }

    public void setIgnoreError(boolean ignore) {
        setTextViewColor(_textViewError, ignore);
    }

    private void setTextViewColor(TextView textView, boolean ignore) {
        textView.setTextColor(ignore ? getContext().getColor(R.color.colorConsoleHeaderDisalbeText) : getContext().getColor(R.color.colorConsoleHeaderEnableText));
    }
}
