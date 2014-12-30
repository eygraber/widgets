package com.staticbloc.widgets;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 12/30/2014
 * Time: 4:47 AM
 * To change this template use File | Settings | File Templates.
 */
/*package*/ final class ArrowView extends View {
    public static enum ArrowGravity {
        TOP, BOTTOM, START, END;

        public static ArrowGravity get(int value) {
            switch (value) {
                case 1:
                    return TOP;
                case 2:
                    return BOTTOM;
                case 4:
                    return START;
                default:
                    return END;
            }
        }

        public boolean isHorizontal() {
            return this == TOP || this == BOTTOM;
        }

        public boolean isVertical() {
            return this == START || this == END;
        }
    }

    public static enum ArrowLocation {
        START, END, CENTER;

        public static ArrowLocation get(int value) {
            switch (value) {
                case 1:
                    return START;
                case 2:
                    return END;
                default:
                    return CENTER;
            }
        }
    }

    private int width;
    private int height;

    private int widthOffset = 0;
    private int heightOffset = 0;

    private Paint backgroundPaint;

    private ArrowGravity gravity = ArrowGravity.TOP;
    private ArrowLocation location = ArrowLocation.CENTER;

    public ArrowView(Context context) {
        super(context);
        init();
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        backgroundPaint = new Paint();
    }

    public int getArrowWidth() {
        return width;
    }

    public int getArrowHeight() {
        return height;
    }

    public void setArrowDimensions(int width, int height) {
        this.width = width;
        this.height = height;

        invalidate();
    }

    public int getColor() {
        return backgroundPaint.getColor();
    }

    public void setColor(int color) {
        this.backgroundPaint.setColor(color);

        invalidate();
    }

    public ArrowGravity getGravity() {
        return gravity;
    }

    public void setGravity(ArrowGravity gravity) {
        this.gravity = gravity;

        setLocation(location);
    }

    public void setLocation(ArrowLocation location) {
        this.location = location;

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

        invalidate();
    }

    public ArrowLocation getLocation() {
        return location;
    }

    private ViewGroup findSibling() {
        ViewGroup parent = (ViewGroup) getParent();
        ViewGroup sibling = null;
        for(int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child != parent && child instanceof ViewGroup) {
                sibling = (ViewGroup) child;
                break;
            }
        }

        if(sibling == null) {
            sibling = parent;
        }

        return sibling;
    }

    private void calculateHorizontalOffsets() {
        int uncleWidth = findSibling().getMeasuredWidth();

        switch(location) {
            case START:
                widthOffset = 0;
                heightOffset = 0;
                break;
            case CENTER:
                widthOffset = (uncleWidth / 2) - (width / 2);
                heightOffset = 0;
                break;
            case END:
                widthOffset = uncleWidth - width;
                heightOffset = 0;
                break;
        }
    }

    private void calculateVerticalOffsets() {
        int uncleHeight = findSibling().getMeasuredHeight();

        switch(location) {
            case START:
                widthOffset = 0;
                heightOffset = 0;
                break;
            case CENTER:
                widthOffset = 0;
                heightOffset = (uncleHeight / 2) - (height / 2);
                break;
            case END:
                widthOffset = 0;
                heightOffset = uncleHeight - height;
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

        int measuredWidth = gravity.isHorizontal() ? findSibling().getMeasuredWidth() : width;
        int measuredHeight = gravity.isVertical() ? findSibling().getMeasuredHeight() : height;

        setMeasuredDimension(measuredWidth, measuredHeight);

        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawArrow(canvas);
    }

    private void drawArrow(Canvas canvas) {
        switch(gravity) {
            case TOP:
                canvas.drawPath(getTopArrowPath(), backgroundPaint);
                break;
            case END:
                canvas.drawPath(getEndArrowPath(), backgroundPaint);
                break;
            case BOTTOM:
                canvas.drawPath(getBottomArrowPath(), backgroundPaint);
                break;
            case START:
                canvas.drawPath(getStartArrowPath(), backgroundPaint);
                break;
        }
    }

    private Path getPath(float rotation) {
        int widthEnd = widthOffset + width;
        int heightEnd = heightOffset + height;

        Point a = new Point(widthOffset, heightOffset);
        Point b = new Point(((width / 2) + widthOffset), heightEnd);
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
        mMatrix.postRotate(rotation,
                (bounds.right + bounds.left) / 2,
                (bounds.bottom + bounds.top) / 2);
        path.transform(mMatrix);

        return path;
    }

    private Path getTopArrowPath() {
        return getPath(180);
    }

    private Path getEndArrowPath() {
        return getPath(270);
    }

    private Path getBottomArrowPath() {
        return getPath(0);
    }

    private Path getStartArrowPath() {
        return getPath(90);
    }
}
