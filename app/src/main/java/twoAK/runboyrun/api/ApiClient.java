package twoAK.runboyrun.api;


import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twoAK.runboyrun.exception.api.LoginFailedException;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.response.TokenResponse;


public class ApiClient implements IApiClient {
    private static ApiClient inst = null;

    public static final String BASE_URL = "https://runboyrun.herokuapp.com/";
    // public static final String BASE_URL = "http://localhost:8000/";

    private Retrofit retrofit;
    private RunBoyRunServerApi service;


    public static ApiClient instance() {
        if(inst == null) {
            inst = new ApiClient();
        }
        return inst;
    }


    private ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client((new OkHttpClient.Builder()).build())
                .build();

        service = retrofit.create(RunBoyRunServerApi.class);
    }



    @Override
    public String login(String login, String password) throws LoginFailedException {
        Call<TokenResponse> req = service.signin(new LoginBody(login, password));
        try {
            Response<TokenResponse> response = req.execute();
            if(response.body().isSuccess())
                return response.body().getToken();
            else
                throw new LoginFailedException("Bad login or password");
        }
        catch (IOException e) {
            throw new LoginFailedException(e);
        }
    }



}