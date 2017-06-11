
package twoAK.runboyrun.objects.route;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class RoutePoint implements Serializable {

    public double lat;
    public double lng;

    public RoutePoint(LatLng latlng) {
        this.lat = latlng.latitude;
        this.lng = latlng.longitude;
    }

    public RoutePoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public RoutePoint(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public static final Parcelable.Creator<RoutePoint> CREATOR = new Parcelable.Creator<RoutePoint>()
    {
        public RoutePoint createFromParcel(Parcel in)
        {
            return new RoutePoint(in);
        }
        public RoutePoint[] newArray(int size)
        {
            return new RoutePoint[size];
        }
    };

}
