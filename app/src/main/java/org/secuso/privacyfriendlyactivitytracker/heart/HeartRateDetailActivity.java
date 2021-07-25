package org.secuso.privacyfriendlyactivitytracker.heart;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 심박수상세화면
 */
public class HeartRateDetailActivity extends ToolbarActivity {
    TextView mTime;
    TextView mValue;
    HeartRateBar mHeartBar;
    ImageView mStatusImage;
    TextView mStatusName;
    TextView mNote;
    LinearLayout mNoteArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_heart_rate_detail);
        super.onCreate(savedInstanceState);

        mTime = findViewById(R.id.time);
        mValue = findViewById(R.id.heart_rate_value);
        mHeartBar = findViewById(R.id.heart_rate_bar);
        mStatusImage = findViewById(R.id.status_image);
        mStatusName = findViewById(R.id.status_name);
        mNoteArea = findViewById(R.id.note_area);
        mNote = findViewById(R.id.note);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int pulseValue = bundle.getInt(HeartRateMeasureActivity.MEASURE_RESULT);
            SpannableString val = new SpannableString(getResources().getString(R.string.with_bpm, pulseValue));
            val.setSpan(new RelativeSizeSpan(2.5f), 0, 3, 0);
            mValue.setText(val);

            // 날자 및 심박수표시
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd (EEE) aaa hh:mm", Locale.getDefault());
            mTime.setText(dateFormat.format(new Date(bundle.getLong(HeartRateMeasureActivity.MEASURE_TIME))));
            mHeartBar.setValue(pulseValue);

            // 상태표시
            int status = -1;
            status = bundle.getInt(HeartRateMeasureActivity.MEASURE_STATUS);
            mHeartBar.setAfterExercise(status == 2);
            if (status >= 0) {
                String[] statusNameArray = getResources().getStringArray(R.array.status_title_array);
                String[] statusImageNameArray = getResources().getStringArray(R.array.heart_rate_status_drawable_array);
                int id = getResources().getIdentifier(statusImageNameArray[status], "drawable", getPackageName());
                mStatusImage.setImageDrawable(ContextCompat.getDrawable(this, id));
                mStatusName.setText(statusNameArray[status]);
            }

            // 남긴말 표시
            String note = "";
            note = bundle.getString(HeartRateMeasureActivity.MEASURE_NOTE);
            assert note != null;
            if (!note.equals("")) {
                mNoteArea.setVisibility(View.VISIBLE);
                mNote.setText(note);
            } else {
                mNoteArea.setVisibility(View.GONE);
            }
        }
    }
}