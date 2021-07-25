package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.persistence.Symptom;
import org.secuso.privacyfriendlyactivitytracker.viewModel.SymptomViewModel;

import java.util.Objects;

import me.rishabhkhanna.customtogglebutton.CustomToggleButton;

/**
 * 신체적증상화면
 */
public class SymptomsActivity extends ToolbarActivity implements View.OnClickListener {
    LinearLayout mFlowArea;
    LinearLayout mPainArea;

    GridLayout mFlows;
    CustomToggleButton mFlowLight;
    CustomToggleButton mFlowAverage;
    CustomToggleButton mFlowHeavy;

    GridLayout mPains;
    CustomToggleButton mPainLight;
    CustomToggleButton mPainAverage;
    CustomToggleButton mPainSevere;

    GridLayout mConditions;

    //Intensity 상태를 변경시킬수 있는지 판별
    boolean isIntensityEnabled;
    SymptomViewModel symptomViewModel;
    int date, symptomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_symptoms);
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        mPainArea = findViewById(R.id.pain_area);
        mFlowArea = findViewById(R.id.flow_area);

        mFlows = findViewById(R.id.flows);
        mFlowLight = findViewById(R.id.flow_light);
        mFlowAverage = findViewById(R.id.flow_average);
        mFlowHeavy = findViewById(R.id.flow_heavy);

        mPains = findViewById(R.id.pains);
        mPainLight= findViewById(R.id.pain_light);
        mPainAverage = findViewById(R.id.pain_average);
        mPainSevere = findViewById(R.id.pain_severe);

        mConditions = findViewById(R.id.conditions);

        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();

        if (bundle != null) {
            if (bundle.getBoolean("enabledIntensity")) {
                isIntensityEnabled = true;
                mPainArea.setVisibility(View.VISIBLE);
                mFlowArea.setVisibility(View.VISIBLE);
            } else {
                isIntensityEnabled = false;
                mPainArea.setVisibility(View.GONE);
                mFlowArea.setVisibility(View.GONE);
            }
            date = bundle.getInt("selectedDate");
        }

        symptomViewModel = new ViewModelProvider(this).get(SymptomViewModel.class);
        symptomViewModel.setDate(date);
        symptomViewModel.dayData.observe(this, new Observer<Symptom>() {
            @Override
            public void onChanged(Symptom symptom) {
                if (symptom != null) {
                    symptomId = symptom.getId();
                    if (isIntensityEnabled) {
                        if (symptom.getFlowIntensity() >= 0)
                            ((CustomToggleButton) mFlows.getChildAt(symptom.getFlowIntensity())).setChecked(true);
                        if (symptom.getPainIntensity() >= 0)
                            ((CustomToggleButton) mPains.getChildAt(symptom.getPainIntensity())).setChecked(true);
                    }

                    if (!symptom.getSymptoms().equals("")) {
                        String[] symptomArray = symptom.getSymptoms().split(" ");
                        for (String s : symptomArray) {
                            ((CustomToggleButton) mConditions.getChildAt(Integer.parseInt(s))).setChecked(true);
                        }
                    }
                }
            }
        });

    }

    /**
     * Menu 생성 함수
     * @param menu 생성될 menu
     * @return true 이면 menu 보여주기, false 이면 보여주지 않기
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    /**
     * Menu 초기화함수
     * @param menu 생성된 Menu
     * @return true 이면 menu 보여주기, false 이면 보여주지 않기
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem editItem = menu.findItem(R.id.menu_edit);
        editItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Menu 항목을 눌렀을때 처리를 진행하는 함수
     * @param item 눌러진 항목
     * @return 처리가 진행되였으면 true, 아니면 false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int flowIntensity = -1, painIntensity = -1;
        StringBuilder symptoms = new StringBuilder();
        boolean isFirst = false;
        if (item.getItemId() == R.id.menu_save) {
            if (isIntensityEnabled) {
                for (int i = 0; i < mFlows.getChildCount(); i ++) {
                    if (((CustomToggleButton) mFlows.getChildAt(i)).isChecked())
                        flowIntensity = i;
                }
                for (int i = 0; i < mPains.getChildCount(); i ++) {
                    if (((CustomToggleButton) mPains.getChildAt(i)).isChecked())
                        painIntensity = i;
                }
            }
            for (int i = 0; i < mConditions.getChildCount(); i ++) {
                if (((CustomToggleButton) mConditions.getChildAt(i)).isChecked()) {
                    if (!isFirst) {
                        symptoms.append(i);
                        isFirst = true;
                    } else {
                        symptoms.append(" ").append(i);
                    }
                }
            }
            if (symptomId > 0) {
                if (flowIntensity == -1 && painIntensity == -1 && symptoms.toString().equals(""))
                    symptomViewModel.delete(symptomId);
                else
                    symptomViewModel.insertOrUpdate(new Symptom(symptomId, date, symptoms.toString(), flowIntensity, painIntensity));
            } else {
                if (flowIntensity > -1 || painIntensity > -1 || !(symptoms.toString().equals("")))
                    symptomViewModel.insertOrUpdate(new Symptom(0, date, symptoms.toString(), flowIntensity, painIntensity));
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == mFlowLight) {
            mFlowAverage.setChecked(false);
            mFlowHeavy.setChecked(false);
        } else if (view == mFlowAverage) {
            mFlowLight.setChecked(false);
            mFlowHeavy.setChecked(false);
        } else if (view == mFlowHeavy) {
            mFlowLight.setChecked(false);
            mFlowAverage.setChecked(false);
        } else if (view == mPainLight) {
            mPainAverage.setChecked(false);
            mPainSevere.setChecked(false);
        } else if (view == mPainAverage) {
            mPainLight.setChecked(false);
            mPainSevere.setChecked(false);
        } else if (view == mPainSevere) {
            mPainLight.setChecked(false);
            mPainAverage.setChecked(false);
        }
    }
}