package com.vader87.view;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

class ConsoleDetailsLookUp extends ItemDetailsLookup<Long> {
    private ConsoleRecyclerView _consoleRecyclerView = null;
    public ConsoleDetailsLookUp(ConsoleRecyclerView consoleRecyclerView) {
        _consoleRecyclerView = consoleRecyclerView;
    }
    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = _consoleRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null ) {
            ConsoleAdpater.ConsoleItemViewHolder viewHolder = (ConsoleAdpater.ConsoleItemViewHolder)_consoleRecyclerView.getChildViewHolder(view);
            return viewHolder.getItemDetails();
        }
        return null;
    }
}
