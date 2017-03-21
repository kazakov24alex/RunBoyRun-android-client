package twoAK.runboyrun.api;

import java.util.List;

import twoAK.runboyrun.exceptions.api.CheckFailedException;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.objects.CityObject;
import twoAK.runboyrun.responses.objects.CountryObject;


/** Interface of IApiClient */
public interface IApiClient {

    public String       login(String login, String password) throws LoginFailedException;
    public boolean      check(String identificator) throws CheckFailedException;

    public List<CountryObject>  countries() throws RequestFailedException, InsuccessfulResponseException;
    public List<CityObject>     cities(String countryCode) throws RequestFailedException, InsuccessfulResponseException;

    //public String signup(String login, String password) throws SignupFailedException;

}