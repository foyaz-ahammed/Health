package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class MeasureEntries {
    public static final class WeightMeasure implements BaseColumns {
        public static final String TABLE_NAME = "weight";
        public static final String WEIGHT_VALUE = "weightValue";
        public static final String FAT_RATE_VALUE = "fatRateValue";
        public static final String MEASURE_DATE = "measureDate";
        public static final String MEASURE_TIME = "measureTime";
        public static final String MEASURE_MILLI_TIME = "measureMilliTime";
        public static final String MODIFIED_TIME = "modifiedTime";

        public static void addTableToDb(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE" + " IF NOT EXISTS " + TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL DEFAULT 0, " +
                    WEIGHT_VALUE + " TEXT, " +
                    FAT_RATE_VALUE + " TEXT, " +
                    MEASURE_DATE + " TEXT, " +
                    MEASURE_TIME + " TEXT, " +
                    MEASURE_MILLI_TIME + " INTEGER, " +
                    MODIFIED_TIME + " INTEGER NOT NULL DEFAULT 0" +
                    ");");
        }
    }

    public static final class GoalMeasure implements BaseColumns {
        public static final String TABLE_NAME = "goal";
        public static final String WEIGHT_START_VALUE = "weightStartValue";
        public static final String WEIGHT_GOAL_VALUE = "weightGoalValue";
        public static final String WATER_TARGET = "waterTarget";

        public static void addTableToDb(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE" + " IF NOT EXISTS " + TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL DEFAULT 0, " +
                    WEIGHT_START_VALUE + " TEXT, " +
                    WEIGHT_GOAL_VALUE + " TEXT, " +
                    WATER_TARGET + " INTEGER " +
                    ");");
        }
    }

    public static final class BloodPressureMeasure implements BaseColumns {
        public static final String TABLE_NAME = "bloodPressure";
        public static final String SYSTOLIC_VALUE = "systolicValue";
        public static final String DIASTOLIC_VALUE = "diastolicValue";
        public static final String PULSE_VALUE = "pulseValue";
        public static final String MEASURE_DATE = "measureDate";
        public static final String MEASURE_TIME = "measureTime";
        public static final String MEASURE_MILLI_TIME = "measureMilliTime";
        public static final String MODIFIED_TIME = "modifiedTime";

        public static void addTableToDb(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE" + " IF NOT EXISTS " + TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL DEFAULT 0, " +
                    SYSTOLIC_VALUE + " INTEGER, " +
                    DIASTOLIC_VALUE + " INTEGER, " +
                    PULSE_VALUE + " INTEGER, " +
                    MEASURE_DATE + " TEXT, " +
                    MEASURE_TIME + " TEXT, " +
                    MEASURE_MILLI_TIME + " INTEGER, " +
                    MODIFIED_TIME + " INTEGER NOT NULL DEFAULT 0" +
                    ");");
        }
    }

    public static final class WaterMeasure implements BaseColumns {
        public static final String TABLE_NAME = "water";
        public static final String GLASSES = "glasses";
        public static final String MEASURE_DATE = "measureDate";

        public static void addTableToDb(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE" + " IF NOT EXISTS " + TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL DEFAULT 0, " +
                    GLASSES + " INTEGER NOT NULL DEFAULT -1, " +
                    MEASURE_DATE + " INTEGER " +
                    ");");
        }
    }

    public static final class ExerciseMeasure implements BaseColumns {
        public static final String TABLE_NAME = "exercise";

        public static final String ACTIVITY = "activity";
        public static final String DURATION = "duration";
        public static final String DISTANCE = "distance";
        public static final String START_TIME = "startTime";
        public static final String MODIFIED_TIME = "modifiedTime";

        public static void addTableToDb(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE" + " IF NOT EXISTS " + TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL DEFAULT 0, " +
                    ACTIVITY + " INTEGER, " +
                    DURATION + " TEXT, " +
                    DISTANCE + " REAL, " +
                    START_TIME + " INTEGER, " +
                    MODIFIED_TIME + " INTEGER NOT NULL DEFAULT 0" +
                    ");");
        }
    }
}
