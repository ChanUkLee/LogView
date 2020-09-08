package com.vader87.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
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

        public void setSelectionTracaker(final SelectionTracker<Long> selectionTracaker) {
            if (selectionTracaker != null && selectionTracaker.isSelected((long)getAdapterPosition()) == true) {
                // [Android] Cannot call this method while RecyclerView is computing a layout or scrolling
                // https://gogorchg.tistory.com/entry/Android-Cannot-call-this-method-while-RecyclerView-is-computing-a-layout-or-scrolling
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        if (0 <= getAdapterPosition() && getAdapterPosition() < _resource.size()) {
                            if (ConsoleDialog.isShow == false) {
                                ConsoleDialog.getInstance(_resource.get(getAdapterPosition())).show(((Activity)getContext()).getFragmentManager(), TAG);
                            }
                        }
                        selectionTracaker.deselect((long)getAdapterPosition());
                    }
                };
                handler.post(r);
            }
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return new ItemDetailsLookup.ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    return getAdapterPosition();
                }

                @Nullable
                @Override
                public Long getSelectionKey() {
                    return getItemId();
                }

                @Override
                public boolean inSelectionHotspot(@NonNull MotionEvent e) {
                    return true;
                }
            };
        }
    }

    public ConsoleAdpater(ArrayList<ConsoleLog> resource) {
        _resource = resource;
        setHasStableIds(true);
    }

    private SelectionTracker<Long> _selectionTracker = null;

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        _selectionTracker = selectionTracker;
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
        viewHoler.setSelectionTracaker(_selectionTracker);
    }

    @Override
    public int getItemCount() {
        return _resource.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
