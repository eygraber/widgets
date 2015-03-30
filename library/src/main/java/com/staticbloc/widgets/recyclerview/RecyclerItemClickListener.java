package com.staticbloc.widgets.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 3/9/2015
 * Time: 3:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    public static interface OnItemClickListener {
        public void onItemClick(View view, int adapterPosition, int layoutPosition);

      /**
       * 
       * @return {@code true} if handled, otherwise {@code false}
       */
        public boolean onItemLongClick(View view, int adapterPosition, int layoutPosition);
    }

    public static class OnItemClickAdapter implements OnItemClickListener {
        @Override
        public void onItemClick(View view, int adapterPosition, int layoutPosition) {}

        @Override
        public boolean onItemLongClick(View view, int adapterPosition, int layoutPosition) { return false; }
    }

    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if(childView != null && mListener != null) {
                    childView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView), recyclerView.getChildLayoutPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());

        if(childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            childView.playSoundEffect(SoundEffectConstants.CLICK);
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView), view.getChildLayoutPosition(childView));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {}
}