package twoAK.runboyrun.responses;


import java.util.List;

import twoAK.runboyrun.responses.objects.CityObject;

/** Country response */
public class CitiesResponse extends BaseResponse {

    protected List<CityObject> cities;

    public List<CityObject> getCities() {
        return cities;
    }

    public void setCities(List<CityObject> cities) {
        this.cities = cities;
    }
}

