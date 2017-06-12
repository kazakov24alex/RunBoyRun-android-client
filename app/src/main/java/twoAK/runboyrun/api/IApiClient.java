package twoAK.runboyrun.api;

import java.util.List;

import twoAK.runboyrun.exceptions.api.CheckFailedException;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.exceptions.api.SendTrainingInfoFailedException;
import twoAK.runboyrun.exceptions.api.SignupFailedException;
import twoAK.runboyrun.request.body.ActivityBody;
import twoAK.runboyrun.request.body.CommentBody;
import twoAK.runboyrun.request.body.SubscribeBody;
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.GetActivityDataResponse;
import twoAK.runboyrun.responses.GetCommentsResponse;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.GetProfileResponse;
import twoAK.runboyrun.responses.GetSearchResponse;
import twoAK.runboyrun.responses.GetSubscribersResponse;
import twoAK.runboyrun.responses.GetValuesResponse;
import twoAK.runboyrun.responses.SendTrainingInfoResponse;
import twoAK.runboyrun.responses.ValueResponse;
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

    public GetProfileResponse getYourProfile()
            throws RequestFailedException, InsuccessfulResponseException;

    public GetProfileResponse getProfile(int athlete_id)
            throws RequestFailedException, InsuccessfulResponseException;

    public SendTrainingInfoResponse sendTrainingInfo(ActivityBody activityBody)
            throws SendTrainingInfoFailedException;

    public GetActivityDataResponse getActivityData(int activity_id)
            throws RequestFailedException, InsuccessfulResponseException;

    public GetValuesResponse getValues(int activity_id)
            throws RequestFailedException, InsuccessfulResponseException;

    public ValueResponse sendValue(ValueBody valueBody)
            throws RequestFailedException, InsuccessfulResponseException;

    public GetCommentsResponse getComments(int activity_id, int comments_num)
            throws RequestFailedException, InsuccessfulResponseException;

    public GetCommentsResponse getCommentsPage(int activity_id, int comments_num, int page_num)
            throws RequestFailedException, InsuccessfulResponseException;

    public boolean sendComment(CommentBody commentBody)
            throws RequestFailedException, InsuccessfulResponseException;

    public GetNewsResponse getNewsPage(int athlete_id, int news_num, int page_num)
            throws RequestFailedException, InsuccessfulResponseException;

    public GetSubscribersResponse getSubscribers(int athlete_id)
            throws RequestFailedException, InsuccessfulResponseException;

    public boolean sendSubscribe(SubscribeBody subscribeBody)
            throws RequestFailedException, InsuccessfulResponseException;

    public List<List<Double>> getRoute(int activity_id)
            throws RequestFailedException, InsuccessfulResponseException;

    public GetSearchResponse getSearch(String searchString)
            throws RequestFailedException, InsuccessfulResponseException;

    public GetNewsResponse getNewsFeedPage(int start_id, int page_size, int page_num)
            throws RequestFailedException, InsuccessfulResponseException;
}