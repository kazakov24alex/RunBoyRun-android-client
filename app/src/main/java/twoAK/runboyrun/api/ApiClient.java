package twoAK.runboyrun.api;


import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twoAK.runboyrun.auth.Auth;
import twoAK.runboyrun.exceptions.api.CheckFailedException;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.exceptions.api.SendTrainingInfoFailedException;
import twoAK.runboyrun.exceptions.api.SignupFailedException;
import twoAK.runboyrun.request.body.ActivityBody;
import twoAK.runboyrun.request.body.CheckBody;
import twoAK.runboyrun.request.body.CommentBody;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.request.body.SignUpBody;
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.BaseResponse;
import twoAK.runboyrun.responses.CheckResponse;
import twoAK.runboyrun.responses.CitiesResponse;
import twoAK.runboyrun.responses.CountriesResponse;
import twoAK.runboyrun.responses.GetActivityDataResponse;
import twoAK.runboyrun.responses.GetCommentsResponse;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.GetProfileResponse;
import twoAK.runboyrun.responses.GetValuesResponse;
import twoAK.runboyrun.responses.SendTrainingInfoResponse;
import twoAK.runboyrun.responses.SignUpResponse;
import twoAK.runboyrun.responses.TokenResponse;
import twoAK.runboyrun.responses.objects.CityObject;
import twoAK.runboyrun.responses.objects.CountryObject;


/** A class that performs requests on the server and receives responses from it.
 * This class is a SINGLTONE!
 * */
public class ApiClient implements IApiClient {

    private static ApiClient inst = null;   // Singleton pattern implementation

    public static final String BASE_URL = "https://runboyrun.herokuapp.com/";
    // TODO: DEBUG
    //public static final String BASE_URL = "http://localhost:8000/";

    private Retrofit retrofit;          // HTTP client
    private RunBoyRunServerApi service; // service of server


    /**
     * Singleton pattern implementation
     */
    public static ApiClient instance() {
        if (inst == null) {
            inst = new ApiClient();
        }
        return inst;
    }


    /**
     * Constructor - initialization of http client and service.
     */
    private ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client((new OkHttpClient.Builder()).build())
                .build();

