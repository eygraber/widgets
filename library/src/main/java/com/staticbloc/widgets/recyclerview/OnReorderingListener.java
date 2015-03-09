package com.staticbloc.widgets.recyclerview;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 3/8/2015
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Listen for changes in a {@link android.support.v7.widget.RecyclerView}'s reordering state.
 */
public interface OnReorderingListener {
    /**
     * Called when a {@link android.support.v7.widget.RecyclerView} has started reordering.
     */
    public void onStartReordering();

    /**
     * Called when a {@link android.support.v7.widget.RecyclerView} has stopped reordering.
     */
    public void onStopReordering();
}
