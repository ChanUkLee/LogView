package com.vader87.view;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ConsoleDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "dialog_console";
    public static boolean isShow = false;

    public static ConsoleDialog getInstance(ConsoleLog consoleLog) {
        ConsoleDialog consoleDialog = new ConsoleDialog();
        Bundle args = new Bundle();
        args.putInt("type", consoleLog.getLogType());
        args.putString("summary", consoleLog.getSummary());
        args.putString("log", consoleLog.getLog());
        consoleDialog.setArguments(args);
        return consoleDialog;
    }

    private int getLogTypeIcon(int logType) {
        switch (logType) {
            case Log.ERROR:
                return R.drawable.dialog_icon_error;
            case Log.WARN:
                return R.drawable.dialog_icon_warn;
            case Log.DEBUG:
            default:
                break;
        }
        return R.drawable.dialog_icon_debug;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        isShow = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShow = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_console, container);
        ImageButton btnClose = (ImageButton)view.findViewById(R.id.button_dialog_console_close);
        btnClose.setOnClickListener(this);
        ImageButton btnCopy = (ImageButton)view.findViewById(R.id.button_dialog_console_copy);
        btnCopy.setOnClickListener(this);

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linearlayout_dialog_console);

        int logType = getArguments().getInt("type");
        String summary = getArguments().getString("summary");
        String log = getArguments().getString("log");

        TextView textViewTitle = (TextView)view.findViewById(R.id.textview_dialog_console_title);
        textViewTitle.setText(summary);
        textViewTitle.setCompoundDrawablesWithIntrinsicBounds(getLogTypeIcon(logType), 0, 0, 0);

        TextView textView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.textview_dialog_console_callstack, null);
        textView.setText(log);
        linearLayout.addView(textView);

        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        Objects.requireNonNull(getDialog().getWindow()).setLayout((int)(point.x * 0.9), (int)(point.y * 0.9));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_dialog_console_copy) {
            ClipboardManager clipboardManager = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("stacktrace", getArguments().getString("stacktrace"));
            assert clipboardManager != null;
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getContext(), "Copy to clipboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.button_dialog_console_close) {
            dismiss();
        }
    }
}
