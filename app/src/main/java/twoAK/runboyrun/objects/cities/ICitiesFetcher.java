package twoAK.runboyrun.objects.cities;

import java.util.List;

import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.objects.CityObject;


public interface ICitiesFetcher {
    public List<CityObject> fetchAll(String countryCode) throws RequestFailedException, InsuccessfulResponseException;
}
