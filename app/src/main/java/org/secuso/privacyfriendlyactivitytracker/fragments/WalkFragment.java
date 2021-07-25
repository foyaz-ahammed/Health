package org.secuso.privacyfriendlyactivitytracker.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ExerciseMeasureActivity;
import org.secuso.privacyfriendlyactivitytracker.exercise.ExerciseActivity;
import org.secuso.privacyfriendlyactivitytracker.viewModel.ExerciseViewModel;

import java.util.Objects;

/**
 * 실외걷기화면
 */
public class WalkFragment extends Fragment implements View.OnClickListener {
    ImageView mExerciseStartBtn;
    TextView mTotalDistance;
    ImageView mBackgroundImage;
    LinearLayout mSwitchBtn;

    ExerciseViewModel exerciseViewModel;

    public WalkFragment() {
        // Required empty public constructor
    }

    public static WalkFragment newInstance() {
        WalkFragment fragment = new WalkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exerciseViewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        mExerciseStartBtn = view.findViewById(R.id.exercise_start_btn);
        mExerciseStartBtn.setOnClickListener(this);
        mTotalDistance = view.findViewById(R.id.total_distance);
        mSwitchBtn = view.findViewById(R.id.switch_btn);
        mSwitchBtn.setOnClickListener(this);
        mBackgroundImage = view.findViewById(R.id.background_image);
        mBackgroundImage.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.walk_bg));

        exerciseViewModel.getTotalWalkDistance().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Float totalDistance) {
                mTotalDistance.setText(String.format("%.2f", totalDistance != null ? totalDistance : 0));
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.switch_btn || view.getId() == R.id.total_distance) {
            Intent intent = new Intent(getContext(), ExerciseActivity.class);
            intent.putExtra("exerciseType", 2);
            startActivity(intent);
        } else if (view.getId() == R.id.exercise_start_btn) {
            //SharedPreference 에 운동종목 보관
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.pref_training_type), 2);
            editor.apply();

            Intent intent = new Intent(getContext(), ExerciseMeasureActivity.class);
            startActivity(intent);
        }
    }
}