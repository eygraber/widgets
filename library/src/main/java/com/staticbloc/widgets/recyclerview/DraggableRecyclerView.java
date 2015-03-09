package com.staticbloc.widgets.recyclerview;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 3/9/2015
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DraggableRecyclerView {
    public boolean dispatchDragEvent(DragEvent ev);

    public boolean superDispatchDragEvent(DragEvent ev);

    public boolean onDragEvent(DragEvent ev);

    public void setOnDragListener(View.OnDragListener listener);

    public RecyclerView.Adapter getAdapter();

    public void setAdapter(RecyclerView.Adapter adapter);

    public void swapAdapter(RecyclerView.Adapter adapter, boolean removeAndRecycleExistingViews);

    public void smoothScrollBy(int x, int y);

    public int getWidth();

    public int getHeight();

    public float getX();

    public float getY();

    public View findChildViewUnder(float x, float y);

    public int getChildPosition(View view);

    public RecyclerView.ViewHolder findViewHolderForPosition(int position);

    public boolean startDrag(ClipData data, View.DragShadowBuilder shadowBuilder, Object myLocalState, int flags);
}
