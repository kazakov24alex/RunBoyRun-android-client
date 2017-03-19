package twoAK.runboyrun.api;


import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.responses.TokenResponse;


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


}