        service = retrofit.create(RunBoyRunServerApi.class);
    }


    @Override
    public boolean checkToken(String token) throws RequestFailedException, InsuccessfulResponseException {
        Call<BaseResponse> req = service.checkToken("JWT " + token);
        try {
            Response<BaseResponse> response = req.execute();
            if (response.code() == 401) {
                return false;
            } else if (response.code() == 200) {
                return true;
            } else {
                throw new InsuccessfulResponseException("Failed to check token on server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }


    /**
     * The method implements LOGIN request to the server and receives from it the TokenResponce.
     *
     * @param oauth         type of authorization
     * @param identificator identificator of user
     * @param password      password of user
     * @return token for user
     * @throws LoginFailedException data isn't correct or request is not passed
     */
    @Override
    public String login(String oauth, String identificator, String password) throws LoginFailedException {
        Call<TokenResponse> req = service.signin(new LoginBody(oauth, identificator, password));
        try {
            Response<TokenResponse> response = req.execute();
            if (response.body().isSuccess())
                return response.body().getToken();
            else
                throw new LoginFailedException("Bad identificator or password");
        } catch (IOException e) {
            throw new LoginFailedException(e);
        }
    }

    @Override
    public String signup(String oauth, String identificator, String password, String name, String surname,
                         String country, String city, String birthday, String sex) throws SignupFailedException {
        Call<SignUpResponse> req = service.signup(
                new SignUpBody(oauth, identificator, password, name, surname, country, city, birthday, sex));
        try {
            Response<SignUpResponse> response = req.execute();
            if (response.body().isSuccess())
                return response.body().getToken();
            else {
                throw new SignupFailedException(response.body().getError());
            }
        } catch (IOException e) {
            throw new SignupFailedException(e);
        }
    }

    @Override
    public boolean check(String oauth, String identificator) throws CheckFailedException {
        Call<CheckResponse> req = service.check(new CheckBody(oauth, identificator));
        try {
            Response<CheckResponse> response = req.execute();

            if (response.body().isSuccess())
                return response.body().getBusy();
            else
                throw new CheckFailedException("Identificator not unique");
        } catch (IOException e) {
            throw new CheckFailedException(e);
        }
    }

    @Override
    public List<CountryObject> countries() throws RequestFailedException, InsuccessfulResponseException {
        Call<CountriesResponse> req = service.countries();
        try {
            Response<CountriesResponse> response = req.execute();
            if (response.body().isSuccess()) {
                return response.body().getCountries();
            } else {
                throw new InsuccessfulResponseException("Failed to fetch countries from server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }

    @Override
    public List<CityObject> cities(String countryCode) throws RequestFailedException, InsuccessfulResponseException {
        Call<CitiesResponse> req = service.cities(countryCode);
        try {
            Response<CitiesResponse> response = req.execute();
            if (response.body().isSuccess()) {
                return response.body().getCities();
            } else {
                throw new InsuccessfulResponseException("Failed to fetch cities from server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }

    @Override
    public GetProfileResponse getYourProfile()
            throws RequestFailedException, InsuccessfulResponseException  {
        Call<GetProfileResponse> req = service.getYourProfile(Auth.getHeaderField());
        try {
            Response<GetProfileResponse> response = req.execute();
            if (response.body().isSuccess()) {
                return response.body();
            } else {
                throw new InsuccessfulResponseException("Failed getting your profile from server");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }

    @Override
    public GetProfileResponse getProfile(int athlete_id)
            throws RequestFailedException, InsuccessfulResponseException  {
        Call<GetProfileResponse> req = service.getProfile(Auth.getHeaderField(), athlete_id);
        try {
            Response<GetProfileResponse> response = req.execute();
            if (response.body().isSuccess()) {
                return response.body();
            } else {
                throw new InsuccessfulResponseException("Failed getting profile from server");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }

    @Override
    public GetActivityDataResponse getActivityData(int activity_id)
            throws RequestFailedException, InsuccessfulResponseException {
        Call<GetActivityDataResponse> req = service.getActivityData(Auth.getHeaderField(), activity_id);
        try {
            Response<GetActivityDataResponse> response = req.execute();
            if (response.body().isSuccess()) {
                return response.body();
            } else {
                throw new InsuccessfulResponseException("Failed to fetch activity data from server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }

    @Override
    public SendTrainingInfoResponse sendTrainingInfo(ActivityBody activityBody) throws SendTrainingInfoFailedException {
        Call<SendTrainingInfoResponse> req = service.sendProfileInfo(Auth.getHeaderField(), activityBody);
        try {
            Response<SendTrainingInfoResponse> response = req.execute();
            if (response.body().isSuccess()) {
                return response.body();
            } else {
                throw new SendTrainingInfoFailedException("Failed sending training info");
            }
        } catch (IOException e) {
            throw new SendTrainingInfoFailedException(e);
        }
    }

    @Override
    public GetValuesResponse getValues(int activity_id)
            throws RequestFailedException, InsuccessfulResponseException {
        Call<GetValuesResponse> req = service.getValues(Auth.getHeaderField(), activity_id);
        try {
            Response<GetValuesResponse> response = req.execute();
            if(response.body().isSuccess()) {
                return response.body();
            } else {
                throw new InsuccessfulResponseException("Failed to get values from server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }

    }

    @Override
    public boolean sendValue(ValueBody valueBody)
            throws RequestFailedException, InsuccessfulResponseException {
        Call<BaseResponse> req = service.sendValue(Auth.getHeaderField(), valueBody);
        try {
            Response<BaseResponse> response = req.execute();
            if (response.body().isSuccess()) {
                return true;
            } else {
                throw new InsuccessfulResponseException("Failed to send value to server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }


    @Override
    public GetCommentsResponse getComments(int activity_id, int comments_num)
            throws RequestFailedException, InsuccessfulResponseException {
        Call<GetCommentsResponse> req = service.getComments(Auth.getHeaderField(), activity_id, comments_num);
        try {
            Response<GetCommentsResponse> response = req.execute();
            if(response.body().isSuccess()) {
                return response.body();
            } else {
                throw new InsuccessfulResponseException("Failed to getting comments from server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }


    @Override
    public GetCommentsResponse getCommentsPage(int activity_id, int comments_num, int page_num)
            throws RequestFailedException, InsuccessfulResponseException {
        Call<GetCommentsResponse> req = service.getCommentsPage(Auth.getHeaderField(), activity_id, comments_num, page_num);
        try {
            Response<GetCommentsResponse> response = req.execute();
            if(response.body().isSuccess()) {
                return response.body();
            } else {
                throw new InsuccessfulResponseException("Failed to getting comments page from server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }


    @Override
    public boolean sendComment(CommentBody commentBody)
            throws RequestFailedException, InsuccessfulResponseException {
        Call<BaseResponse> req = service.sendComment(Auth.getHeaderField(), commentBody);
        try {
            Response<BaseResponse> response = req.execute();
            if(response.body().isSuccess()) {
                return true;
            } else {
                throw new InsuccessfulResponseException("Failed to send comment to server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }

    @Override
    public GetNewsResponse getNewsPage(int athlete_id, int news_num, int page_num)
        throws RequestFailedException, InsuccessfulResponseException {
        Call<GetNewsResponse> req = service.getNewsPage(Auth.getHeaderField(), athlete_id, news_num, page_num);
        try {
            Response<GetNewsResponse> response = req.execute();
            if(response.body().isSuccess()) {
                return response.body();
            } else {
                throw new InsuccessfulResponseException("Failed to getting news page from server.");
            }
        } catch (IOException e) {
            throw new RequestFailedException(e);
        }
    }


}