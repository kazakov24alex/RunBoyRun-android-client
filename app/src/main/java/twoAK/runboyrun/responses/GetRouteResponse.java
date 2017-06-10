package twoAK.runboyrun.responses;


import android.os.Parcelable;

import java.util.List;

public class GetRouteResponse extends BaseResponse implements Parcelable {

    private List<List<Double>> route;


    public List<List<Double>> getRoute() {
        return route;
    }

    public void setRoute(List<List<Double>> route) {
        this.route = route;
    }

}
