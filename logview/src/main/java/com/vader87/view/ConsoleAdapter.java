package com.vader87.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Creating a ListView with custom list items programmatically in Android - no xml list item layout
// https://stackoverflow.com/questions/12784695/creating-a-listview-with-custom-list-items-programmatically-in-android-no-xml
class ConsoleAdpater extends RecyclerView.Adapter<ConsoleAdpater.ConsoleItemViewHolder> {

    private static final String TAG = "ConsoleAdapter";
    private ArrayList<LogcatInfo> _resource = null;

    class ConsoleItemViewHolder extends RecyclerView.ViewHolder {
        public View _view = null;
        private TextView _textView = null;
        public ConsoleItemViewHolder(View view) {
            super(view);
            _view = view;
            //_view.setOnClickListener(this);
            _textView = (TextView)view.findViewById(R.id.textview_console_recycleview_item);
        }

        public Context getContext() {
            return _textView.getContext();
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
        viewHolder = new ConsoleItemViewHolder(view);
        return viewHolder;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final ConsoleItemViewHolder holder, final int position) {
        ConsoleItemViewHolder viewHoler = (ConsoleItemViewHolder)holder;
        viewHoler.setBackgroundColor((position % 2 == 0) ? viewHoler.getContext().getColor(R.color.colorConsoleListViewTextBackgroundA) : viewHoler.getContext().getColor(R.color.colorConsoleListViewTextBackgroundB));
        viewHoler.setText(_resource.get(position).getSummary());
        viewHoler.setIcon(getLogTypeIcon(_resource.get(position).getLogType()));
        //viewHoler._view.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //Log.d(TAG, "onClick");
                //ConsoleDialog.getInstance(_resource.get(position)).show(((Activity)holder.getContext()).getFragmentManager(), ConsoleDialog.TAG);
            //}
        //});
        viewHoler._view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    return false;

                Log.d(TAG, "onTouch " + event.getAction());
                if (ConsoleDialog.isShow == false) {
                    // 0 ACTION_DOWN
                    // 3 ACTION_CANCEL
                    ConsoleDialog.getInstance(_resource.get(position)).show(((Activity)holder.getContext()).getFragmentManager(), ConsoleDialog.TAG);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return _resource.size();
    }
}
