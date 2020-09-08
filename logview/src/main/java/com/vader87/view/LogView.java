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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.selection.MutableSelection;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vader87.view.service.ILogBinder;
import com.vader87.view.service.ILogCallback;
import com.vader87.view.service.LogService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
public class LogView extends LinearLayout {

    private final String TAG = "LogView";

    private ILogBinder _logBinder = null;

    private LinearLayout _linearLayout = null;
    private View _btnShowView = null;
    private ConsoleHeaderView _headerView = null;
    private ConsoleFooterView _footerView = null;
    private ConsoleRecyclerView _recyclerView = null;
    private ConsoleAdpater _recyclerAdapter = null;
    private RecyclerView.LayoutManager _layoutManager = null;

    private boolean _ignoreConsoleLogDebug = false;
    private boolean _ignoreConsoleLogWarn = false;
    private boolean _ignoreConsoleLogError = false;
    private int _consoleLogDebugCount = 0;
    private int _consoleLogWarnCount = 0;
    private int _consoleLogErrorCount = 0;
    private ArrayList<ConsoleLog> _consoleLogList = null;
    private ArrayList<ConsoleLog> _consoleLogViewList = null;

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
        _consoleLogList = new ArrayList<ConsoleLog>();
        _consoleLogViewList = new ArrayList<ConsoleLog>();

        View view = View.inflate(getContext(), R.layout.view_log, this);

