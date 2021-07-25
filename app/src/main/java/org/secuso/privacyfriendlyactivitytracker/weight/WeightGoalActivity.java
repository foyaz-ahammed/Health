package org.secuso.privacyfriendlyactivitytracker.weight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.zjun.widget.RuleView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.persistence.MeasureDatabase;

/**
 * 몸무게목표변경화면
 */
public class WeightGoalActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView saveBtn;
    private TextView weightValue;

    boolean isNewSet = false; // 새로 설정여부
    String lastWeightValue; // 새 시작몸무게를 설정하기 위한 값

    MeasureDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_goal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = MeasureDatabase.getInstance(getApplicationContext());

        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();
        if (bundle != null) {
            isNewSet = true;
            lastWeightValue = bundle.getString("lastWeightValue");
        }

        weightValue = findViewById(R.id.weight_value);
        RuleView goalWeightRuler = findViewById(R.id.weight_ruler);
        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(this);

        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        float originWeightGoal = sharePref.getFloat(getString(R.string.pref_weight_goal), 0);
        if (originWeightGoal != 0) {
            weightValue.setText(String.valueOf(originWeightGoal));
            goalWeightRuler.setCurrentValue(originWeightGoal);
        } else {
            weightValue.setText(String.valueOf(60.0));
        }

        goalWeightRuler.setOnValueChangedListener(value -> weightValue.setText(String.valueOf(value)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == saveBtn) {
            SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharePref.edit();
            if (isNewSet)
                editor.putFloat(getString(R.string.pref_weight_start), Float.parseFloat(lastWeightValue));
            editor.putFloat(getString(R.string.pref_weight_goal), Float.parseFloat(weightValue.getText().toString()));
            editor.apply();

            finish();
        }
    }
}