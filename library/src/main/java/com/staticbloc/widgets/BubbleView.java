package com.staticbloc.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;

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

    private LinearLayout container;
    private TextView titleView;
    private TextView textView;
    private Button confirmView;
    private OnClickListener confirmClickListener;

    private boolean removeOnConfirm;

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

        container = (LinearLayout) findViewById(R.id.help_bubble_container);
        titleView = (TextView) findViewById(R.id.help_bubble_title);
        textView = (TextView) findViewById(R.id.help_bubble_text);
        confirmView = (Button) findViewById(R.id.help_bubble_confirm);

        arrowBackgroundPaint = new Paint();

        int arrowWidth = DEFAULT_ARROW_MEASUREMENT;
        int arrowHeight = DEFAULT_ARROW_MEASUREMENT;

        ArrowGravity arrowGravity = ArrowGravity.TOP;
        ArrowLocation arrowLocation = ArrowLocation.CENTER;

        int backgroundColor = Color.WHITE;

        CharSequence title = null;
        int titleColor = DEFAULT_TEXT_COLOR;

        CharSequence text = null;
        int textColor = DEFAULT_TEXT_COLOR;

        int confirmTextColor = DEFAULT_TEXT_COLOR;
        CharSequence confirmText = null;
        boolean showConfirm = true;
        Drawable confirmBackground = null;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleView);

            backgroundColor = a.getColor(R.styleable.BubbleView_hbv_background_color, -1);
            if (backgroundColor == -1) {
                backgroundColor = Color.WHITE;
            }
            container.setBackgroundColor(backgroundColor);

            arrowWidth = (int) a.getDimension(R.styleable.BubbleView_hbv_arrow_width, DEFAULT_ARROW_MEASUREMENT);
            arrowHeight = (int) a.getDimension(R.styleable.BubbleView_hbv_arrow_height, DEFAULT_ARROW_MEASUREMENT);

            arrowGravity = ArrowGravity.get(a.getInt(R.styleable.BubbleView_hbv_arrow_gravity, DEFAULT_ARROW_GRAVITY));
            arrowLocation = ArrowLocation.get(a.getInt(R.styleable.BubbleView_hbv_arrow_location, DEFAULT_ARROW_LOCATION));

            title = a.getText(R.styleable.BubbleView_hbv_title);
            titleColor = a.getColor(R.styleable.BubbleView_hbv_title_color, DEFAULT_TEXT_COLOR);

            text = a.getText(R.styleable.BubbleView_hbv_text);
            textColor = a.getColor(R.styleable.BubbleView_hbv_text_color, DEFAULT_TEXT_COLOR);

            confirmText = a.getText(R.styleable.BubbleView_hbv_confirm_text);
            confirmTextColor = a.getColor(R.styleable.BubbleView_hbv_confirm_text_color, DEFAULT_TEXT_COLOR);
            confirmBackground = a.getDrawable(R.styleable.BubbleView_hbv_confirm_background);

            showConfirm = a.getBoolean(R.styleable.BubbleView_hbv_show_confirm, true);

            removeOnConfirm = a.getBoolean(R.styleable.BubbleView_hbv_remove_on_confirm, true);

            a.recycle();
        }

        setTitle(title);
        setTitleColor(titleColor);

        setText(text);
        setTextColor(textColor);

        setConfirmText(confirmText);
        setConfirmTextColor(confirmTextColor);
        showConfirmButton(showConfirm);
        setConfirmBackground(confirmBackground);

        arrowView = new ArrowView(getContext());
        arrowView.setColor(backgroundColor);
        arrowView.setArrowDimensions(arrowWidth, arrowHeight);
        setArrowGravity(arrowGravity);
        setArrowLocation(arrowLocation);

        setBackgroundColor(Color.TRANSPARENT);

        confirmView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(removeOnConfirm) {
                    ((ViewGroup) getParent()).removeView(BubbleView.this);
                }

                if(BubbleView.this.confirmClickListener != null) {
                    BubbleView.this.confirmClickListener.onClick(v);
                    BubbleView.this.confirmClickListener = null;
                }
            }
        });
    }

    public void setOnConfirmClickListener(final OnClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
    }

    public int getArrowWidth() {
        return arrowView.getArrowWidth();
    }

    public int getArrowHeight() {
        return arrowView.getArrowHeight();
    }

    public void setArrowDimensions(int width, int height) {
        arrowView.setArrowDimensions(width, height);
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

    public CharSequence getTitle() {
        return titleView.getText();
    }

    public void setTitle(int titleRes) {
        titleView.setText(titleRes);
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    public int getTitleColor() {
        return titleView.getCurrentTextColor();
    }

    public void setTitleColor(int color) {
        titleView.setTextColor(color);
    }

    public CharSequence getText() {
        return textView.getText();
    }

    public void setText(int textRes) {
        textView.setText(textRes);
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public int getTextColor() {
        return textView.getCurrentTextColor();
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public CharSequence getConfirmText() {
        return confirmView.getText();
    }

    public void setConfirmText(int confirmTextRes) {
        confirmView.setText(confirmTextRes);
    }

    public void setConfirmText(CharSequence confirmText) {
        confirmView.setText(confirmText);
    }

    public int getConfirmTextColor() {
        return confirmView.getCurrentTextColor();
    }

    public void setConfirmTextColor(int color) {
        confirmView.setTextColor(color);
    }

    public boolean isConfirmButtonShowing() {
        return confirmView.getVisibility() == View.VISIBLE;
    }

    public void showConfirmButton(boolean showConfirmView) {
        if(showConfirmView) {
            confirmView.setVisibility(View.VISIBLE);
        }
        else {
            confirmView.setVisibility(View.GONE);
        }
    }

    public Drawable getConfirmBackground() {
        return confirmView.getBackground();
    }

    @SuppressLint("NewApi")
    public void setConfirmBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            confirmView.setBackground(background);
        } else {
            confirmView.setBackgroundDrawable(background);
        }
    }

    public boolean isRemoveOnConfirm() {
        return removeOnConfirm;
    }

    public void setRemoveOnConfirm(boolean removeOnConfirm) {
        this.removeOnConfirm = removeOnConfirm;
    }
}
