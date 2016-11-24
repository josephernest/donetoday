package com.als.donetoday.preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.als.donetoday.R;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
    }
}
