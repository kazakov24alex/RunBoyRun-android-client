package twoAK.runboyrun.api;


import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twoAK.runboyrun.exceptions.api.CheckFailedException;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.exceptions.api.SignupFailedException;
import twoAK.runboyrun.request.body.CheckBody;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.request.body.SignUpBody;
import twoAK.runboyrun.responses.CheckResponse;
import twoAK.runboyrun.responses.CitiesResponse;
import twoAK.runboyrun.responses.CountriesResponse;
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
    // public static final String BASE_URL = "http://localhost:8000/";

    private Retrofit retrofit;          // HTTP client
    private RunBoyRunServerApi service; // service of server


    /** Singleton pattern implementation */
    public static ApiClient instance() {
        if(inst == null) {
            inst = new ApiClient();
        }
        return inst;
    }


    /** Constructor - initialization of http client and service. */
    private ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client((new OkHttpClient.Builder()).build())
                .build();

        service = retrofit.create(RunBoyRunServerApi.class);
    }


    /** The method implements LOGIN request to the server and receives from it the TokenResponce.
     * @param identificator identificator of user
     * @param password      password of user
     * @exception LoginFailedException data isn't correct or request is not passed
     * @return token for user
     * */
     @Override
    public String login(String identificator, String password) throws LoginFailedException {
        Call<TokenResponse> req = service.signin(new LoginBody(identificator, password));
        try {
            Response<TokenResponse> response = req.execute();
            if(response.body().isSuccess())
                return response.body().getToken();
            else
                throw new LoginFailedException("Bad identificator or password");
        }
        catch (IOException e) {
            throw new LoginFailedException(e);
        }
    }

    @Override
    public String signup(String identificator,String password,String name,String surname,String country,
                         String city, String birthday, String sex) throws SignupFailedException{
        Call<SignUpResponse> req = service.signup(new SignUpBody(identificator, password, name, surname, country, city, birthday, sex));
        try {
            Response<SignUpResponse> response = req.execute();
            if (response.body().isSuccess())
                return response.body().getToken();
            else {
                throw new SignupFailedException(response.body().getError());
            }
        }
        catch (IOException e){
            throw new SignupFailedException(e);
        }
    }

    @Override
    public boolean check(String identificator) throws CheckFailedException {
        Call<CheckResponse> req = service.check(new CheckBody(identificator));
        try {
            Response<CheckResponse> response = req.execute();
            if(response.body().isSuccess())
                return response.body().getBusy();
            else
                throw new CheckFailedException("Request is failed");
        }
        catch (IOException e) {
            throw new CheckFailedException(e);
        }
    }

    @Override
    public List<CountryObject> countries() throws RequestFailedException, InsuccessfulResponseException {
        Call<CountriesResponse> req = service.countries();
        try {
            Response<CountriesResponse> response = req.execute();
            if(response.body().isSuccess()){
                return response.body().getCountries();
            } else {
                throw new InsuccessfulResponseException("Failed to fetch countries from server.");
            }
        } catch(IOException e) {
            throw new RequestFailedException(e);
        }
    }

    @Override
    public List<CityObject> cities(String countryCode) throws RequestFailedException, InsuccessfulResponseException {
        Call<CitiesResponse> req = service.cities(countryCode);
        try {
            Response<CitiesResponse> response = req.execute();
            if(response.body().isSuccess()) {
                return response.body().getCities();
            } else {
                throw new InsuccessfulResponseException("Failed to fetch cities from server.");
            }
        } catch(IOException e) {
            throw new RequestFailedException(e);
        }
    }



}