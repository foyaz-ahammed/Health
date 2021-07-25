package org.secuso.privacyfriendlyactivitytracker.heart;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 측정된 심박수자료들을 보관하는 클라스
 */
class MeasureStore {
    private final CopyOnWriteArrayList<Measurement<Integer>> measurements = new CopyOnWriteArrayList<>();
    private int minimum = 2147483647;
    private int maximum = -2147483648;

    /**
     * The latest N measurements are always averaged in order to smooth the values before it is
     * analyzed.
     *
     * This value may need to be experimented with - it is better on the class level than putting it
     * into local scope
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int rollingAverageSize = 4;

    void add(int measurement) {
        Measurement<Integer> measurementWithDate = new Measurement<>(new Date(), measurement);

        measurements.add(measurementWithDate);
        if (measurement < minimum) minimum = measurement;
        if (measurement > maximum) maximum = measurement;
    }

    void clear() {
        measurements.clear();
    }

    /**
     * 마지막 4개 측정값들을 합한 50개의 목록을 얻는 함수
     */
    CopyOnWriteArrayList<Measurement<Float>> getStdValues() {
        CopyOnWriteArrayList<Measurement<Float>> stdValues = new CopyOnWriteArrayList<>();

        CopyOnWriteArrayList<Measurement<Float>> splitValues = new CopyOnWriteArrayList<>();
        for (int i = 0; i < measurements.size(); i++) {
            int sum = 0;
            for (int rollingAverageCounter = 0; rollingAverageCounter < rollingAverageSize; rollingAverageCounter++) {
                sum += measurements.get(Math.max(0, i - rollingAverageCounter)).measurement;
            }

            Measurement<Float> stdValue =
                    new Measurement<>(
                            measurements.get(i).timestamp,
                            ((float)sum / rollingAverageSize - minimum ) / (maximum - minimum));
            stdValues.add(stdValue);
            if (stdValues.size() > 51) {
                splitValues.clear();
                for (int j = stdValues.size() - 50; j <= stdValues.size() - 1; j++) {
                    splitValues.add(stdValues.get(j));
                }
            }
        }

        if (stdValues.size() > 51)
            return splitValues;
        else return stdValues;
    }

    /**
     * 주어진 개수에 해당하는 마지막 측정값들을 얻는 함수
     */
    @SuppressWarnings("SameParameterValue")
    CopyOnWriteArrayList<Measurement<Integer>> getLastStdValues(int count) {
        if (count < measurements.size()) {
            return  new CopyOnWriteArrayList<>(measurements.subList(measurements.size() - 1 - count, measurements.size() - 1));
        } else {
            return measurements;
        }
    }

    Date getLastTimestamp() {
        return measurements.get(measurements.size() - 1).timestamp;
    }
}
