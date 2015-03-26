package com.staticbloc.widgets.recyclerview;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 3/8/2015
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
public final class RecyclerViewReorderer implements View.OnDragListener {
    private DraggableRecyclerView recyclerView;
    private ReordererAdapterDecorator adapter;

    private LayoutManagerOrientation orientation;
    private ClipData clipData;
    private OnReorderingListener onReorderingListener;

    private boolean mIsReordering = false;

    /**
     *
     * @param orientation the orientation of the {@link android.support.v7.widget.RecyclerView.LayoutManager}
     * @param label the label will be used to make sure the dragged item is ours
     */
    public RecyclerViewReorderer(LayoutManagerOrientation orientation, String label) {
        this(orientation, new ClipData(label, new String[]{}, new ClipData.Item("")), null);
    }

    /**
     *
     * @param orientation the orientation of the {@link android.support.v7.widget.RecyclerView.LayoutManager}
     * @param label the label will be used to make sure the dragged item is ours
     * @param onReorderingListener can be null
     */
    public RecyclerViewReorderer(LayoutManagerOrientation orientation, String label, OnReorderingListener onReorderingListener) {
        this(orientation, new ClipData(label, new String[]{}, new ClipData.Item("")), onReorderingListener);
    }

    /**
     *
     * @param orientation the orientation of the {@link android.support.v7.widget.RecyclerView.LayoutManager}
     * @param clipData its description's label will be used to make sure the dragged item is ours
     *
     * @throws java.lang.IllegalArgumentException if {@code clipData}, its description, or its description's label is {@code null}
     */
    public RecyclerViewReorderer(LayoutManagerOrientation orientation, ClipData clipData) {
        this(orientation, clipData, null);
    }

    /**
     *
     * @param orientation the orientation of the {@link android.support.v7.widget.RecyclerView.LayoutManager}
     * @param clipData its description's label will be used to make sure the dragged item is ours
     * @param onReorderingListener can be null
     *
     * @throws java.lang.IllegalArgumentException if {@code clipData}, its description, or its description's label is {@code null}
     */
    public RecyclerViewReorderer(LayoutManagerOrientation orientation, ClipData clipData, OnReorderingListener onReorderingListener) {
        this.orientation = orientation;
        if(clipData == null) throw new IllegalArgumentException("ClipData cannot be null");
        if(clipData.getDescription() == null) throw new IllegalArgumentException("ClipData' description cannot be null");
        if(clipData.getDescription().getLabel() == null) throw new IllegalArgumentException("ClipData' description's label cannot be null");
        this.clipData = clipData;
        this.onReorderingListener = onReorderingListener;
    }

    /**
     * Should be followed by a call to {@link RecyclerViewReorderer#setAdapter} or {@link RecyclerViewReorderer#swapAdapter}.
     *
     * <br/>
     *
     * This {@link android.support.v7.widget.RecyclerView}'s {@link android.support.v7.widget.RecyclerView.Adapter} will be set in those calls,
     * so there is no need to set them on the {@code RecyclerView} itself.
     *
     * @throws java.lang.IllegalArgumentException if {@code recyclerView} is {@code null}
     */
    public void setRecyclerView(DraggableRecyclerView recyclerView) {
        if(recyclerView == null) throw new IllegalArgumentException("RecyclerView cannot be null");
        this.recyclerView = recyclerView;
        this.recyclerView.setOnDragListener(this);
    }

    /**
     * This will set the {@link android.support.v7.widget.RecyclerView.Adapter} on the underlying {@link android.support.v7.widget.RecyclerView}.
     * <br/>
     * <b>{@link android.support.v7.widget.RecyclerView#setAdapter} should not be called.</b>
     *
     * @throws java.lang.IllegalStateException if {@link RecyclerViewReorderer#setRecyclerView} has not been called
     */
    public void setAdapter(RecyclerView.Adapter newAdapter) {
        if(recyclerView == null) throw new IllegalStateException("Can't set adapter if RecyclerView is not set");

        adapter = ReordererAdapterDecorator.decorateAdapter(newAdapter, clipData);

        recyclerView.setAdapter(adapter);
    }

