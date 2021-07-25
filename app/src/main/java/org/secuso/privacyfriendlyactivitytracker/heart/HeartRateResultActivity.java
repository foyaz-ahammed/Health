package org.secuso.privacyfriendlyactivitytracker.heart;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.models.HeartRateInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRateRepository;
import org.secuso.privacyfriendlyactivitytracker.persistence.MeasureDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 심박수결과화면
 */
public class HeartRateResultActivity extends ToolbarActivity implements View.OnClickListener{
    TextView mHeartValue;
    HeartRateBar mHeartBar;
    RecyclerView mStatusList;
    EditText mNotes;
    TextView mCancel;
    TextView mSave;
    TextView mRestTip;

    List<Drawable> statusImgArray = new ArrayList<>();
    String[] statusTitleArray;

    int pulseValue;
    long measureTime;

    StatusAdapter statusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_heart_rate_result);
        super.onCreate(savedInstanceState);

        mHeartValue = findViewById(R.id.heart_rate_value);
        mHeartBar = findViewById(R.id.heart_rate_bar);
        mStatusList = findViewById(R.id.status_list);
        mNotes = findViewById(R.id.notes);
        mCancel = findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        mSave = findViewById(R.id.save);
        mSave.setOnClickListener(this);
        mRestTip = findViewById(R.id.rest_tip);

        initStatusArray(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        mStatusList.setLayoutManager(layoutManager);
        statusAdapter = new StatusAdapter(statusImgArray, statusTitleArray, this);
        mStatusList.setAdapter(statusAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            pulseValue = bundle.getInt(HeartRateMeasureActivity.MEASURE_RESULT);
            measureTime = bundle.getLong(HeartRateMeasureActivity.MEASURE_TIME);
            mHeartBar.setValue(pulseValue);
            SpannableString val = new SpannableString(getResources().getString(R.string.with_bpm, pulseValue));
            val.setSpan(new RelativeSizeSpan(2.5f), 0, 3, 0);
            mHeartValue.setText(val);
        }
    }

    /**
     * 상태 화상 및 이름목록 얻기
     * @param context The application context
     */
    private void initStatusArray(Context context) {
        statusTitleArray = context.getResources().getStringArray(R.array.status_title_array);

        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_general_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_resting_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_afterexercise_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_beforeexercise_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_sad_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_excited_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_tired_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_inlove_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_surprise_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_angry_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_fearful_mtrl));
        statusImgArray.add(ContextCompat.getDrawable(context, R.drawable.tracker_hr_result_ic_unwell_mtrl));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel) {
            finish();
        } else if (v.getId() == R.id.save) {
            HeartRateRepository repository = new HeartRateRepository(getApplication());
            Date date = new Date(measureTime);
            SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            repository.insertOrUpdateHeartRate(new HeartRateInfo(pulseValue, measureTime, simpleMonthFormat.format(date),
                    simpleDayFormat.format(date), mNotes.getText().toString(), statusAdapter.getSelectPosition()));
            finish();
        }
    }
}