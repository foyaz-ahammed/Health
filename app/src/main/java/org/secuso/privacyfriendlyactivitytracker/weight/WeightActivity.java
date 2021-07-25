package org.secuso.privacyfriendlyactivitytracker.weight;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.persistence.Weight;
import org.secuso.privacyfriendlyactivitytracker.utils.SetNewGoalDialog;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WeightViewModel;

/**
 * 몸무게화면
 */
public class WeightActivity extends ToolbarActivity implements View.OnClickListener {
    WeightViewModel mWeightViewModel;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_weight);
        super.onCreate(savedInstanceState);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_area, new WeightFragment(), "WeightFragment");
        fragmentTransaction.commit();

        mContext = this;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mWeightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);
        mWeightViewModel.getLatestWeightData().observe(this, new Observer<Weight>() {
            @Override
            public void onChanged(Weight weight) {
                float weightStart = sharedPref.getFloat(getString(R.string.pref_weight_start), 0);
                float weightGoal = sharedPref.getFloat(getString(R.string.pref_weight_goal), 0);
                boolean isNewWeightAdded = sharedPref.getBoolean(getString(R.string.pref_is_new_weight_added), false);
                if (weight != null && weightStart != 0 && weightGoal != 0 && weightStart != weightGoal && isNewWeightAdded) {
                    float currentWeight = Float.parseFloat(weight.getWeightValue());
                    if ((weightStart < weightGoal && weightGoal <= currentWeight) ||
                            (weightStart > weightGoal && weightGoal >= currentWeight)) {
                        SetNewGoalDialog setNewGoalDialog = new SetNewGoalDialog(mContext);
                        setNewGoalDialog.setOnButtonClickListener(new SetNewGoalDialog.OnButtonClickListener() {
                            @Override
                            public void onSetClicked() {
                                Intent intent = new Intent(mContext, WeightGoalActivity.class);
                                intent.putExtra("status", "new_goal_set");
                                intent.putExtra("lastWeightValue", weight.getWeightValue());
                                mContext.startActivity(intent);
                            }

                            @Override
                            public void onCancelClicked() {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putFloat(getString(R.string.pref_weight_goal), 0);
                                editor.apply();
                            }
                        });
                        setNewGoalDialog.setCancelable(false);
                        setNewGoalDialog.show();

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.pref_is_new_weight_added), false);
                        editor.apply();
                    }
                }
            }
        });
    }

    /**
     * 몸무게 도움말 Menu 생성함수
     * @param menu 생성될 Menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_help);
        return true;
    }

    /**
     * 도움말을 눌렀을때 호출되는 callback
     * @param item menu item(도움말)
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_help) {
            Intent intent = new Intent(this, WeightHelpActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_record) {
            Intent intent = new Intent(this, AddRecordActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}