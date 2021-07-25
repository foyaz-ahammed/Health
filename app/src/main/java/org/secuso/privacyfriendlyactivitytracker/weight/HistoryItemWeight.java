package org.secuso.privacyfriendlyactivitytracker.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemView;
import org.secuso.privacyfriendlyactivitytracker.weight.AddRecordActivity;
import org.secuso.privacyfriendlyactivitytracker.weight.HistoryActivity;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.DeleteConfirmDialog;

/**
 * 개별적인 기록에 대한 class
 */
public class HistoryItemWeight extends HistoryItemView implements View.OnClickListener, View.OnLongClickListener {
    TextView mWeightValue;
    TextView mFatRateValue;
    TextView mMeasureTime;
    LinearLayout mFatRateArea;
    CheckBox mCheckBox;
    ImageView mArrowView;
    View mDivider;

    HistoryActivity mHistoryActivity;
    WeightInfo weightInfo;

    boolean isDeletable = false; // 삭제가능상태
    boolean isChecked = false; // 선택가능상태

    public HistoryItemWeight(Context context) {
        this(context, null);
    }

    public HistoryItemWeight(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryItemWeight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDivider = findViewById(R.id.divider);
        mWeightValue = findViewById(R.id.weight_value);
        mFatRateValue = findViewById(R.id.fat_rate_value);
        mMeasureTime = findViewById(R.id.measure_time);
        mFatRateArea = findViewById(R.id.fat_rate_area);
        mCheckBox = findViewById(R.id.checkbox);
        mArrowView = findViewById(R.id.arrow);

        setOnClickListener(this);
        setOnLongClickListener(this);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;
                mHistoryActivity.checkList.put(weightInfo.getId(), b);
                mHistoryActivity.mSelectAllText.setText(mHistoryActivity.getCheckedCount() == mHistoryActivity.checkList.size() ?
                        getResources().getString(R.string.deselect_all) : getResources().getString(R.string.select_all));
                if (mHistoryActivity.canDeletable)
                    mHistoryActivity.changeTitle();
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
        if (!(info instanceof WeightInfo)) {
            return;
        }
        mHistoryActivity = (HistoryActivity) toolbarActivity;

        weightInfo = (WeightInfo) info;
        mWeightValue.setText(getContext().getResources().getString(R.string.with_kilogram,
                Float.parseFloat(weightInfo.getWeightValue())));
        if (weightInfo.getFatRateValue() != null) {
            mFatRateArea.setVisibility(View.VISIBLE);
            mFatRateValue.setText(getContext().getResources().getString(R.string.with_percent,
                    Float.parseFloat(weightInfo.getFatRateValue())));
        } else {
            mFatRateArea.setVisibility(View.GONE);
        }
        mMeasureTime.setText(Utils.getTimeString(getContext(), weightInfo.getMeasureDateTime().getHourOfDay(),
                weightInfo.getMeasureDateTime().getMinuteOfHour()));

        mCheckBox.setVisibility(isDeletable ? View.VISIBLE : View.GONE);
        mArrowView.setVisibility(isDeletable ? View.GONE : View.VISIBLE);
        setLongClickable(!isDeletable);
        if (mCheckBox.getVisibility() == View.VISIBLE)
            mCheckBox.setChecked(mHistoryActivity.checkList.get(weightInfo.getId()));
        this.isDeletable = isDeletable;
    }

    @Override
    public void onClick(View view) {
        if (view == this) {
            if (isDeletable) {
                isChecked = !isChecked;
                mCheckBox.setChecked(isChecked);
            } else {
                Intent intent = new Intent(getContext(), AddRecordActivity.class);
                intent.putExtra("status", "update");
                intent.putExtra("_id", weightInfo.getId());
                intent.putExtra("millisec", weightInfo.getMeasureDateTime().getMillis());
                intent.putExtra("weightValue", weightInfo.getWeightValue());
                intent.putExtra("fatRateValue", weightInfo.getFatRateValue());
                mHistoryActivity.startActivity(intent);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        mHistoryActivity.selectedItemList.clear();
        mHistoryActivity.selectedItemList.add(weightInfo.getId());
        DeleteConfirmDialog confirmDialog = new DeleteConfirmDialog(mHistoryActivity);
        confirmDialog.setOnButtonClickListener(mHistoryActivity);
        confirmDialog.show();
        return true;
    }

    /**
     * 기록별 divider 상태변경함수
     * @param position 기록위치
     */
    @Override
    public void setDividerVisibility(int position) {
        mDivider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
    }
}