    /**
     * This will swap the {@link android.support.v7.widget.RecyclerView.Adapter} on the underlying {@link android.support.v7.widget.RecyclerView}.
     * <br/>
     * <b>{@link android.support.v7.widget.RecyclerView#swapAdapter} should not be called.</b>
     *
     * @throws java.lang.IllegalStateException if {@link RecyclerViewReorderer#setRecyclerView} has not been called
     */
    public void swapAdapter(RecyclerView.Adapter adapterToSwap, boolean removeAndRecycleExistingViews) {
        if(recyclerView == null) throw new IllegalStateException("Can't set adapter if RecyclerView is not set");

        adapter = ReordererAdapterDecorator.decorateAdapter(adapterToSwap, clipData);

        recyclerView.swapAdapter(adapter, removeAndRecycleExistingViews);
    }

    /**
     * Call this if the {@link android.support.v7.widget.RecyclerView.LayoutManager}'s orientation changes
     */
    public void setOrientation(LayoutManagerOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     * As per this bug - https://code.google.com/p/android/issues/detail?id=25073
     */
    public boolean dispatchDragEvent(DragEvent ev) {
        boolean r = recyclerView.superDispatchDragEvent(ev);
        if (r && (ev.getAction() == DragEvent.ACTION_DRAG_STARTED
                || ev.getAction() == DragEvent.ACTION_DRAG_ENDED)){
            // If we got a start or end and the return value is true, our
            // onDragEvent wasn't called by ViewGroup.dispatchDragEvent
            // So we do it here.
            onDragEvent(ev);
            return true;
        }
        else {
            return true;
        }
    }

    /*package*/ boolean onDragEvent(DragEvent ev) {
        return onDrag(null, ev);
    }

    private final Handler scrollHandler = new Handler();
    private int scrollDistance;
    private boolean isScrolling = false;
    private final Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            if(isScrolling) {
                if(orientation == LayoutManagerOrientation.VERTICAL) {
                    recyclerView.smoothScrollBy(0, scrollDistance);
                }
                else {
                    recyclerView.smoothScrollBy(scrollDistance, 0);
                }
                scrollHandler.postDelayed(this, 250);
            }
        }
    };

    private int lastKnownPosition = RecyclerView.NO_POSITION;
    @Override
    public boolean onDrag(View v, DragEvent ev) {
        ClipDescription desc = ev.getClipDescription();
        // if this isn't our dragged item, ignore it
        if(desc != null && !desc.getLabel().equals(clipData.getDescription().getLabel())) {
            return false;
        }

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        RecyclerView.ViewHolder viewHolderToSendToAdapter = null;

        if(ev.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
            // reset our last known position for a new drag
            lastKnownPosition = AbsListView.INVALID_POSITION;
        }
        else if(ev.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
            boolean isVertical = (orientation == LayoutManagerOrientation.VERTICAL);
            int pointer = (isVertical ? y : x);
            int bottomBound = (isVertical ? recyclerView.getHeight() : recyclerView.getWidth());
            int bottomOffset = bottomBound - pointer;

            // TODO: this might not be the best way to get the first visible view
            View firstVisibleView = recyclerView.findChildViewUnder(recyclerView.getX(), recyclerView.getY());
            scrollDistance = bottomBound / 8;
            int scrollThreshold = scrollDistance / 2;
            if(firstVisibleView != null) {
                int bound = (isVertical ? firstVisibleView.getHeight() : firstVisibleView.getWidth());
                scrollDistance = ((bound * 2) + (bound / 2));
                scrollThreshold = bound / 8;
            }

            if(pointer <= scrollThreshold && bottomOffset >= scrollThreshold) {
                scrollDistance = -scrollDistance;
                if(!isScrolling) {
                    scrollHandler.post(scrollRunnable);
                    isScrolling = true;
                }
            }
            else if(pointer >= scrollThreshold && bottomOffset <= scrollThreshold) {
                if(!isScrolling) {
                    scrollHandler.post(scrollRunnable);
                    isScrolling = true;
                }
            }
            else {
                scrollHandler.removeCallbacks(scrollRunnable);
                isScrolling = false;
            }

            // get the view that the dragged item is over
            View viewAtCurrentPosition = recyclerView.findChildViewUnder(ev.getX(), ev.getY());
            if(viewAtCurrentPosition != null) {
                // and get its position
                int currentPosition = recyclerView.getChildLayoutPosition(viewAtCurrentPosition);
                // this is an optimization so that we don't keep sending the same ViewHolder to the adapter
                // for every pixel that it moves (which would be redundant anyway)
                if(currentPosition != lastKnownPosition) {
                    // if it's a valid position, use it to get the ViewHolder to send to the adapter
                    if(currentPosition != RecyclerView.NO_POSITION) {
                        viewHolderToSendToAdapter = recyclerView.findViewHolderForLayoutPosition(currentPosition);
                    }
                    // this position is now our known position
                    lastKnownPosition = currentPosition;
                }
            }
            else { // if we can't get the view we're over, we don't have a known position
                lastKnownPosition = RecyclerView.NO_POSITION;
            }
        }
        else if(ev.getAction() == DragEvent.ACTION_DROP) {
            // stop scrolling
            scrollHandler.removeCallbacks(scrollRunnable);
            isScrolling = false;

            // get the view that the dragged item is over
            View viewAtCurrentPosition = recyclerView.findChildViewUnder(ev.getX(), ev.getY());
            if(viewAtCurrentPosition != null) {
                // and get its position
                int currentPosition = recyclerView.getChildLayoutPosition(viewAtCurrentPosition);
                // if it's a valid position, use it to get the ViewHolder to send to the adapter
                if(currentPosition != RecyclerView.NO_POSITION) {
                    viewHolderToSendToAdapter = recyclerView.findViewHolderForLayoutPosition(currentPosition);
                }
            }
            // reset our last known position since we dropped
            lastKnownPosition = RecyclerView.NO_POSITION;
        }
        else if(ev.getAction() == DragEvent.ACTION_DRAG_ENDED) {
            setIsReordering(false);

            // stop scrolling
            scrollHandler.removeCallbacks(scrollRunnable);
            isScrolling = false;

            // reset our last known position since we're done
            lastKnownPosition = RecyclerView.NO_POSITION;
        }

        return adapter != null && adapter.onDrag(viewHolderToSendToAdapter, ev);
    }

    public boolean isReordering() {
        return mIsReordering;
    }

    /*package*/ void setIsReordering(boolean isReordering) {
        this.mIsReordering = isReordering;
        if(onReorderingListener != null) {
            if(isReordering) {
                onReorderingListener.onStartReordering();
            }
            else {
                onReorderingListener.onStopReordering();
            }
        }
    }

  /**
   *
   * @param position should always be the layout position, <b>NOT</b> the adapter position
   */
    public boolean startReorder(int position) {
        if(isReordering()) {
            throw new IllegalStateException("Cannot start reordering if a reordering operation is already in progress");
        }
        if(recyclerView.getAdapter() == null) {
            throw new IllegalStateException("Cannot start a reorder operation if there is no adapter set");
        }
        if(position < 0 || position >= recyclerView.getAdapter().getItemCount()) {
            throw new IndexOutOfBoundsException("Cannot start a reorder operation if the position is out of the bounds of the adapter");
        }

        // TODO: custom DragShadowBuilder

        View.DragShadowBuilder dragShadowBuilder;
        View viewAtReorderPosition = recyclerView.findViewHolderForLayoutPosition(position).itemView;
        dragShadowBuilder = new View.DragShadowBuilder(viewAtReorderPosition);

        boolean success = recyclerView.startDrag(clipData, dragShadowBuilder, null, 0);

        if(success) {
            setIsReordering(true);
            adapter.startReordering(position);
        }

        return success;
    }
}
