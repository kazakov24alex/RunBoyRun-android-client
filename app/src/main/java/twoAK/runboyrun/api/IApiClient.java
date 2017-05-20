package twoAK.runboyrun.api;

import java.util.List;

import twoAK.runboyrun.exceptions.api.CheckFailedException;
import twoAK.runboyrun.exceptions.api.GetProfileInfoFailedException;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.exceptions.api.SendTrainingInfoFailedException;
import twoAK.runboyrun.exceptions.api.SignupFailedException;
import twoAK.runboyrun.request.body.ActivityBody;
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.GetActivityDataResponse;
import twoAK.runboyrun.responses.GetProfileInfoResponse;
import twoAK.runboyrun.responses.SendTrainingInfoResponse;
import twoAK.runboyrun.responses.objects.CityObject;
import twoAK.runboyrun.responses.objects.CountryObject;


/** Interface of IApiClient */
public interface IApiClient {

    public List<CountryObject>  countries()
            throws RequestFailedException, InsuccessfulResponseException;

    public List<CityObject>     cities(String countryCode)
            throws RequestFailedException, InsuccessfulResponseException;

    public boolean checkToken(String token)
            throws RequestFailedException, InsuccessfulResponseException;

    public String login(String oauth, String login, String password)
            throws LoginFailedException;

    public boolean check(String oauth, String identificator)
            throws CheckFailedException;

    public String signup(String oauth, String identificator,String password,String name,
                           String surname, String country, String city, String birthday, String sex)
            throws SignupFailedException;

    public GetProfileInfoResponse getProfileInfo()
        throws GetProfileInfoFailedException;

    public SendTrainingInfoResponse sendTrainingInfo(ActivityBody activityBody)
        throws SendTrainingInfoFailedException;

    public GetActivityDataResponse getActivityData(int activity_id)
            throws RequestFailedException, InsuccessfulResponseException;

    public boolean sendValue(ValueBody valueBody)
            throws RequestFailedException, InsuccessfulResponseException;

}