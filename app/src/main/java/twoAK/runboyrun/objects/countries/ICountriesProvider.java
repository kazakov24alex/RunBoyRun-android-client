package twoAK.runboyrun.objects.countries;


import java.util.List;

import twoAK.runboyrun.responses.objects.CountryObject;


public interface ICountriesProvider {
    public List<CountryObject> getAll();
}