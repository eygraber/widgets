package com.staticbloc.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 12/29/2014
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public final class BubbleView extends LinearLayout {
    private static final String DEFAULT_DECORATION_CLASS = ArrowView.class.getName();
    private static final int DEFAULT_DECORATION_MEASUREMENT = 50;
    private static final int DEFAULT_DECORATION_MARGIN = 0;
    private static final int DEFAULT_DECORATION_GRAVITY = 1;
    private static final int DEFAULT_DECORATION_LOCATION = 4;
    private static final int INVALID_CONTENT_RESOURCE = -1;

    private FrameLayout container;

    private Decoration decoView;

    public BubbleView(Context context) throws ClassNotFoundException {
        super(context);
        init(null);
    }

    public BubbleView(Context context, AttributeSet attrs) throws ClassNotFoundException {
        super(context, attrs);
        init(attrs);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) throws ClassNotFoundException {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) throws ClassNotFoundException {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.bubble_view, this, true);

        container = (FrameLayout) findViewById(R.id.bv_content);

        String decorationViewFQName = null;

        int contentResource = INVALID_CONTENT_RESOURCE;

        int decorationWidth = DEFAULT_DECORATION_MEASUREMENT;
        int decorationHeight = DEFAULT_DECORATION_MEASUREMENT ;

        int decorationStartMargin = DEFAULT_DECORATION_MARGIN;
        int decorationEndMargin = DEFAULT_DECORATION_MARGIN;

        Decoration.DecorationGravity decorationGravity = Decoration.DecorationGravity.TOP;
        Decoration.DecorationLocation decorationLocation = Decoration.DecorationLocation.CENTER;

        int backgroundColor = Color.WHITE;
        int decorationColor = Color.WHITE;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleView);

            contentResource = a.getResourceId(R.styleable.BubbleView_bv_content, INVALID_CONTENT_RESOURCE);

            backgroundColor = a.getColor(R.styleable.BubbleView_bv_background_color, Color.WHITE);

            decorationViewFQName = a.getString(R.styleable.BubbleView_bv_decoration);
            if(TextUtils.isEmpty(decorationViewFQName)) {
                decorationViewFQName = DEFAULT_DECORATION_CLASS;
            }

            decorationColor = a.getColor(R.styleable.BubbleView_bv_decoration_color, backgroundColor);

            decorationWidth = (int) a.getDimension(R.styleable.BubbleView_bv_decoration_width, DEFAULT_DECORATION_MEASUREMENT);
            decorationHeight = (int) a.getDimension(R.styleable.BubbleView_bv_decoration_height, DEFAULT_DECORATION_MEASUREMENT);

            decorationStartMargin = (int) a.getDimension(R.styleable.BubbleView_bv_decoration_margin_start, DEFAULT_DECORATION_MARGIN);
            decorationEndMargin = (int) a.getDimension(R.styleable.BubbleView_bv_decoration_margin_end, DEFAULT_DECORATION_MARGIN);

            decorationGravity = Decoration.DecorationGravity.get(a.getInt(R.styleable.BubbleView_bv_decoration_gravity, DEFAULT_DECORATION_GRAVITY));
            decorationLocation = Decoration.DecorationLocation.get(a.getInt(R.styleable.BubbleView_bv_decoration_location, DEFAULT_DECORATION_LOCATION));

            a.recycle();
        }

        if(contentResource != INVALID_CONTENT_RESOURCE) {
            inflater.inflate(contentResource, container, true);
        }

        container.setBackgroundColor(backgroundColor);

        Class<?> decoClass = Class.forName(decorationViewFQName);
        try {
            Constructor<?> constructor = decoClass.getDeclaredConstructor(Context.class);
            decoView = (Decoration) constructor.newInstance(getContext());
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException("There was an error creating the Decoration", e);
        }

        decoView.setDecorationColor(decorationColor);
        decoView.setDecoWidth(decorationWidth);
        decoView.setDecoHeight(decorationHeight);
        decoView.setStartMargin(decorationStartMargin);
        decoView.setEndMargin(decorationEndMargin);
        setDecorationGravity(decorationGravity);
        setDecorationLocation(decorationLocation);

        setBackgroundColor(Color.TRANSPARENT);
    }

    public int getDecorationWidth() {
        return decoView.getDecoWidth();
    }

    public void setDecorationWidth(int decorationWidth) {
        decoView.setDecoWidth(decorationWidth);
    }

    public int getDecorationHeight() {
        return decoView.getDecoHeight();
    }

    public void setDecorationHeight(int decorationHeight) {
        decoView.setDecoHeight(decorationHeight);
    }

    public int getDecorationStartMargin() {
        return decoView.getStartMargin();
    }

    public void setDecorationStartMargin(int startMargin) {
        decoView.setStartMargin(startMargin);
    }

    public int getDecorationEndMargin() {
      return decoView.getEndMargin();
    }

    public void setDecorationEndMargin(int endMargin) {
        decoView.setEndMargin(endMargin);
    }

    public int getDecorationColor() {
        return decoView.getDecorationColor();
    }

    public void setDecorationColor(int color) {
        decoView.setDecorationColor(color);
    }

    public Decoration.DecorationGravity getDecorationGravity() {
        return decoView.getGravity();
    }

    public void setDecorationGravity(Decoration.DecorationGravity decorationGravity) {
        removeView(decoView);

        switch(decorationGravity) {
            case TOP:
                setOrientation(VERTICAL);
                addView(decoView, 0);
                break;
            case BOTTOM:
                setOrientation(VERTICAL);
                addView(decoView, 1);
                break;
            case START:
                setOrientation(HORIZONTAL);
                addView(decoView, 0);
                measure(1, 1);
                break;
            case END:
                setOrientation(HORIZONTAL);
                addView(decoView, 1);
                measure(1, 1);
                break;
        }

        setLayoutParamsAfterSettingGravity(.9f, .1f);

        decoView.setGravity(decorationGravity);
    }

    private void setLayoutParamsAfterSettingGravity(float containerWeight, float decoWeight) {
        LayoutParams containerLp = (LayoutParams) container.getLayoutParams();
        LayoutParams decoLp = (LayoutParams) decoView.getLayoutParams();
        containerLp.weight = containerWeight;
        decoLp.weight = decoWeight;
        container.setLayoutParams(containerLp);
        decoView.setLayoutParams(decoLp);
    }

    public Decoration.DecorationLocation getDecorationLocation() {
        return decoView.getLocation();
    }

    public void setDecorationLocation(Decoration.DecorationLocation decorationLocation) {
        decoView.setLocation(decorationLocation);
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

    public abstract static class Decoration extends View {
        public static enum DecorationGravity {
            TOP, BOTTOM, START, END;

            public static DecorationGravity get(int value) {
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

        public static enum DecorationLocation {
            START, END, CENTER;

            public static DecorationLocation get(int value) {
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

        private int startMargin = 0;
        private int endMargin = 0;

        private int color;
        private Paint paint;

        private DecorationGravity gravity = DecorationGravity.TOP;
        private DecorationLocation location = DecorationLocation.CENTER;

        protected Decoration(Context context) {
            super(context);
        }

        protected final int getDecoWidth() {
            return width;
        }

        protected final void setDecoWidth(int width) {
            this.width = width;
            invalidate();
        }

        protected final int getDecoHeight() {
            return height;
        }

        protected final void setDecoHeight(int height) {
            this.height = height;
            invalidate();
        }

        protected final int getStartMargin() {
            return startMargin;
        }

        protected final void setStartMargin(int startMargin) {
            this.startMargin = startMargin;
            invalidate();
        }

        protected final int getEndMargin() {
            return endMargin;
        }

        protected final void setEndMargin(int endMargin) {
            this.endMargin = endMargin;
            invalidate();
        }

        protected final int getDecorationColor() {
            return color;
        }

        protected final void setDecorationColor(int color) {
            this.color = color;
            if(paint == null) {
                paint = new Paint();
            }

            paint.setColor(color);
            invalidate();
        }

        protected final Paint getPaint() {
            return paint;
        }

        protected final DecorationGravity getGravity() {
            return gravity;
        }

        protected final void setGravity(DecorationGravity gravity) {
            this.gravity = gravity;

            onGravityChanged();

            invalidate();
        }

        protected final DecorationLocation getLocation() {
            return location;
        }

        protected final void setLocation(DecorationLocation location) {
            this.location = location;

            onLocationChanged();

            invalidate();
        }

        protected abstract void onGravityChanged();
        protected abstract void onLocationChanged();
        protected abstract void drawDecoration(Canvas canvas);

        @Override
        public final void draw(Canvas canvas) {
            super.draw(canvas);

            drawDecoration(canvas);
        }


    }
}
