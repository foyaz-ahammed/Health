package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.secuso.privacyfriendlyactivitytracker.R;

@Database(entities = {Step.class, WalkingModes.class, Weight.class, Blood.class, HeartRate.class,
        Water.class, Exercise.class, Symptom.class, Ovulation.class, CycleLength.class, HealthOrder.class},
        exportSchema = false, version = 18)
public abstract class MeasureDatabase extends RoomDatabase {
    private static volatile MeasureDatabase INSTANCE;

    public abstract StepCountDao stepCountDao();
    public abstract WalkingModesDao walkingModesDao();
    public abstract WeightDao weightDao();
    public abstract BloodDao BloodDao();
    public abstract WaterDao WaterDao();
    public abstract ExerciseDao ExerciseDao();
    public abstract SymptomDao SymptomDao();
    public abstract OvulationDao OvulationDao();
    public abstract CycleLengthDao CycleLengthDao();
    public abstract HealthOrderDao healthOrderDao();
    public abstract HeartRateDao heartRateDao();

    public static MeasureDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MeasureDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MeasureDatabase.class, "Measure.db")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                INSTANCE.initWalkingModes(context);
                INSTANCE.initHealthOrder(context);
            }
        }
        return INSTANCE;
    }

    private void initWalkingModes(Context context) {
        if (walkingModesDao().getAllWalkingModes().size() <= 0) {
            String[] walkingModesNames = context.getResources().getStringArray(R.array.pref_default_walking_mode_names);
            String[] walkingModesStepLengthStrings = context.getResources().getStringArray(R.array.pref_default_walking_mode_step_lenghts);

            for (int i = 0; i < walkingModesStepLengthStrings.length; i++) {
                String stepLengthString = walkingModesStepLengthStrings[i];
                double stepLength = Double.parseDouble(stepLengthString);
                String name = walkingModesNames[i];
                walkingModesDao().insertOrUpdate(new WalkingModes(i + 1, name, stepLength, 0, i == 0 ? 1 : 0, 0));
            }
        }
    }

    private void initHealthOrder(Context context) {
        if (healthOrderDao().getOrders().size() <= 0) {
            String[] healthItems = context.getResources().getStringArray(R.array.health_item_list);
            for (int i = 0; i < healthItems.length; i++) {
                healthOrderDao().insertOrUpdate(new HealthOrder(0, healthItems[i], i, 1));
            }
        }
    }
}
