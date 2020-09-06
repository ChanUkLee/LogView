package com.vader87.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Creating a ListView with custom list items programmatically in Android - no xml list item layout
// https://stackoverflow.com/questions/12784695/creating-a-listview-with-custom-list-items-programmatically-in-android-no-xml
class ConsoleAdpater extends RecyclerView.Adapter<ConsoleAdpater.ConsoleItemViewHolder> {
    private ArrayList<LogcatInfo> _resource = null;

    class ConsoleItemViewHolder extends RecyclerView.ViewHolder {
        private TextView _textView = null;
        private LogcatInfo _logcatInfo = null;
        public ConsoleItemViewHolder(TextView textView) {
            super(textView);
            _textView = textView;
            _textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_logcatInfo != null) {
                        ConsoleDialog.getInstance(_logcatInfo).show(((Activity)getContext()).getFragmentManager(), ConsoleDialog.TAG);
                    } else {
                        Toast.makeText(getContext(),"Null Error!", Toast.LENGTH_SHORT);
                    }

                }
            });
        }

        public Context getContext() {
            return _textView.getContext();
        }

        public void setLogcatInfo(LogcatInfo logcatInfo) {
            _logcatInfo = logcatInfo;
        }

        public void setBackgroundColor(int color) {
            _textView.setBackgroundColor(color);
        }

        public void setText(String text) {
            _textView.setText(text);
        }

        public void setIcon(int icon) {
            _textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        }
    }

    public ConsoleAdpater(ArrayList<LogcatInfo> resource) {
        _resource = resource;
    }

    private int getLogTypeIcon(int logType) {
        switch (logType) {
            case Log.ERROR:
                return R.drawable.listview_icon_error;
            case Log.WARN:
                return R.drawable.listview_icon_warn;
            case Log.DEBUG:
            default:
                break;
        }
        return R.drawable.listview_icon_debug;
    }

    @NonNull
    @Override
    public ConsoleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConsoleItemViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_console_recycleview_item, parent, false);
        TextView textView = (TextView)view.findViewById(R.id.textview_console_recycleview_item);
        viewHolder = new ConsoleItemViewHolder(textView);
        return viewHolder;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ConsoleItemViewHolder holder, int position) {
        ConsoleItemViewHolder viewHoler = (ConsoleItemViewHolder)holder;
        viewHoler.setBackgroundColor((position % 2 == 0) ? viewHoler.getContext().getColor(R.color.colorConsoleListViewTextBackgroundA) : viewHoler.getContext().getColor(R.color.colorConsoleListViewTextBackgroundB));
        viewHoler.setText(_resource.get(position).getSummary());
        viewHoler.setIcon(getLogTypeIcon(_resource.get(position).getLogType()));
        viewHoler.setLogcatInfo(_resource.get(position));
    }

    @Override
    public int getItemCount() {
        return _resource.size();
    }
}
