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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;

// How to create a release android library package (aar) in Android Studio (not debug)
// https://stackoverflow.com/questions/27646262/how-to-create-a-release-android-library-package-aar-in-android-studio-not-deb

// JCenter(Bintray) 배포 방법
// http://www.bluebee.co.kr/?p=440
// How to sign AAR Artifacts in Android?
// https://stackoverflow.com/questions/43121499/how-to-sign-aar-artifacts-in-android

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

    private TextView _txtError = null;
    private TextView _txtWarn = null;
    private TextView _txtInfo = null;

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

    protected void initView() {
        // LinearLayout API
        // https://developer.android.com/reference/android/widget/LinearLayout
        _rootLayout = new LinearLayout(getContext());
        _rootLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _rootLayout.setOrientation(LinearLayout.VERTICAL);
        _rootLayout.setGravity(Gravity.CENTER);

        Button button = new Button(getContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText(getContext().getString(R.string.btn_show));
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onAnimation(true);
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

        RelativeLayout.LayoutParams headerLeftLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        headerLeftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        headerLeftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        LinearLayout headerLeftLayout = new LinearLayout(getContext());
        headerLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLeftLayout.setLayoutParams(headerLeftLayoutParams);
        headerLayout.addView(headerLeftLayout);

        _txtInfo = new TextView(getContext());
        _txtInfo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _txtInfo.setTextColor(Color.WHITE);
        _txtInfo.setGravity(Gravity.CENTER_VERTICAL);
        _txtInfo.setSingleLine(true);
        _txtInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.big_icon_log_info, 0, 0, 0);
        headerLeftLayout.addView(_txtInfo);

        Space spaceInfo = new Space(getContext());
        spaceInfo.setLayoutParams(new ViewGroup.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT));
        headerLeftLayout.addView(spaceInfo);

        _txtWarn = new TextView(getContext());
        _txtWarn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _txtWarn.setTextColor(Color.WHITE);
        _txtWarn.setGravity(Gravity.CENTER_VERTICAL);
        _txtWarn.setSingleLine(true);
        _txtWarn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.big_icon_log_warn, 0, 0, 0);
        headerLeftLayout.addView(_txtWarn);

        Space spaceWarn = new Space(getContext());
        spaceWarn.setLayoutParams(new ViewGroup.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT));
        headerLeftLayout.addView(spaceWarn);

        _txtError = new TextView(getContext());
        _txtError.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _txtError.setTextColor(Color.WHITE);
        _txtError.setGravity(Gravity.CENTER_VERTICAL);
        _txtError.setSingleLine(true);
        _txtError.setCompoundDrawablesWithIntrinsicBounds(R.drawable.big_icon_log_error, 0, 0, 0);
        headerLeftLayout.addView(_txtError);

        Space spaceError = new Space(getContext());
        spaceError.setLayoutParams(new ViewGroup.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT));
        headerLeftLayout.addView(spaceError);

        UpdateCountTxt();

        //ImageButton btnHide = new ImageButton(getContext(), null, android.R.attr.borderlessButtonStyle);
        ImageButton btnHide = new ImageButton(getContext());
        btnHide.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnHide.setImageResource(R.drawable.normal_icon_btn_close);
        btnHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnimation(false);
            }
        });
        headerLeftLayout.addView(btnHide);

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
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
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

    public void w(String tag, String msg) {
        add(Log.WARN, tag, msg);
    }

    public void d(String tag, String msg) {
        add(Log.DEBUG, tag, msg);
    }

    public void e(String tag, String msg) {
        add(Log.ERROR, tag, msg);
    }

    private void add(int logType, String tag, String msg) {
        _logcatAdapter.add(new LogcatInfo(getContext(), logType, tag, msg));
        _logcatAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(_listView);
        UpdateCountTxt();
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
        UpdateCountTxt();
    }

    private void UpdateCountTxt() {
        _txtInfo.setText(Integer.toString(_logcatAdapter.getLogTypeCount(Log.DEBUG)));
        _txtWarn.setText(Integer.toString(_logcatAdapter.getLogTypeCount(Log.WARN)));
        _txtError.setText(Integer.toString(_logcatAdapter.getLogTypeCount(Log.ERROR)));
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
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}