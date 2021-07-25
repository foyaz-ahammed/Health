package org.secuso.privacyfriendlyactivitytracker;

import android.content.pm.PackageManager;

import org.secuso.privacyfriendlyactivitytracker.services.AbstractStepDetectorService;
import org.secuso.privacyfriendlyactivitytracker.services.AccelerometerStepDetectorService;
import org.secuso.privacyfriendlyactivitytracker.services.HardwareStepCounterService;
import org.secuso.privacyfriendlyactivitytracker.services.HardwareStepDetectorService;
import org.secuso.privacyfriendlyactivitytracker.utils.AndroidVersionHelper;

/**
 * Factory class
 */

public class Factory {

    /**
     * App 에서 리용될 걸음수관련 service 를 돌려주는 함수
     * @param pm PackageManager 의 instance
     * @return 장치에 걸음수 관련 sensor 가 존재하면 HardwareStepCounterService class 돌려주고 존재하지 않으면
     * AccelerometerStepDetectorService class 돌려준다.
     */
    public static Class<? extends AbstractStepDetectorService> getStepDetectorServiceClass(PackageManager pm){
        if(pm != null && AndroidVersionHelper.supportsStepDetector(pm)) {
            return HardwareStepCounterService.class;
        }else{
            return AccelerometerStepDetectorService.class;
        }
    }
}
