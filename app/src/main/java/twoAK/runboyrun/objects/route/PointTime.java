
package twoAK.runboyrun.objects.route;


import com.google.android.gms.maps.model.LatLng;

public class PointTime {

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

}
