package twoAK.runboyrun.objects.countries;

import java.util.List;

import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.objects.CountryObject;


public interface ICountriesFetcher {
    public List<CountryObject> fetchAll() throws RequestFailedException, InsuccessfulResponseException;
}
