package com.staticbloc.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 12/29/2014
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public final class BubbleView extends LinearLayout {
    private static final int DEFAULT_ARROW_MEASUREMENT = 50;
    private static final int DEFAULT_ARROW_GRAVITY = 1;
    private static final int DEFAULT_ARROW_LOCATION = 4;

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

    private View container;

    private ArrowView arrowView;

    private Paint arrowBackgroundPaint;

    public BubbleView(Context context) {
        super(context);
        init(null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.bubble_view, this, true);

        arrowBackgroundPaint = new Paint();

        int arrowWidth = DEFAULT_ARROW_MEASUREMENT;
        int arrowHeight = DEFAULT_ARROW_MEASUREMENT;

        int arrowStartMargin = 0;
        int arrowEndMargin = 0;

        ArrowGravity arrowGravity = ArrowGravity.TOP;
        ArrowLocation arrowLocation = ArrowLocation.CENTER;

        int backgroundColor = Color.WHITE;
        int arrowColor = Color.WHITE;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleView);

            ViewStub contentStub = (ViewStub) findViewById(R.id.content_stub);
            int contentResource = a.getResourceId(R.styleable.BubbleView_bv_content, -1);
            if(contentResource != -1) {
                contentStub.setLayoutResource(contentResource);
                container = contentStub.inflate();
            }
            else {
                container = contentStub;
            }

            backgroundColor = a.getColor(R.styleable.BubbleView_bv_background_color, -1);
            if (backgroundColor == -1) {
                backgroundColor = Color.WHITE;
            }
            container.setBackgroundColor(backgroundColor);

            arrowColor = a.getColor(R.styleable.BubbleView_bv_arrow_color, -1);
            if(arrowColor == -1) {
                arrowColor = backgroundColor;
            }

            arrowWidth = (int) a.getDimension(R.styleable.BubbleView_bv_arrow_width, DEFAULT_ARROW_MEASUREMENT);
            arrowHeight = (int) a.getDimension(R.styleable.BubbleView_bv_arrow_height, DEFAULT_ARROW_MEASUREMENT);

            arrowStartMargin = (int) a.getDimension(R.styleable.BubbleView_bv_arrow_margin_start, 0);
            arrowEndMargin = (int) a.getDimension(R.styleable.BubbleView_bv_arrow_margin_end, 0);

            arrowGravity = ArrowGravity.get(a.getInt(R.styleable.BubbleView_bv_arrow_gravity, DEFAULT_ARROW_GRAVITY));
            arrowLocation = ArrowLocation.get(a.getInt(R.styleable.BubbleView_bv_arrow_location, DEFAULT_ARROW_LOCATION));

            a.recycle();
        }

        arrowView = new ArrowView(getContext());
        arrowView.setColor(arrowColor);
        arrowView.setArrowDimensions(arrowWidth, arrowHeight);
        arrowView.setArrowMargins(arrowStartMargin, arrowEndMargin);
        setArrowGravity(arrowGravity);
        setArrowLocation(arrowLocation);

        setBackgroundColor(Color.TRANSPARENT);
    }

    public int getArrowWidth() {
        return arrowView.getArrowWidth();
    }

    public int getArrowHeight() {
        return arrowView.getArrowHeight();
    }

    public void setArrowDimensions(int width, int height) {
        arrowView.setArrowDimensions(width, height);
        arrowView.invalidate();
    }

    public int getArrowStartMargin() {
        return arrowView.getArrowStartMargin();
    }

    public int getArrowEndMargin() {
      return arrowView.getArrowEndMargin();
    }

    public void setArrowMargins(int arrowStartMargin, int arrowEndMargin) {
        arrowView.setArrowMargins(arrowStartMargin, arrowEndMargin);
        arrowView.invalidate();
    }

    public int getArrowColor() {
        return arrowView.getColor();
    }

    public void setArrowColor(int color) {
        arrowBackgroundPaint.setColor(color);
        arrowView.invalidate();
    }

    public ArrowGravity getArrowGravity() {
        return arrowView.getGravity();
    }

    public void setArrowGravity(ArrowGravity arrowGravity) {
        removeView(arrowView);

        switch(arrowGravity) {
            case TOP:
                setOrientation(VERTICAL);
                addView(arrowView, 0);
                break;
            case BOTTOM:
                setOrientation(VERTICAL);
                addView(arrowView, 1);
                break;
            case START:
                setOrientation(HORIZONTAL);
                addView(arrowView, 0);
                measure(1, 1);
                break;
            case END:
                setOrientation(HORIZONTAL);
                addView(arrowView, 1);
                measure(1, 1);
                break;
        }

        setLayoutParamsAfterSettingGravity(.9f, .1f);

        arrowView.setGravity(arrowGravity);
    }

    private void setLayoutParamsAfterSettingGravity(float containerWeight, float arrowWeight) {
        LayoutParams containerLp = (LayoutParams) container.getLayoutParams();
        LayoutParams arrowLp = (LayoutParams) arrowView.getLayoutParams();
        containerLp.weight = containerWeight;
        arrowLp.weight = arrowWeight;
        container.setLayoutParams(containerLp);
        arrowView.setLayoutParams(arrowLp);
    }

    public ArrowLocation getArrowLocation() {
        return arrowView.getLocation();
    }

    public void setArrowLocation(ArrowLocation arrowLocation) {
        arrowView.setLocation(arrowLocation);
    }

//  for testing purposes
//    @Override
//    public void draw(Canvas canvas) {
//        super.draw(canvas);
//
//        Paint red = new Paint();
//        red.setColor(Color.RED);
//
//        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), getMeasuredHeight() / 2, red);
//        canvas.drawLine(getMeasuredWidth() / 2, 0, getMeasuredWidth() / 2, getMeasuredHeight(), red);
//    }
}
