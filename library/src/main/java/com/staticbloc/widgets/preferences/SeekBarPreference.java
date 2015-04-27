package com.staticbloc.widgets.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.staticbloc.widgets.R;

/**
 * Created with IntelliJ IDEA.
 * User: eygraber
 * Date: 4/26/2015
 * Time: 8:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
  private static final int MAX_DEFAULT = 100;

  private SeekBar seekBar;
  private SeekBar.OnSeekBarChangeListener listener;

  private TextView valueRepresentationView;

  private int maxValue = MAX_DEFAULT;

  private int currentValue;
  private String valueRepresentation;

  private int paddingLeft;
  private int paddingTop;
  private int paddingRight;
  private int paddingBottom;

  public SeekBarPreference(Context context) {
    super(context);
    init(context, null, 0, 0);
  }

  public SeekBarPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0, 0);
  }

  public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    if(attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyleAttr, defStyleRes);
      try {
        maxValue = a.getInt(R.styleable.SeekBarPreference_android_max, MAX_DEFAULT);
        currentValue = a.getInt(R.styleable.SeekBarPreference_android_defaultValue, maxValue / 2);

        int padding = (int) a.getDimension(R.styleable.SeekBarPreference_android_padding, 0);
        paddingLeft = (int) a.getDimension(R.styleable.SeekBarPreference_android_paddingLeft, padding);
        paddingLeft = (int) a.getDimension(R.styleable.SeekBarPreference_android_paddingStart, paddingLeft);
        paddingTop = (int) a.getDimension(R.styleable.SeekBarPreference_android_paddingTop, padding);
        paddingRight = (int) a.getDimension(R.styleable.SeekBarPreference_android_paddingRight, padding);
        paddingRight = (int) a.getDimension(R.styleable.SeekBarPreference_android_paddingEnd, paddingRight);
        paddingBottom = (int) a.getDimension(R.styleable.SeekBarPreference_android_paddingBottom, padding);
      }
      finally {
        a.recycle();
      }
    }
    else {
      maxValue = MAX_DEFAULT;
      currentValue = maxValue / 2;
    }
  }

  @Override
  protected View onCreateView(ViewGroup parent) {
    return LayoutInflater.from(parent.getContext()).inflate(R.layout.seek_bar_preference, parent, false);
  }

  @Override
  public void onBindView(View view) {
    super.onBindView(view);

    seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
    seekBar.setMax(maxValue);
    seekBar.setProgress(currentValue);
    seekBar.setOnSeekBarChangeListener(this);

    seekBar.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

    valueRepresentationView = (TextView) view.findViewById(R.id.seek_bar_value_representation);
    valueRepresentationView.setText(valueRepresentation);

    ((TextView) view.findViewById(android.R.id.title)).setText(getTitle());
    ((TextView) view.findViewById(android.R.id.summary)).setText(getSummary());
  }

  public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
    this.listener = listener;
  }

  public void setValueRepresentation(String valueRepresentation) {
    this.valueRepresentation = valueRepresentation;
    if(valueRepresentationView != null) {
      valueRepresentationView.setText(valueRepresentation);
    }
  }

  public int getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(int currentValue) {
    this.currentValue = currentValue;
    if(seekBar != null) {
      seekBar.setProgress(currentValue);
    }
  }

  public int getMax() {
    return maxValue;
  }

  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
    if(seekBar != null) {
      seekBar.setMax(maxValue);
    }
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    currentValue = progress;
    if(listener != null) {
      listener.onProgressChanged(seekBar, progress, fromUser);
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    if(listener != null) {
      listener.onStartTrackingTouch(seekBar);
    }
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar){
    notifyChanged();
    persistInt(seekBar.getProgress());
    if(listener != null) {
      listener.onStopTrackingTouch(seekBar);
    }
  }


  @Override
  protected Object onGetDefaultValue(TypedArray ta, int index){
    return ta.getInt(index, ta.getInt(index, currentValue));
  }

  @Override
  protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
    if(restorePersistedValue) {
      currentValue = getSharedPreferences().getInt(getKey(), currentValue);
    }
    else {
      if(defaultValue instanceof Integer) {
        currentValue = (int) defaultValue;
      }
    }

    if(shouldPersist()) {
      persistInt(currentValue);
    }
  }
}
