package com.staticbloc.widgets.recyclerview;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 3/8/2015
 * Time: 11:44 PM
 * To change this template use File | Settings | File Templates.
 */

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * A decorator for {@link android.support.v7.widget.RecyclerView.Adapter}. It adds reordering capabilities.
 * @param <VH> the type of ViewHolder the wrapped {@code Adapter} holds
 *            (inferred from {@link ReordererAdapterDecorator#decorateAdapter})
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/*package*/ final class ReordererAdapterDecorator<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final int INVALID = -1;

    // the Adapter that we're decorating
    private RecyclerView.Adapter<VH> decoratedAdapter;
    private ClipData clipData;

    private int mReorderPosition = INVALID;
    private int mDropPosition = INVALID;

    private ReordererAdapterDecorator(final RecyclerView.Adapter<VH> adapterToDecorate, ClipData clipData) {
        this.decoratedAdapter = adapterToDecorate;
        this.clipData = clipData;
    }

    /**
     * Returns a {@code ReordererAdapterDecorator} that decorates the
     * {@link android.support.v7.widget.RecyclerView.Adapter}.
     * {@code adapterToDecorate} cannot be {@code null} and must be-a {@link ReorderableAdapter}.
     * @param adapterToDecorate the {@code Adapter} to decorate
     * @param <VH> the type of ViewHolder that {@code adapterToDecorate} holds
     * @throws java.lang.NullPointerException if {@code adapterToDecorate} is {@code null}
     * @throws java.lang.IllegalArgumentException if {@code adapterToDecorate} is not a {@code ReorderableAdapter}
     */
    /*package*/ static <VH extends RecyclerView.ViewHolder> ReordererAdapterDecorator<VH>
    decorateAdapter(final RecyclerView.Adapter<VH> adapterToDecorate, ClipData clipData) {
        if(adapterToDecorate == null) {
            throw new NullPointerException("reorderable adapter cannot be null");
        }
        if(!(adapterToDecorate instanceof ReorderableAdapter)) {
            throw new IllegalArgumentException("reorderable adapter must implement ReorderableAdapter or one of its subinterfaces");
        }
        return new ReordererAdapterDecorator<>(adapterToDecorate, clipData);
    }

    /**
     * Called when a reordering operation is started on the {@link RecyclerView}.
     * @param position the position that the reordering operation was started on
     */
    /*package*/ void startReordering(int position) {
        mReorderPosition = position;
        mDropPosition = position;
        if(decoratedAdapter != null) decoratedAdapter.notifyItemChanged(position);
    }

    private boolean isReordering() {
        return mReorderPosition != INVALID;
    }

    /**
     * Notifies any observers about the range of items that were changed.
     */
    private void notifyPositionRangeChanged(int initialRangePosition, int currentRangePosition) {
        if(currentRangePosition == INVALID) {
            return;
        }

        if(initialRangePosition == INVALID) {
            initialRangePosition = currentRangePosition;
        }

        if(initialRangePosition == currentRangePosition) {
            if(decoratedAdapter != null) decoratedAdapter.notifyItemChanged(currentRangePosition);
            return;
        }

        int max = Math.max(initialRangePosition, currentRangePosition);
        int min = Math.min(initialRangePosition, currentRangePosition);

        int changedCount = (max + 1) - min;
        decoratedAdapter.notifyItemRangeChanged(min, changedCount);
    }

    /**
     * Receive drag events from {@link RecyclerViewReorderer#onDrag}.
     * @param holder the {@link android.support.v7.widget.RecyclerView.ViewHolder} at this drag position, or null
     * @param e the {@link DragEvent}
     * @param <T> the type of {@code ViewHolder}
     * @return whether the drag event was handled
     */
    /*package*/ <T extends RecyclerView.ViewHolder> boolean onDrag(T holder, DragEvent e) {
        ClipDescription desc = e.getClipDescription();
        // if this isn't our dragged item, ignore it
        if(desc != null && !desc.getLabel().equals(clipData.getDescription().getLabel())) {
            return false;
        }

        if(e.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
            int oldDropPosition = mDropPosition;
            if(holder != null) {
                mDropPosition = holder.getPosition();
            }

            notifyPositionRangeChanged(oldDropPosition, mDropPosition);
        }
        else if(e.getAction() == DragEvent.ACTION_DROP) {
            int oldDropPosition = mDropPosition;
            if(holder != null) {
                mDropPosition = holder.getPosition();
            }

            notifyPositionRangeChanged(oldDropPosition, mDropPosition);
            if(mDropPosition != INVALID) {
                if(decoratedAdapter != null) ((ReorderableAdapter) decoratedAdapter).onItemDropped(mReorderPosition, mDropPosition);
                notifyPositionRangeChanged(mReorderPosition, mDropPosition);
            }
            mReorderPosition = INVALID;
            mDropPosition = INVALID;
        }
        else if(e.getAction() == DragEvent.ACTION_DRAG_ENDED) {
            notifyPositionRangeChanged(mReorderPosition, mDropPosition);
            mReorderPosition = INVALID;
            mDropPosition = INVALID;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return decoratedAdapter == null ? 0 : decoratedAdapter.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return decoratedAdapter == null ? 0 : decoratedAdapter.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return decoratedAdapter == null ? 0 : decoratedAdapter.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if(decoratedAdapter == null) return;

        if(!isReordering()) { //if we're not reordering, don't do anything to the view (unless we have to undo the drop position decoration)
            decoratedAdapter.onBindViewHolder(holder, position);
            if(decoratedAdapter instanceof ReorderableAdapterViewDecorator) {
                ((ReorderableAdapterViewDecorator) decoratedAdapter).undoDropPositionDecoration(holder.itemView);
            }
            else {
                if(holder.itemView.getVisibility() == View.INVISIBLE) {
                    holder.itemView.setVisibility(View.VISIBLE);
                }
            }
        }
        else if(holder.getPosition() == mDropPosition) { // if it's the drop position, apply the drop position decoration
            decoratedAdapter.onBindViewHolder(holder, position);
            if(decoratedAdapter instanceof ReorderableAdapterViewDecorator) {
                ((ReorderableAdapterViewDecorator) decoratedAdapter).applyDropPositionDecoration(holder.itemView);
            }
            else {
                holder.itemView.setVisibility(View.INVISIBLE);
            }
        }
        else { //if we're reordering but it's not the drop position, resolve the position (and undo the drop position decoration if needed)
            decoratedAdapter.onBindViewHolder(holder, resolvePosition(position));
            if(decoratedAdapter instanceof ReorderableAdapterViewDecorator) {
                ((ReorderableAdapterViewDecorator) decoratedAdapter).undoDropPositionDecoration(holder.itemView);
            }
            else {
                if(holder.itemView.getVisibility() == View.INVISIBLE) {
                    holder.itemView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Resolve the position to where it needs to be (shifted +/- 1 because of drop position, or not affected).
     * @param position the position to resolve
     * @return the resolved position
     */
    private int resolvePosition(final int position) {
        if(mDropPosition < mReorderPosition && position > mDropPosition && position <= mReorderPosition) {
            return position - 1;
        }
        else if(mDropPosition > mReorderPosition && position < mDropPosition && position >= mReorderPosition) {
            return position + 1;
        }
        else {
            return position;
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return decoratedAdapter == null ? null : decoratedAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        if(decoratedAdapter != null) decoratedAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        if(decoratedAdapter != null) decoratedAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(VH holder) {
        if(decoratedAdapter != null) decoratedAdapter.onViewRecycled(holder);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if(decoratedAdapter != null) decoratedAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        if(decoratedAdapter != null) decoratedAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if(decoratedAdapter != null) decoratedAdapter.unregisterAdapterDataObserver(observer);
    }
}