        _btnShowView = (Button)view.findViewById(R.id.button_log_show);
        _btnShowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "btn show onClick", Toast.LENGTH_SHORT);
                show();
            }
        });

        _linearLayout = (LinearLayout)view.findViewById(R.id.linearlayout_view_log);
        _linearLayout.getLayoutParams().height = 0;

        _headerView = (ConsoleHeaderView)view.findViewById(R.id.consoleheaderview_view_log);
        _headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.textview_console_header_debug) {
                    _ignoreConsoleLogDebug = !_ignoreConsoleLogDebug;
                    _headerView.setIgnoreDebug(_ignoreConsoleLogDebug);
                    rebuildConsoleLogViewList();
                } else if (id == R.id.textview_console_header_warn) {
                    _ignoreConsoleLogWarn = !_ignoreConsoleLogWarn;
                    _headerView.setIgnoreDebug(_ignoreConsoleLogWarn);
                    rebuildConsoleLogViewList();
                } else if (id == R.id.textview_console_header_error) {
                    _ignoreConsoleLogError = !_ignoreConsoleLogError;
                    _headerView.setIgnoreDebug(_ignoreConsoleLogError);
                    rebuildConsoleLogViewList();
                }
            }
        });

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
                    sendEmail();
                } else if (id == R.id.imagebutton_console_fotter_close) {
                    dismiss();
                }
            }
        });

        _recyclerView = (ConsoleRecyclerView)view.findViewById(R.id.consolerecyclerview_view_log);
        _recyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(getContext());
        _recyclerView.setLayoutManager(_layoutManager);

        _recyclerAdapter = new ConsoleAdpater(_consoleLogViewList);
        _recyclerView.setAdapter(_recyclerAdapter);

        SelectionTracker selectionTracker = new SelectionTracker.Builder<> (
                getContext().getPackageName() + ".selectiontracker",
                _recyclerView,
                new StableIdKeyProvider(_recyclerView),
                new ConsoleDetailsLookUp(_recyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.<Long>createSelectSingleAnything())
                .build();
        _recyclerAdapter.setSelectionTracker(selectionTracker);

        bind();
        //addView(_rootLayout);
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
                    _btnShowView.setEnabled(false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    _btnShowView.setVisibility(VISIBLE);
                    _btnShowView.setEnabled(true);
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

    //region Button Functions
    // Show action
    public void show() {
        onShowOrDismissAnimation(true);
    }

    // Hide action
    public void dismiss() {
        onShowOrDismissAnimation(false);
    }

    private void rebuildConsoleLogViewList() {
        _consoleLogViewList.clear();
        for (int i = 0; i < _consoleLogList.size(); i++) {
            ConsoleLog consoleLog = _consoleLogList.get(i);
            int logType = consoleLog.getLogType();
            switch (logType) {
                case Log.DEBUG:
                    if (_ignoreConsoleLogDebug == false)
                        _consoleLogViewList.add(consoleLog);
                    break;
                case Log.WARN:
                    if (_ignoreConsoleLogWarn == false)
                        _consoleLogViewList.add(consoleLog);
                    break;
                case Log.ERROR:
                    if (_ignoreConsoleLogError == false)
                        _consoleLogViewList.add(consoleLog);
                    break;
                default:
                    break;
            }
        }
        _recyclerAdapter.notifyDataSetChanged();
    }

    private void clear() {
        _headerView.initCounts();

        _consoleLogList.clear();
        _consoleLogViewList.clear();
        _consoleLogDebugCount = 0;
        _consoleLogWarnCount = 0;
        _consoleLogErrorCount = 0;
        _recyclerAdapter.notifyDataSetChanged();
    }

    private void clipboard() {
        ClipboardManager clipboardManager = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("full_log", getFullLog());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), "Copy to clipboard", Toast.LENGTH_SHORT).show();
    }

    // 공통 인텐트 - 이메일
    // https://developer.android.com/guide/components/intents-common?hl=ko#Email
    private void sendEmail() {
        Log.d(TAG, "sendEmail");
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String currentDateStr = simpleDateFormat.format(currentDate);
        Uri attachment = getLogFileUri(currentDateStr);
        composeEmail("LogView_" + currentDateStr, attachment);
    }

    private void composeEmail(String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        //intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (attachment != null) {
            intent.putExtra(Intent.EXTRA_STREAM, attachment);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // How to use support FileProvider for sharing content to other apps?
            // https://stackoverflow.com/questions/18249007/how-to-use-support-fileprovider-for-sharing-content-to-other-apps
            List<ResolveInfo> resInfoList = getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getContext().grantUriPermission(packageName, attachment, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(intent);
        }
    }
    //endregion

    //region Utils
    private String getFullLog() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < _consoleLogList.size(); i ++) {
            stringBuilder.append(_consoleLogList.get(i).getLog());
            stringBuilder.append("\n\r");
        }
        return stringBuilder.toString();
    }

    private Uri getLogFileUri(String currentDateStr) {
        String filename = "log_" + currentDateStr + ".txt";
        writeToFile(filename, getFullLog());
        return getFileUri(filename);
    }

    private void writeToFile(String filename, String string) {
        try {
            File file = new File(getContext().getFilesDir(), filename);
            if (file.exists() == false)
                file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(string);
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private Uri getFileUri(String filename) {
        File file = new File(getContext().getFilesDir(), filename);
        String authority = getContext().getPackageName() + ".fileprovider";
        try {
            return FileProvider.getUriForFile(getContext(), authority, file);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }
    //endrgion

    //region Logcat Service
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
                    addNewLog(newLog);

                }
            });
        }
    };

    private void addNewLog(final String newLog) {
        ConsoleLog consoleLog = new ConsoleLog(newLog);

        int logType = consoleLog.getLogType();
        int count = 0;
        if (_consoleLogList != null && _consoleLogViewList != null) {
            _consoleLogList.add(consoleLog);

            switch (logType) {
                case Log.DEBUG:
                    if (_ignoreConsoleLogDebug == false) {
                        _consoleLogViewList.add(consoleLog);
                    }
                    _consoleLogDebugCount++;
                    count = _consoleLogDebugCount;
                    break;
                case Log.WARN:
                    if (_ignoreConsoleLogWarn == false) {
                        _consoleLogViewList.add(consoleLog);
                    }
                    _consoleLogWarnCount++;
                    count = _consoleLogWarnCount;
                    break;
                case Log.ERROR:
                    if (_ignoreConsoleLogError == false) {
                        _consoleLogViewList.add(consoleLog);
                    }
                    _consoleLogErrorCount++;
                    count = _consoleLogErrorCount;
                    break;
                default:
                    break;
            }

            if (_recyclerAdapter != null) {
                _recyclerAdapter.notifyDataSetChanged();
            }
        }

        updateLogCount(logType, count);
    }

    private void updateLogCount(int logType, int count) {
        if (_headerView != null) {
            _headerView.setCount(logType, count);
        }
    }

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
    //endregion
}