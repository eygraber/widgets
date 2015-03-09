package com.staticbloc.widgets.recyclerview;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 3/8/2015
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReorderableAdapter {
    /**
     * Called when an item that was being dragged is dropped. Most implementations will want to
     * remove the item from the dataset at {@code from} and reinsert it at {@code to}.
     * @param from the position the item was dragged from
     * @param to the position the item was dropped on
     */
    public void onItemDropped(int from, int to);
}
