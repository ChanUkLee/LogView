package com.vader87.locatlayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Android Module을 Bintray(JCenter)에 배포하는 방법
// https://thdev.tech/androiddev/2016/09/01/Android-Bintray(JCenter)-Publish/

// Custom ViewGroup Examples
// https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/widget/AbsoluteLayout.java
// https://github.com/yhpark/FloatingLayout/blob/master/FloatingLayout.java
//
// Want to be
// https://assetstore.unity.com/packages/tools/gui/lunar-mobile-console-free-82881?locale=ko-KR
public class LogcatLayout extends ViewGroup {

    private final String TAG = "LogcatLayout";

    private LinearLayout _rootLayout = null;
    private ScrollView _scrollView = null;
    private ListView _listView = null;
    private LogcatAdpater _logcatAdapter = null;

    private View _btnShowView = null;
    private View _headerView = null;

    private ArrayList<LogcatInfo> _logcatInfoList = null;

    public LogcatLayout(Context context) {
        super(context);
        initView();
    }

    public LogcatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LogcatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        initView();
    }

    public LogcatLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    protected void onAnimation(final boolean isShow) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;

        ValueAnimator animator = ValueAnimator.ofInt(_scrollView.getMeasuredHeight(), isShow ? getBottom() : 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer)animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = _scrollView.getLayoutParams();
                layoutParams.height = value;
                _scrollView.setLayoutParams(layoutParams);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isShow) {
                    _rootLayout.removeView(_btnShowView);
                    _listView.addHeaderView(_headerView);
                } else {

                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isShow) {

                } else {
                    _listView.removeHeaderView(_headerView);
                    _rootLayout.addView(_btnShowView, 0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(500);
        animator.start();
    }

    protected void initView() {
        // LinearLayout API
        // https://developer.android.com/reference/android/widget/LinearLayout
        _rootLayout = new LinearLayout(getContext());
        _rootLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _rootLayout.setOrientation(LinearLayout.VERTICAL);
        _rootLayout.setGravity(Gravity.CENTER);

        Button button = new Button(getContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText("Show");
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Show");


                onAnimation(true);

                //_scrollView.getLayoutParams().height = 1000;
                //_rootLayout.invalidate();
                //_rootLayout.requestLayout();
            }
        });

        _btnShowView = button;
        _rootLayout.addView(_btnShowView);

        // ScrollView API
        // https://developer.android.com/reference/android/widget/ScrollView
        _scrollView = new ScrollView(getContext());
        _scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        _scrollView.getLayoutParams().height = 0;
        _scrollView.invalidate();
        _scrollView.setFillViewport(true);
        _scrollView.setBackgroundColor(Color.BLACK);
        _scrollView.setAlpha(0.75f);
        _rootLayout.addView(_scrollView);

        _logcatInfoList = new ArrayList<LogcatInfo>();
        _logcatAdapter = new LogcatAdpater(getContext(), _logcatInfoList);

        _listView = new ListView(getContext());
        _listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _listView.setAdapter(_logcatAdapter);

        RelativeLayout headerLayout = new RelativeLayout(getContext());
        headerLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        RelativeLayout.LayoutParams btnHideParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnHideParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        btnHideParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        Button btnHide = new Button(getContext());
        btnHide.setLayoutParams(btnHideParams);
        btnHide.setText("Hide");
        btnHide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Hide");



                onAnimation(false);

                //_scrollView.getLayoutParams().height = 500;
                //_rootLayout.invalidate();
                //_rootLayout.requestLayout();
            }
        });
        headerLayout.addView(btnHide);
        _headerView = headerLayout;

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LogcatInfo logcatInfo = (LogcatInfo)parent.getItemAtPosition(position);
                logcatInfo.setExtend(!logcatInfo.isExtend());
                _logcatAdapter.notifyDataSetChanged();
            }
        });

        _scrollView.addView(_listView);

        addView(_rootLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        int y = 0;

        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // Find rightmost and bottom-most child
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childRight;
                int childBottom;

                LogcatLayout.LayoutParams lp
                        = (LogcatLayout.LayoutParams) child.getLayoutParams();

                childRight = child.getMeasuredWidth();
                childBottom = y + child.getMeasuredHeight();

                maxWidth = Math.max(maxWidth, childRight);
                maxHeight = Math.max(maxHeight, childBottom);

                y += child.getMeasuredHeight();
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));

        Log.d(TAG, "onMeasure count(" + getChildCount() + ") width(" + maxWidth + ") height(" + maxHeight + ")");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        int y = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                LogcatLayout.LayoutParams lp =
                        (LogcatLayout.LayoutParams) child.getLayoutParams();

                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop() + y;
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());

                y += child.getMeasuredHeight();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent");
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onTouchEvent");
        return false;
    }

    public void w(String tag, String msg) {
        Log.w(tag, msg);
        add(Log.WARN, tag, msg);
    }

    public void d(String tag, String msg) {
        Log.d(tag, msg);
        add(Log.DEBUG, tag, msg);
    }

    public void e(String tag, String msg) {
        Log.e(tag, msg);
        add(Log.ERROR, tag, msg);
    }

    private void add(int logType, String tag, String msg) {
        Log.d(TAG, msg);
        _logcatAdapter.add(new LogcatInfo(logType, tag, msg));
        _logcatAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(_listView);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View mView = adapter.getView(i, null, listView);
            mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void removeAll() {
        _logcatAdapter.clear();
    }

    public class LogcatInfo {
        private final int IGNORE_DEPTH = 0;

        private int _logType = 0;
        private boolean _extend = false;
        private String _miniLog = "";
        private String _shortLog = "";
        private ArrayList<String> _fullLogs = null;

        public LogcatInfo(int logType, String tag, String msg) {
            _logType = logType;

            Date date = Calendar.getInstance().getTime();
            _miniLog = tag + ": " + msg;
            _shortLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(date) + " "
                      + android.os.Process.myPid() + "/" + getContext().getApplicationContext().getPackageName() + " "
                      + getLogTypeToString(_logType) + "/" + tag + ": " + msg;

            _fullLogs = new ArrayList<String>();
            _fullLogs.add(_shortLog);

            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            // Ignore depth not working
            for (int i = IGNORE_DEPTH; i < stacks.length; i++) {
                _fullLogs.add(stacks[i].toString());
            }
        }

        private String getLogTypeToString(int logType) {
            switch (logType) {
                case Log.DEBUG:
                    return "D";
                case Log.WARN:
                    return "W";
                case Log.ERROR:
                    return "E";
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
    }

    // Creating a ListView with custom list items programmatically in Android - no xml list item layout
    // https://stackoverflow.com/questions/12784695/creating-a-listview-with-custom-list-items-programmatically-in-android-no-xml
    public class LogcatAdpater extends ArrayAdapter<LogcatInfo> {

        public LogcatAdpater(@NonNull Context context, ArrayList<LogcatInfo> resource) {
            super(context, -1, -1, resource);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LogcatInfo logcatInfo = super.getItem(position);

            LinearLayout rootLayout = new LinearLayout(getContext());
            rootLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootLayout.setBackgroundColor((getChildCount() % 2 == 0) ? Color.GRAY : Color.DKGRAY);

            if (logcatInfo.isExtend()) {
                rootLayout.setOrientation(LinearLayout.VERTICAL);

                // Java - Stack trace 출력하는 방법 (Throwable, Exception)
                // https://codechacha.com/ko/java-print-stack-trace/
                for (int i = 0; i < logcatInfo.getFullLogs().size(); i++) {
                    LinearLayout singleLineLayout = new LinearLayout(getContext());
                    singleLineLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    singleLineLayout.setOrientation(LinearLayout.VERTICAL);
                    rootLayout.addView(singleLineLayout);

                    TextView textView = new TextView(getContext());
                    textView.setText(logcatInfo.getFullLogs().get(i));
                    textView.setTextColor(Color.WHITE);

                    singleLineLayout.addView(textView);
                }
            } else {
                rootLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(getContext());
                textView.setText(logcatInfo.getMiniLog());
                textView.setTextColor(Color.WHITE);

                rootLayout.addView(textView);
            }

            return rootLayout;
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LogcatLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LogcatLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            //TypedArray a = c.obtainStyledAttributes(attrs,
            //        com.android.internal.R.styleable.AbsoluteLayout_Layout);
            //x = a.getDimensionPixelOffset(
            //        com.android.internal.R.styleable.AbsoluteLayout_Layout_layout_x, 0);
            //y = a.getDimensionPixelOffset(
            //        com.android.internal.R.styleable.AbsoluteLayout_Layout_layout_y, 0);
            //a.recycle();
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}