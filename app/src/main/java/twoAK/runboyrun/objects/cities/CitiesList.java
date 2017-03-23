package twoAK.runboyrun.objects.cities;


import java.util.List;

import twoAK.runboyrun.api.IApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.objects.CityObject;

public class CitiesList implements ICitiesProvider {
    private ICitiesFetcher fetcher;

    private List<CityObject> cities;

    public CitiesList(IApiClient client) {
        fetcher = new CitiesFetcher(client);
    }

    public boolean load(String countryCode) {
        try {
            cities = fetcher.fetchAll(countryCode);
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
    public List<CityObject> getAll() {
        return cities;
    }

    @Override
    public CityObject getByPosition(int pos) { return cities.get(pos); }

}
