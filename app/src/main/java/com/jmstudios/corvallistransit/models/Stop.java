package com.jmstudios.corvallistransit.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.jmstudios.corvallistransit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Stop implements ClusterItem {
    public String name;
    public String road;
    public float bearing;
    public boolean adherehancePoint;
    public double latitude;
    public double longitude;
    public int id;
    public DateTime expectedTime;
    public DateTime scheduledTime;
    public String expectedTimeString;
    public String scheduledTimeString;

    public int eta() {
        return etaAsPeriod().getMinutes();
    }

    public Period etaAsPeriod() {
        return new Period(DateTime.now(), this.expectedTime);
    }

    public String etaDisplayText() {
        int eta = eta();
        Period period = etaAsPeriod();
        String text;

        if (eta > 1) {
            text = eta + " mins away";
        } else if (eta == 1) {
            text = "1 min away";
        } else if (eta < 1 && eta >= 0) {
            if (this.expectedTimeString == null || this.expectedTimeString.equals("")) {
                text = "No expected arrivals";
            } else {
                int seconds = period.getSeconds();
                if (seconds > 30) {
                    text = "1 min away";
                } else {
                    text = "Bus at stop";
                }
            }
        } else {
            text = "Bus passed stop";
        }

        return text;
    }

    public String scheduledDisplayText() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");

        return scheduledTime.toString(formatter);
    }

    /*
     * Overridden equals() and hashCode() here for comparison purposes.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (obj instanceof Stop)
                && ((Stop) obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public DateTime getExpectedTime() {
        return Utils.convertToDateTime(this.expectedTimeString);
    }

    public DateTime getScheduledTime() {
        return Utils.convertToDateTime(this.scheduledTimeString);
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }
}
