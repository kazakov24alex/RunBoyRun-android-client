package twoAK.runboyrun.objects.cities;


import java.util.List;

import twoAK.runboyrun.api.IApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.objects.CityObject;


public class CitiesFetcher implements ICitiesFetcher {
    private IApiClient client;

    public CitiesFetcher(IApiClient client) {
        this.client = client;
    }

    @Override
    public List<CityObject> fetchAll(String countryCode) throws RequestFailedException, InsuccessfulResponseException {
        return client.cities(countryCode);
    }
}