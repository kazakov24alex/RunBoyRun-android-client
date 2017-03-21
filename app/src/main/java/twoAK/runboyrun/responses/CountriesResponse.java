package twoAK.runboyrun.responses;


import java.util.List;

import twoAK.runboyrun.responses.objects.CountryObject;

/** Country response */
public class CountriesResponse extends BaseResponse {

    protected List<CountryObject> countries;

    public List<CountryObject> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryObject> countries) {
        this.countries = countries;
    }
}

