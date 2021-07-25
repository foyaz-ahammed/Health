package org.secuso.privacyfriendlyactivitytracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 자주 리용되는 함수들
 */
public class Utils {

    /**
     * 시간형식의 String을 얻는 함수
     * @param context
     * @param hour 시
     * @param minute 분
     * @return 시간형식의 String 결과값
     */
    public static String getTimeString(Context context, int hour, int minute){
        boolean isAm = hour < 12;
        @SuppressLint("DefaultLocale") String resultString = String.format("%1$02d:%2$02d %3$s",
                get12Hour(hour), minute,
                isAm? context.getString(R.string.lany_am_label) : context.getString(R.string.lany_pm_label));
        return resultString;
    }

    /**
     * 12시간 형식의 시간을 얻는 함수
     * @param hour 주어진 시간
     * @return 12시간 형식의 시간
     */
    public static int get12Hour(int hour){
        int res = hour % 12;
        return res == 0? 12 : res;
    }

    /**
     * 평균값을 얻는 함수
     * @param data 기초자료
     * @return 평균값
     */
    public static float getAverageValue(List<Float> data) {
        float sum = 0;
        for (int i = 0; i < data.size(); i ++) {
            sum += data.get(i);
        }
        return sum / data.size();
    }

    /**
     * 날자형식의 String 을 얻는 함수
     * @param dateTime 변환할 날자
     * @param context
     * @return String 형의 날자형식
     */
    public static String getDateString(DateTime dateTime, Context context){
        return context.getResources().getString(R.string.date_format2, dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }

    /**
     * dp 를 pixel 로 변환하는 함수
     * @param dipValue dp 값
     * @return 변환된  pixel 값
     */
    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 주어진 Calendar 형으로부터 int 형의 날자를 얻는 함수
     * @param calendar 변환할 Calendar 날자자료
     * @return int 형의 날자 (례: 20200101)
     */
    public static int getIntDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return Integer.parseInt(dateFormat.format(new Date(calendar.getTimeInMillis())).replace("-", ""));
    }

    /**
     * 주어진 int 형 날자로부터 Calendar 형을 얻는 함수
     * @param date int 형 날자
     * @return Calendar 형의 날자자료
     */
    public static Calendar convertIntDateToCalendar(int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(String.valueOf(date).substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(String.valueOf(date).substring(4, 6)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(String.valueOf(date).substring(6, 8)));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 주어진 두 Calendar 형의 날자들로부터 차이나는 날자수를 얻는 함수
     * @param start Calendar 형의 날자
     * @param end Calendar 형의 날자
     * @return 차이나는 날자수
     */
    public static int getDiffDays(Calendar start, Calendar end) {
        return (int) TimeUnit.MILLISECONDS.toDays(end.getTimeInMillis() - start.getTimeInMillis());
    }

    /**
     * x, y 좌표가 주어진 view 령역에 속하는지 검사하는 함수
     * @param v x, y 좌표를 검사할 령역
     * @param x x 좌표
     * @param y  y 좌표
     * @return true 이면 주어진 view 령역에 속하는것이고 false 이면 속하지 않는다.
     */
    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (v.getTranslationX() + 0.5f);
        final int ty = (int) (v.getTranslationY() + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    /**
     * 장치가 체계를 기동해서부터 지금까지 지나온 시간을 얻는 함수
     * @return long 형의 경과시간
     */
    public static long now() {
        return SystemClock.elapsedRealtime();
    }

    /**
     * 현재시간을 얻는 함수
     * @return long 형의 현재시간
     */
    public static long wallClock() {
        return System.currentTimeMillis();
    }

    /**
     * 화상 byte 자료에서 개별적인 색상(red, green, blue)의 합을 얻는 함수
     * @param yuv420sp 화상 byte 자료
     * @param width 화상의 너비
     * @param height 화상의 높이
     * @param type 색상형태
     * @return 얻으려는 색상의 합
     */
    public static int decodeYUV420SPtoRedBlueGreenSum(byte[] yuv420sp, int width, int height, int type) {
        if (yuv420sp == null) return 0;

        final int frameSize = width * height;

        int sum = 0;
        int sumr = 0;
        int sumg = 0;
        int sumb = 0;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & yuv420sp[yp]) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                sumr += red;
                sumg += green;
                sumb += blue;
            }
        }
        switch (type) {
            case (1):
                sum = sumr;
                break;
            case (2):
                sum = sumb;
                break;
            case (3):
                sum = sumg;
                break;
        }
        return sum;
    }
}
