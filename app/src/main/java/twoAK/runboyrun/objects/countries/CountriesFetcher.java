package twoAK.runboyrun.objects.countries;


import java.util.List;

import twoAK.runboyrun.api.IApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.objects.CountryObject;


public class CountriesFetcher implements ICountriesFetcher {
    private IApiClient client;

    public CountriesFetcher(IApiClient client) {
        this.client = client;
    }

    @Override
    public List<CountryObject> fetchAll() throws RequestFailedException, InsuccessfulResponseException {
        return client.countries();
    }
}