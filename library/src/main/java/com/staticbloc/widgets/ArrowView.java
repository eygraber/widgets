package com.staticbloc.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
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

        int measuredWidth = gravity.isHorizontal() ? findSibling().getMeasuredWidth() : getDecoHeight();
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
                heightOffset = (siblingHeight / 2) - (getDecoWidth() / 2);
                break;
            case END:
                widthOffset = 0;
                heightOffset = siblingHeight - getDecoWidth();
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

    private Path getPath(Point a, Point b, Point c, Point d) {
        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);

        return path;
    }

    private Path getTopArrowPath() {
        int widthEnd = widthOffset + getDecoWidth();
        int heightEnd = heightOffset + getDecoHeight();

        Point a = new Point(widthOffset, heightEnd);
        Point b = new Point(((getDecoWidth() / 2) + widthOffset), heightOffset);
        Point c = new Point(widthEnd, heightEnd);
        Point d = new Point(widthOffset, heightEnd);

        return getPath(a, b, c, d);
    }

    private Path getEndArrowPath() {
        int widthEnd = widthOffset + getDecoHeight();
        int heightEnd = heightOffset + getDecoWidth();

        Point a = new Point(widthEnd, (getDecoWidth() / 2) + heightOffset);
        Point b = new Point(widthOffset, heightEnd);
        Point c = new Point(widthOffset, heightOffset);
        Point d = new Point(widthEnd, (getDecoWidth() / 2) + heightOffset);

        return getPath(a, b, c, d);
    }

    private Path getBottomArrowPath() {
        int widthEnd = widthOffset + getDecoWidth();
        int heightEnd = heightOffset + getDecoHeight();

        Point a = new Point(widthOffset, heightOffset);
        Point b = new Point(((getDecoWidth() / 2) + widthOffset), heightEnd);
        Point c = new Point(widthEnd, heightOffset);
        Point d = new Point(widthOffset, heightOffset);

        return getPath(a, b, c, d);
    }

    private Path getStartArrowPath() {
        int widthEnd = widthOffset + getDecoHeight();
        int heightEnd = heightOffset + getDecoWidth();

        Point a = new Point(widthOffset, (getDecoWidth() / 2) + heightOffset);
        Point b = new Point(widthEnd, heightEnd);
        Point c = new Point(widthEnd, heightOffset);
        Point d = new Point(widthOffset, (getDecoWidth() / 2) + heightOffset);

        return getPath(a, b, c, d);
    }
}
