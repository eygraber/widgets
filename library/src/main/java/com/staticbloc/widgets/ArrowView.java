package com.staticbloc.widgets;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 12/30/2014
 * Time: 4:47 AM
 * To change this template use File | Settings | File Templates.
 */
/*package*/ final class ArrowView extends BubbleView.Decoration {
    private int widthOffset = 0;
    private int heightOffset = 0;

    public ArrowView(Context context) {
        super(context);

        setBackgroundColor(Color.TRANSPARENT);
        setDecorationColor(Color.WHITE);
    }

    @Override
    protected void onGravityChanged() {
        onLocationChanged();
    }

    @Override
    protected void onLocationChanged() {
        switch(getGravity()) {
            case TOP:
            case BOTTOM:
                calculateHorizontalOffsets();
                break;
            case END:
            case START:
                calculateVerticalOffsets();
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DecorationGravity gravity = getGravity();

        switch(gravity) {
            case TOP:
            case BOTTOM:
                calculateHorizontalOffsets();
                break;
            case END:
            case START:
                calculateVerticalOffsets();
                break;
        }

        View sibling = findSibling();
        if(sibling == null) {
            return;
        }

        int measuredWidth = gravity.isHorizontal() ? findSibling().getMeasuredWidth() : getDecoWidth();
        int measuredHeight = gravity.isVertical() ? findSibling().getMeasuredHeight() : getDecoHeight();

        setMeasuredDimension(measuredWidth, measuredHeight);

        invalidate();
    }

    @Override
    public void drawDecoration(Canvas canvas) {
        drawArrow(canvas);
    }

    private View findSibling() {
        ViewGroup parent = (ViewGroup) getParent();

        if(parent != null) {
          for(int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child != null && child != parent && child != this) {
              return child;
            }
          }
        }

        return null;
    }

    private void calculateHorizontalOffsets() {
        View sibling = findSibling();
        if(sibling == null) {
            return;
        }

        int siblingWidth = sibling.getMeasuredWidth();

        switch(getLocation()) {
            case START:
                widthOffset = 0;
                heightOffset = 0;
                break;
            case CENTER:
                widthOffset = (siblingWidth / 2) - (getDecoWidth() / 2);
                heightOffset = 0;
                break;
            case END:
                widthOffset = siblingWidth - getDecoWidth();
                heightOffset = 0;
                break;
        }
    }

    private void calculateVerticalOffsets() {
        View sibling = findSibling();
        if(sibling == null) {
            return;
        }

        int siblingHeight = sibling.getMeasuredHeight();

        switch(getLocation()) {
            case START:
                widthOffset = 0;
                heightOffset = 0;
                break;
            case CENTER:
                widthOffset = 0;
                heightOffset = (siblingHeight / 2) - (getDecoHeight() / 2);
                break;
            case END:
                widthOffset = 0;
                heightOffset = siblingHeight - getDecoHeight();
                break;
        }
    }

    private void drawArrow(Canvas canvas) {
        switch(getGravity()) {
            case TOP:
                canvas.drawPath(getTopArrowPath(), getPaint());
                break;
            case END:
                canvas.drawPath(getEndArrowPath(), getPaint());
                break;
            case BOTTOM:
                canvas.drawPath(getBottomArrowPath(), getPaint());
                break;
            case START:
                canvas.drawPath(getStartArrowPath(), getPaint());
                break;
        }
    }

    private Path getPath(float rotation, float pivotX, float pivotY, float postRotateTranslationX, float postRotateTranslationY) {
        int widthEnd = widthOffset + getDecoWidth();
        int heightEnd = heightOffset + getDecoHeight();

        Point a = new Point(widthOffset, heightOffset);
        Point b = new Point(((getDecoWidth() / 2) + widthOffset), heightEnd);
        Point c = new Point(widthEnd, heightOffset);
        Point d = new Point(widthOffset, heightOffset);

        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);

        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        mMatrix.postRotate(rotation, pivotX, pivotY);
        if(getGravity().isHorizontal()) {
            postRotateTranslationX += getStartMargin() - getEndMargin();
        }
        else {
          postRotateTranslationY += getStartMargin() - getEndMargin();
        }
        mMatrix.postTranslate(postRotateTranslationX, postRotateTranslationY);
        path.transform(mMatrix);

        return path;
    }

    private Path getTopArrowPath() {
        return getPath(180, widthOffset + (getDecoWidth() / 2), getDecoHeight() / 2, 0, 0);
    }

    private Path getEndArrowPath() {
        return getPath(270, 0, heightOffset, 0, (getDecoHeight() / 2) + (getDecoWidth() / 2));
    }

    private Path getBottomArrowPath() {
        return getPath(0, widthOffset + (getDecoWidth() / 2), getDecoHeight() / 2, 0, 0);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Path getStartArrowPath() {
        return getPath(90, getDecoWidth(), heightOffset, 0, (getDecoHeight() / 2) + (getDecoWidth() / 2));
    }
}
