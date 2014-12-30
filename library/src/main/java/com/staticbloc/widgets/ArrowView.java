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
    private int width;
    private int height;

    private int widthOffset = 0;
    private int heightOffset = 0;

    private Paint backgroundPaint;

    private BubbleView.ArrowGravity gravity = BubbleView.ArrowGravity.TOP;
    private BubbleView.ArrowLocation location = BubbleView.ArrowLocation.CENTER;

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

    public BubbleView.ArrowGravity getGravity() {
        return gravity;
    }

    public void setGravity(BubbleView.ArrowGravity gravity) {
        this.gravity = gravity;

        setLocation(location);
    }

    public void setLocation(BubbleView.ArrowLocation location) {
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

    public BubbleView.ArrowLocation getLocation() {
        return location;
    }

    private ViewGroup findSibling() {
        ViewGroup parent = (ViewGroup) getParent();

        if(parent != null) {
            ViewGroup sibling = null;
            for(int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if(child != parent && child instanceof ViewGroup) {
                    sibling = (ViewGroup) child;
                    break;
                }
            }

            if(sibling != null) {
                return sibling;
            }
        }

        return null;
    }

    private void calculateHorizontalOffsets() {
        ViewGroup sibling = findSibling();
        if(sibling == null) {
            return;
        }

        int siblingWidth = sibling.getMeasuredWidth();

        switch(location) {
            case START:
                widthOffset = 0;
                heightOffset = 0;
                break;
            case CENTER:
                widthOffset = (siblingWidth / 2) - (width / 2);
                heightOffset = 0;
                break;
            case END:
                widthOffset = siblingWidth - width;
                heightOffset = 0;
                break;
        }
    }

    private void calculateVerticalOffsets() {
        ViewGroup sibling = findSibling();
        if(sibling == null) {
            return;
        }

        int siblingHeight = sibling.getMeasuredHeight();

        switch(location) {
            case START:
                widthOffset = 0;
                heightOffset = 0;
                break;
            case CENTER:
                widthOffset = 0;
                heightOffset = (siblingHeight / 2) - (height / 2);
                break;
            case END:
                widthOffset = 0;
                heightOffset = siblingHeight - height;
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

        ViewGroup sibling = findSibling();
        if(sibling == null) {
            return;
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