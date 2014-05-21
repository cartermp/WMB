package com.jmstudios.corvallistransit.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.jmstudios.corvallistransit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class Stop implements ClusterItem {
    public String name;
    public String road;
    public double bearing;
    public boolean adherehancePoint;
    public double latitude;
    public double longitude;
    public int id;
    public DateTime expectedTime;
    public String expectedTimeString;

    public int eta() {
        Period period = new Period(DateTime.now(), this.expectedTime);
        return period.getMinutes();
    }

    public String etaText() {
        int eta = eta();
        String text;

        if (eta > 1) {
            text = eta + " mins away";
        } else if (eta == 1) {
            text = "1 min away";
        } else if (eta < 1 && eta >= 0) {
            if (this.expectedTimeString == null || this.expectedTime.equals("")) {
                text = "No expected arrivals";
            } else {
                text = "Bus at stop";
            }
        } else {
            text = "Bus passed stop";
        }

        return text;
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

    public DateTime getScheduledTime() {
        return Utils.convertToDateTime(this.expectedTimeString);
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }
}
