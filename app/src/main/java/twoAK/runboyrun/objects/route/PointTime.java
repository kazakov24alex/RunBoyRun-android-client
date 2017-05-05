
package twoAK.runboyrun.objects.route;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class PointTime implements Serializable {

    public double lat;
    public double lng;
    public int    sec;

    public PointTime(LatLng latlng, int sec) {
        this.lat = latlng.latitude;
        this.lng = latlng.longitude;
        this.sec = sec;
    }

    public PointTime(double lat, double lng, int sec) {
        this.lat = lat;
        this.lng = lng;
        this.sec = sec;
    }

    public PointTime(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        sec = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(sec);
    }

    public static final Parcelable.Creator<PointTime> CREATOR = new Parcelable.Creator<PointTime>()
    {
        public PointTime createFromParcel(Parcel in)
        {
            return new PointTime(in);
        }
        public PointTime[] newArray(int size)
        {
            return new PointTime[size];
        }
    };

}
