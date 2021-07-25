package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlyactivitytracker.R;

/**
 * 단위변환 함수들을 포함하는 class
 */
public class UnitHelper {
    /**
     * kilometer 로의 변환을 위한 단위인자
     */
    public static int USER_UNIT_FACTOR = 0;
    /**
     * 길이단위명 descriptor (례: km)
     *
     */
    public static int USER_UNIT_SHORT_DESCRIPTION = 1;
    /**
     * 속도 단위명 (례: km/h)
     */
    public static int USER_UNIT_VELOCITY_DESCRIPTION = 3;
    /**
     * kilometer 로의 변환을 위한 작은 단위인자
     */
    public static int USER_SMALL_UNIT_FACTOR = 4;
    /**
     * 작은 길이 단위명 (례: m)
     */
    public static int USER_SMALL_UNIT_SHORT_DESCRIPTION = 5;

    /**
     * m 를 km 로 변환하는 함수
     *
     * @param m 변환할 값
     * @return 변환된 km 값
     */
    public static double metersToKilometers(double m) {
        return m / 1000;
    }

    /**
     * m/s 를 km/h로 변환하는 함수
     *
     * @param ms 변환할 m/s 값
     * @return 변환된 값
     */
    public static double metersPerSecondToKilometersPerHour(double ms) {
        return ms * 3.6;
    }

    /**
     * kilometer 값을 사용자정의 길이단위로 변환하는 함수
     * @param km      변환할 kilometer값
     * @param context The application context
     * @return 변환된 kilometer 값
     */
    public static double kilometerToUsersLengthUnit(double km, Context context) {
        double factor = Double.parseDouble(getUsersUnit(USER_UNIT_FACTOR, context));
        return km * factor;
    }

    /**
     * kilometer 를 작은 길이단위로 변환하는 함수
     * @param km      변환할 kilometer 값
     * @param context The application context
     * @return 변환된 kilometer 값
     */
    public static double kilometerToUsersSmallLengthUnit(double km, Context context) {
        double factor = Double.parseDouble(getUsersUnit(USER_SMALL_UNIT_FACTOR, context));
        return km * factor;
    }

    /**
     * km/h 값을 사용자정의 속도단위로 변환하는 함수
     * @param kmh     변환할 km/h 속도값
     * @param context The application context
     * @return 변환된 속도값
     */
    public static double kilometersPerHourToUsersVelocityUnit(double kmh, Context context) {
        double factor = Double.parseDouble(getUsersUnit(USER_UNIT_FACTOR, context));
        return kmh * factor;
    }

    /**
     * 단위를 얻는 함수
     *
     * @param context The application context
     * @return 단위 (례 km 혹은 mi)
     */
    public static String usersLengthDescriptionShort(Context context) {
        return getUsersUnit(USER_UNIT_SHORT_DESCRIPTION, context);
    }

    /**
     * 작은 단위를 얻는 함수
     *
     * @param context The application context
     * @return 단위 (례 km 혹은 mi)
     */
    public static String usersSmallLengthDescriptionShort(Context context) {
        return getUsersUnit(USER_SMALL_UNIT_SHORT_DESCRIPTION, context);
    }

    /**
     * 속도단위를 얻는 함수
     *
     * @param context The application context
     * @return 속도단위 (례 km/h 혹은 mph)
     */
    public static String usersVelocityDescription(Context context) {
        return getUsersUnit(USER_UNIT_VELOCITY_DESCRIPTION, context);
    }

    /**
     * km/h 속도값으로부터 사용자정의 속도단위로 변환된 값을 얻는 함수
     * @param kmh km/h 속도값
     * @param context The application context
     * @return 변환된 값
     */
    public static String formatKilometersPerHour(double kmh, Context context){
        return formatString("%.2f", kilometersPerHourToUsersVelocityUnit(kmh, context), context) + usersVelocityDescription(context);
    }

    /**
     * km 거리값으로부터 사용자정의 거리단위로 변환된 값을 얻는 함수
     * @param km 거리값
     * @param context The application context
     * @return 변환된 값
     */
    public static FormattedUnitPair formatKilometers(double km, Context context){
        double kilometerInUsersLenghtUnit = kilometerToUsersLengthUnit(km, context);
        if(kilometerInUsersLenghtUnit < 0.1){
            return new FormattedUnitPair(formatString("%.2f", kilometerToUsersSmallLengthUnit(km, context), context), usersSmallLengthDescriptionShort(context));
        }else{
            return new FormattedUnitPair(formatString("%.2f", kilometerInUsersLenghtUnit, context), usersLengthDescriptionShort(context));
        }
    }

    /**
     * kcal 카로리값으로부터 사용자정의 카로리단위로 변환된 값을 얻는 함수
     * @param kcal 카로리값
     * @param context The application context
     * @return 변환된 값
     */
    public static FormattedUnitPair formatCalories(double kcal, Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String unit_key = sharedPref.getString(context.getString(R.string.pref_unit_of_energy), "cal");
        if(unit_key.equals("J")) {
            double joule = kcal * 4184;
            if (joule < 100) {
                return new FormattedUnitPair(formatString("%.2f", joule, context), context.getString(R.string.joules));
            } else {
                return new FormattedUnitPair(formatString("%.2f", joule / 1000, context), context.getString(R.string.kilojoules));
            }
        }else {
            return new FormattedUnitPair(formatString("%.2f", kcal, context), context.getString(R.string.summary_card_kilocalories));
        }
    }

    private static String formatString(String format, double d, Context context){
        return String.format(context.getResources().getConfiguration().locale, format, d);
    }

    /**
     * Preference 에서 사용자정의 단위정보를 얻는 함수
     *
     * @param type    USER_UNIT_FACTOR,USER_UNIT_SHORT_DESCRIPTION, USER_UNIT_DESCRIPTION, USER_UNIT_VELOCITY_DESCRIPTION
     * @param context The application context
     * @return 요청한 정보 혹은 설정되여 있지 않으면 "-"
     */
    public static String getUsersUnit(int type, Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String unit_key = sharedPref.getString(context.getString(R.string.pref_unit_of_length), "km");
        String unit;
        switch(unit_key){
            case "mi":
                unit = context.getString(R.string.unit_of_length_mi);
                break;
            case "km":
            default:
                unit = context.getString(R.string.unit_of_length_km);
        }
        String[] units = unit.split("\\|");
        if (units.length <= type) {
            return "-";
        }
        return units[type];
    }

    /**
     * 값과 값의 단위 쌍
     */
    public static class FormattedUnitPair{
        private String value;
        private String unit;

        public FormattedUnitPair(String value, String unit){
            this.value = value;
            this.unit = unit;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
