package com.staticbloc.widgets;

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
        /**
         * If {@code true} is returned {@link View#playSoundEffect(int)} will be called
         * @return {@code true} if handled, otherwise {@code false}
         */
        public boolean onItemClick(View view, int adapterPosition, int layoutPosition);

      /**
       * If {@code true} is returned and haptic feedback is enabled on the device
       * {@link View#performHapticFeedback(int)} will be called
       * @return {@code true} if handled, otherwise {@code false}
       */
        public boolean onItemLongClick(View view, int adapterPosition, int layoutPosition);
    }

    public static class OnItemClickAdapter implements OnItemClickListener {
        @Override
        public boolean onItemClick(View view, int adapterPosition, int layoutPosition) {
            return false;
        }

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
                    if(mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView), recyclerView.getChildLayoutPosition(childView))) {
                        childView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    }
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());

        if(childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            if(mListener.onItemClick(childView, view.getChildAdapterPosition(childView), view.getChildLayoutPosition(childView))) {
                childView.playSoundEffect(SoundEffectConstants.CLICK);
            }
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {}
}