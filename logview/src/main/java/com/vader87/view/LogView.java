package com.vader87.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vader87.view.service.ILogBinder;
import com.vader87.view.service.ILogCallback;
import com.vader87.view.service.LogService;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
public class LogView extends ViewGroup {

    private final String TAG = "LogView";

    private ILogBinder _logBinder = null;

    private LinearLayout _linearLayout = null;
    private View _btnShowView = null;
    private ConsoleHeaderView _headerView = null;
    private ConsoleFooterView _footerView = null;
    private ConsoleRecyclerView _recyclerView = null;
    private ConsoleAdpater _recyclerAdapter = null;
    private RecyclerView.LayoutManager _layoutManager = null;

    private ArrayList<LogcatInfo> _logcatInfoList = null;

    public LogView(Context context) {
        super(context);
        initView();
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LogView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        initView();
    }

    public LogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    // 안드로이드 커스텀 뷰 1편
    // https://myksb1223.github.io/develop_diary/2019/03/23/CustomView-in-Android.html
    protected void initView() {
        // Custom ArrayAdapter
        _logcatInfoList = new ArrayList<LogcatInfo>();

        View view = View.inflate(getContext(), R.layout.view_log, this);

        _btnShowView = (Button)view.findViewById(R.id.button_log_show);
        _btnShowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                show();
            }
        });

        _linearLayout = (LinearLayout)view.findViewById(R.id.linearlayout_view_log);
        _linearLayout.getLayoutParams().height = 0;

        _headerView = (ConsoleHeaderView)view.findViewById(R.id.consoleheaderview_view_log);
        _footerView = (ConsoleFooterView)view.findViewById(R.id.consolefooterview_view_log);
        _footerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.imagebutton_console_fotter_delete) {
                    clear();
                } else if (id == R.id.imagebutton_console_fotter_lock) {
                    Toast.makeText(getContext(), "not ready lock", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.imagebutton_console_fotter_copy) {
                    clipboard();
                } else if (id == R.id.imagebutton_console_fotter_mail) {
                    //Toast.makeText(getContext(), "not ready email", Toast.LENGTH_SHORT).show();
                    sendEmail("chanuklee0227@gmail.com");
                } else if (id == R.id.imagebutton_console_fotter_close) {
                    dismiss();
                }
            }
        });

        _recyclerView = (ConsoleRecyclerView)view.findViewById(R.id.consolerecyclerview_view_log);
        _recyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(getContext());
        _recyclerView.setLayoutManager(_layoutManager);

        _recyclerAdapter = new ConsoleAdpater(_logcatInfoList);
        _recyclerView.setAdapter(_recyclerAdapter);

        bind();
        //addView(_rootLayout);
    }

    // Show action
    public void show() {
        onShowOrDismissAnimation(true);
    }

    // Hide action
    private void dismiss() {
        onShowOrDismissAnimation(false);
    }

    private String getFullLog() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <_logcatInfoList.size();i ++) {
            stringBuilder.append(_logcatInfoList.get(i).getLog());
            stringBuilder.append("\\n");
        }
        return stringBuilder.toString();
    }

    private void saveLog() {
        writeToFile("logs", getFullLog());
    }

    private void writeToFile(String filename, String content) {
        try {
            File dir = new File(getContext().getFilesDir().getAbsolutePath(), "logs");
            File file = new File(dir, "log.txt");
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(file.getPath(), Context.MODE_APPEND));
            outputStreamWriter.write(content);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private void clipboard() {
        ClipboardManager clipboardManager = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("full_log", getFullLog());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), "Copy to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void sendEmail(String sendTo) {
        Log.d(TAG, "sendEmail");
        saveLog();
        File dir = new File(getContext().getFilesDir().getAbsolutePath(), "logs");
        File file = new File(dir, "log.txt");
        if (file.exists() == false) {
            Log.e(TAG,"file not exists");
        }
        if (file.canRead() == false) {
            Log.e(TAG, "file cannot read");
        }
        Uri fileUri = null;
        try {
            //fileUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setType("text/html");
        //emailIntent.setType("text/plain");
        //emailIntent.setPackage("com.google.android.gm");
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {sendTo});
        if (fileUri != null) {
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        }
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        try {
            Log.d(TAG, "sendEmail");
            getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    protected void onShowOrDismissAnimation(final boolean isShow) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;

        ValueAnimator animator = ValueAnimator.ofInt(_linearLayout.getMeasuredHeight(), isShow ? getBottom() : 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer)animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = _linearLayout.getLayoutParams();
                layoutParams.height = value;
                _linearLayout.setLayoutParams(layoutParams);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isShow) {
                    _btnShowView.setVisibility(GONE);
                    //_listView.addHeaderView(_headerView);
                    //_listView.addFooterView(_footerView);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    //_listView.removeHeaderView(_headerView);
                    //_listView.removeFooterView(_footerView);
                    _btnShowView.setVisibility(VISIBLE);
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

                LogView.LayoutParams lp
                        = (LogView.LayoutParams) child.getLayoutParams();

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

                LogView.LayoutParams lp =
                        (LogView.LayoutParams) child.getLayoutParams();

                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop() + y;
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());

                y += child.getMeasuredHeight();
            }
        }
    }

    public void clear() {
        _logcatInfoList.clear();
        LogcatInfo.Counter.clear();
        _headerView.initCounts();
        _recyclerAdapter.notifyDataSetChanged();
    }

    public void onResume() {
        bind();
    }

    public void onPause() {
        stop();
    }

    private void bind() {
        getContext().bindService(new Intent(getContext(), LogService.class), _serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void run() {
        bind();
        try {
            _logBinder.registerLogCallback(_logCallback);
            _logBinder.run();
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void stop() {
        try {
            _logBinder.unregisterCallback(_logCallback);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }

        getContext().unbindService(_serviceConnection);
    }


    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            _logBinder = ILogBinder.Stub.asInterface((IBinder)service);
            LogService.setHandler(_handler);

            run();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            _logBinder = null;
        }
    };

    private ILogCallback _logCallback = new ILogCallback.Stub() {
        @Override
        public void onNewLog(final String newLog) throws RemoteException {
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (_recyclerAdapter != null) {
                        LogcatInfo logcatInfo = new LogcatInfo(newLog);
                        _logcatInfoList.add(logcatInfo);
                        _headerView.setCount(logcatInfo.getLogType(), LogcatInfo.Counter.getCount(logcatInfo.getLogType()));
                        _recyclerAdapter.notifyDataSetChanged();;
                    }
                }
            });
        }
    };

    private Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    Log.e(TAG, (String)msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LogView.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LogView.LayoutParams;
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