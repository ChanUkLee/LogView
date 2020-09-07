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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

// Creating a ListView with custom list items programmatically in Android - no xml list item layout
// https://stackoverflow.com/questions/12784695/creating-a-listview-with-custom-list-items-programmatically-in-android-no-xml
class ConsoleAdpater extends RecyclerView.Adapter<ConsoleAdpater.ConsoleItemViewHolder> {

    private static final String TAG = "ConsoleAdapter";
    private ArrayList<ConsoleLog> _resource = null;

    class ConsoleItemViewHolder extends RecyclerView.ViewHolder {
        public View _itemView = null;
        private TextView _textView = null;
        public ConsoleItemViewHolder(View itemView) {
            super(itemView);
            _itemView = itemView;
            _textView = (TextView)itemView.findViewById(R.id.textview_console_recycleview_item);
            _textView.setClickable(false);
        }

        public Context getContext() {
            return _textView.getContext();
        }

        public void setBackgroundColor(int color) {
            _itemView.setBackgroundColor(color);
        }

        public void setText(String text) {
            _textView.setText(text);
        }

        public void setIcon(int icon) {
            _textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        }
    }

    public ConsoleAdpater(ArrayList<ConsoleLog> resource) {
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
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_console_recycleview_item, parent, false);
        ConsoleItemViewHolder viewHolder = new ConsoleItemViewHolder(view);
        return viewHolder;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final ConsoleItemViewHolder holder, final int position) {
        ConsoleItemViewHolder viewHoler = (ConsoleItemViewHolder)holder;
        viewHoler.setBackgroundColor((position % 2 == 0) ? viewHoler.getContext().getColor(R.color.colorConsoleListViewTextBackgroundA) : viewHoler.getContext().getColor(R.color.colorConsoleListViewTextBackgroundB));
        viewHoler.setText(_resource.get(position).getSummary());
        viewHoler.setIcon(getLogTypeIcon(_resource.get(position).getLogType()));
        viewHoler._itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
            }
        });
        viewHoler._itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange:" + hasFocus);
            }
        });
        viewHoler._itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch:" + event.getAction());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return _resource.size();
    }
}
