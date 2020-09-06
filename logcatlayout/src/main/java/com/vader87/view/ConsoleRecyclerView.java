package com.vader87.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;

class ConsoleRecyclerView extends RecyclerView {

    public ConsoleRecyclerView(Context context) {
        super(context);
        initView();
    }

    public ConsoleRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ConsoleRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View view = View.inflate(getContext(), R.layout.view_console_recyclerview, this);
    }
}
