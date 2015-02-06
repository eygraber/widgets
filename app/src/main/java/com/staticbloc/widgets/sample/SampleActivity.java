package com.staticbloc.widgets.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

public class SampleActivity extends Activity {
  ViewGroup v;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onResume() {
      super.onResume();
      v = (ViewGroup) findViewById(R.id.test);
      v.postDelayed(new Runnable() {
        @Override
        public void run() {
          Log.i("", "");
        }
      }, 2000);
    }
}
