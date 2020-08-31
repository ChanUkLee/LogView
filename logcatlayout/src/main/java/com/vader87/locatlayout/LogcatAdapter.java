package com.vader87.locatlayout;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// Creating a ListView with custom list items programmatically in Android - no xml list item layout
// https://stackoverflow.com/questions/12784695/creating-a-listview-with-custom-list-items-programmatically-in-android-no-xml
class LogcatAdpater extends ArrayAdapter<LogcatInfo> {

    public LogcatAdpater(@NonNull Context context, ArrayList<LogcatInfo> resource) {
        super(context, -1, -1, resource);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LogcatInfo logcatInfo = super.getItem(position);

        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootLayout.setBackgroundColor((position % 2 == 0) ? Color.GRAY : Color.DKGRAY);

        if (logcatInfo.isExtend()) {
            rootLayout.setOrientation(LinearLayout.VERTICAL);

            // Java - Stack trace 출력하는 방법 (Throwable, Exception)
            // https://codechacha.com/ko/java-print-stack-trace/
            for (int i = 0; i < logcatInfo.getFullLogs().size(); i++) {
                LinearLayout singleLineLayout = new LinearLayout(getContext());
                singleLineLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                singleLineLayout.setOrientation(LinearLayout.VERTICAL);
                rootLayout.addView(singleLineLayout);

                TextView textView = new TextView(getContext());
                textView.setText(logcatInfo.getFullLogs().get(i));
                textView.setTextColor(Color.WHITE);

                singleLineLayout.addView(textView);
            }
        } else {
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(getContext());
            textView.setText(logcatInfo.getMiniLog());
            textView.setTextColor(Color.WHITE);

            rootLayout.addView(textView);
        }

        return rootLayout;
    }
}
