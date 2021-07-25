package org.secuso.privacyfriendlyactivitytracker.blood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.blood.AddBloodPressureActivity;
import org.secuso.privacyfriendlyactivitytracker.blood.BloodPressureActivity;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemView;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.DeleteConfirmDialog;

/**
 * 개별적인 혈압자료를 담고있는 클라스
 */
public class HistoryItemBloodPressure extends HistoryItemView implements View.OnClickListener, View.OnLongClickListener {
    TextView mValue;
    TextView mDatetime;
    CheckBox mCheckBox;
    ImageView mArrowView;

    BloodPressureActivity mBloodPressureActivity;
    BloodPressureInfo bloodPressureInfo;

    boolean isDeletable = false; // 삭제가능상태판별
    boolean isChecked = false; // 선택가능상태판별

    public HistoryItemBloodPressure(Context context) {
        this(context, null);
    }

    public HistoryItemBloodPressure(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryItemBloodPressure(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mValue = findViewById(R.id.blood_pressure_value);
        mDatetime = findViewById(R.id.datetime);
        mCheckBox = findViewById(R.id.checkbox);
        mArrowView = findViewById(R.id.arrow);

        setOnClickListener(this);
        setOnLongClickListener(this);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;
                mBloodPressureActivity.checkList.put(bloodPressureInfo.getId(), b);
                mBloodPressureActivity.mSelectAllText.setText(mBloodPressureActivity.getCheckedCount() == mBloodPressureActivity.checkList.size() ?
                        getResources().getString(R.string.deselect_all) : getResources().getString(R.string.select_all));
                if (mBloodPressureActivity.canDeletable)
                    mBloodPressureActivity.changeTitle();
            }
        });
    }

    /**
     * 개별적인 기록상태변경을 위한 함수
     * @param info 새로받은 기록에 대한 object
     * @param toolbarActivity 기록을 담고 있는 activity
     * @param isDeletable 삭제상태
     */
    @SuppressLint("StringFormatInvalid")
    @Override
    public void applyFromItemInfo(HistoryItemContainer.HistoryItemInfo info, ToolbarActivity toolbarActivity, boolean isDeletable) {
        if (!(info instanceof BloodPressureInfo)) {
            return;
        }
        mBloodPressureActivity = (BloodPressureActivity) toolbarActivity;

        bloodPressureInfo = (BloodPressureInfo) info;
        mValue.setText(getContext().getString(R.string.blood_pressure_format1,
                bloodPressureInfo.getSystolicValue(), bloodPressureInfo.getDiastolicValue()));
        mDatetime.setText(getContext().getString(R.string.datetime_format, bloodPressureInfo.getMeasureDateTime().getYear(),
                bloodPressureInfo.getMeasureDateTime().getMonthOfYear(), bloodPressureInfo.getMeasureDateTime().getDayOfMonth(),
                Utils.get12Hour(bloodPressureInfo.getMeasureDateTime().getHourOfDay()), bloodPressureInfo.getMeasureDateTime().getMinuteOfHour(),
                bloodPressureInfo.getMeasureDateTime().getHourOfDay() < 12 ? getContext().getString(R.string.lany_am_label) : getContext().getString(R.string.lany_pm_label)));
        mCheckBox.setVisibility(isDeletable ? View.VISIBLE : View.GONE);
        mArrowView.setVisibility(isDeletable ? View.GONE : View.VISIBLE);
        setLongClickable(!isDeletable);
        mCheckBox.setChecked(mBloodPressureActivity.checkList.get(bloodPressureInfo.getId()));
        this.isDeletable = isDeletable;
    }

    @Override
    public void setDividerVisibility(int position) {

    }

    /**
     * 개별적인 기록을 눌렀을때의 처리
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == this) {
            if (isDeletable) {
                isChecked = !isChecked;
                mCheckBox.setChecked(isChecked);
            } else {
                Intent intent = new Intent(getContext(), AddBloodPressureActivity.class);
                intent.putExtra("status", "update");
                intent.putExtra("_id", bloodPressureInfo.getId());
                intent.putExtra("millisec", bloodPressureInfo.getMeasureDateTime().getMillis());
                intent.putExtra("systolicValue", bloodPressureInfo.getSystolicValue());
                intent.putExtra("diastolicValue", bloodPressureInfo.getDiastolicValue());
                intent.putExtra("pulseValue", bloodPressureInfo.getPulseValue());
                getContext().startActivity(intent);
            }
        }
    }

    /**
     * 개별적인 기록을 길게 눌렀을때의 처리
     * @param view
     * @return
     */
    @Override
    public boolean onLongClick(View view) {
        mBloodPressureActivity.selectedItemList.clear();
        mBloodPressureActivity.selectedItemList.add(bloodPressureInfo.getId());
        DeleteConfirmDialog confirmDialog = new DeleteConfirmDialog(getContext());
        confirmDialog.setOnButtonClickListener(mBloodPressureActivity);
        confirmDialog.show();
        return true;
    }
}
