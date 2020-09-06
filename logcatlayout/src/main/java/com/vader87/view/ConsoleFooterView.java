package com.vader87.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

class ConsoleFooterView extends LinearLayout {

    private ImageButton _imageButtonDelete = null;
    private ImageButton _imageButtonLock = null;
    private ImageButton _imageButtonCopy = null;
    private ImageButton _imageButtonMail = null;
    private ImageButton _imageButtonClose = null;

    public ConsoleFooterView(Context context) {
        super(context);
        initView();
    }

    public ConsoleFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ConsoleFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ConsoleFooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.view_console_fotter, this);

        _imageButtonDelete = (ImageButton)view.findViewById(R.id.imagebutton_console_fotter_delete);
        _imageButtonLock = (ImageButton)view.findViewById(R.id.imagebutton_console_fotter_lock);
        _imageButtonCopy = (ImageButton)view.findViewById(R.id.imagebutton_console_fotter_copy);
        _imageButtonMail = (ImageButton)view.findViewById(R.id.imagebutton_console_fotter_mail);
        _imageButtonClose = (ImageButton)view.findViewById(R.id.imagebutton_console_fotter_close);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        _imageButtonDelete.setOnClickListener(onClickListener);
        _imageButtonLock.setOnClickListener(onClickListener);
        _imageButtonCopy.setOnClickListener(onClickListener);
        _imageButtonMail.setOnClickListener(onClickListener);
        _imageButtonClose.setOnClickListener(onClickListener);
    }
}
