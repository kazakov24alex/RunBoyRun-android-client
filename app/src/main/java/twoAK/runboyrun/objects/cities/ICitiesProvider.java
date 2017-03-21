package twoAK.runboyrun.objects.cities;


import java.util.List;

import twoAK.runboyrun.responses.objects.CityObject;


public interface ICitiesProvider {
    public List<CityObject> getAll();
}