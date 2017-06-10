package twoAK.runboyrun.responses;


import java.util.List;

import twoAK.runboyrun.activities.ConditionActivity;

public class GetRouteResponse extends BaseResponse {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private List<List<Double>> route;

    public GetRouteResponse(List<List<Double>> route) {
        this.route = route;
    }

    public List<List<Double>> getRoute() {
        return route;
    }

    public void setRoute(List<List<Double>> route) {
        this.route = route;
    }

}
