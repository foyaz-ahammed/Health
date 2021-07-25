package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class AndroidVersionHelper {
    /**
     * 장치가 걸음수 sensor 를 가지고 있는지 확인하는 함수
     * @param pm PackageManager 의 instance
     * @return true 이면 걸음수 sensor 를 가지고 있는것이고 false 이면 sensor 를 가지고 있지 않음
     */
    public static boolean supportsStepDetector(PackageManager pm) {
        // (Hardware) 걸음수 관련 장치는 Android kitkat 에서부터 도입되였다. (4.4 / API 19)
        // https://developer.android.com/about/versions/android-4.4.html
        Log.w("supportsStepDetector ", "counter " + pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER));
        Log.w("supportsStepDetector ", "detector " + pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR));
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) || pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
    }

    /**
     * 장치에 Step Counter Sensor 를 가지고 있는지 확인하는 함수
     * @param pm PackageManager 의 instance
     * @return true 이면 sensor 를 가지고 있는것이고 false 이면 sensor 를 가지고 있지 않음
     */
    public static boolean isHardwareStepCounterEnabled(PackageManager pm){
        return supportsStepDetector(pm);
    }
}
