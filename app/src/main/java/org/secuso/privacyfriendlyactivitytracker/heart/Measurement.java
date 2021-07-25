package org.secuso.privacyfriendlyactivitytracker.heart;

import java.util.Date;

/**
 * 측정된 심박수자료 관련 object
 */
class Measurement<T> {
    final Date timestamp;
    final T measurement;

    Measurement(Date timestamp, T measurement) {
        this.timestamp = timestamp;
        this.measurement = measurement;
    }
}