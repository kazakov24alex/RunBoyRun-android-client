package twoAK.runboyrun.objects.countries;


import java.util.List;

import twoAK.runboyrun.api.IApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.objects.CountryObject;

public class CountriesList implements ICountriesProvider {
    private ICountriesFetcher fetcher;

    private List<CountryObject> countries;

    public CountriesList(IApiClient client) {
        fetcher = new CountriesFetcher(client);
    }

    public boolean load() {
        try {
            countries = fetcher.fetchAll();
            return true;
        }
        catch(InsuccessfulResponseException e) {
            e.printStackTrace();
            return false;
        }
        catch (RequestFailedException e) {
            e.printStackTrace();
            return false;
        }
    }




    @Override
    public List<CountryObject> getAll() {
        return countries;
    }

    @Override
    public CountryObject getByPosition(int pos) { return countries.get(pos); }

}
