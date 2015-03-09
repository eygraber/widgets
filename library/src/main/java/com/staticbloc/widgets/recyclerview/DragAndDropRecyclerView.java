package com.staticbloc.widgets.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.DragEvent;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 3/9/2015
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class DragAndDropRecyclerView extends RecyclerView implements DraggableRecyclerView {
    private RecyclerViewReorderer reorderer;

    public DragAndDropRecyclerView(Context context) {
        super(context);
    }

    public DragAndDropRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragAndDropRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setReorderer(RecyclerViewReorderer reorderer) {
        this.reorderer = reorderer;
        reorderer.setRecyclerView(this);
    }

    // As per this bug - https://code.google.com/p/android/issues/detail?id=25073
    @Override
    public boolean dispatchDragEvent(DragEvent ev) {
        return reorderer != null && reorderer.dispatchDragEvent(ev);
    }

    @Override
    public boolean superDispatchDragEvent(DragEvent ev) {
        return super.dispatchDragEvent(ev);
    }

    @Override
    public boolean onDragEvent(DragEvent ev) {
        return reorderer != null && reorderer.onDragEvent(ev);
    }
}
