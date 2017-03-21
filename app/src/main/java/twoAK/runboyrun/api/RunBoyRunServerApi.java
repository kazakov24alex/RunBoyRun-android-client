package twoAK.runboyrun.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import twoAK.runboyrun.request.body.CheckBody;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.responses.CheckResponse;
import twoAK.runboyrun.responses.CitiesResponse;
import twoAK.runboyrun.responses.CountriesResponse;
import twoAK.runboyrun.responses.TokenResponse;

//import com.et.response.BaseResponse;
//import com.et.response.SignupResponse;



/** The interface is description of application server REST-ful API */
public interface RunBoyRunServerApi {

/* OPEN ROUTES: */

    @POST("signin")
    Call<TokenResponse> signin(@Body LoginBody login_password);

    @POST("check")
    Call<CheckResponse> check(@Body CheckBody identificator);

    @GET("countries")
    Call<CountriesResponse> countries();

    @GET("cities/{countryCode}")
    Call<CitiesResponse> cities(@Path("countryCode") String countryCode);

    /*
    @POST("signup")
    Call<SignupResponse> signup(@Body LoginBody login_password);
    */
}