package twoAK.runboyrun.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import twoAK.runboyrun.request.body.CheckBody;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.request.body.SignUpBody;
import twoAK.runboyrun.responses.GetActivityDataResponse;
import twoAK.runboyrun.request.body.ActivityBody;
import twoAK.runboyrun.responses.BaseResponse;
import twoAK.runboyrun.responses.CheckResponse;
import twoAK.runboyrun.responses.CitiesResponse;
import twoAK.runboyrun.responses.CountriesResponse;
import twoAK.runboyrun.responses.GetProfileInfoResponse;
import twoAK.runboyrun.responses.SendTrainingInfoResponse;
import twoAK.runboyrun.responses.SignUpResponse;
import twoAK.runboyrun.responses.TokenResponse;

//import com.et.response.BaseResponse;
//import com.et.response.SignupResponse;



/** The interface is description of application server REST-ful API */
public interface RunBoyRunServerApi {

/* OPEN ROUTES: */

    @GET("check_token")
    Call<BaseResponse> checkToken(@Header("Authorization") String token);

    @POST("signin")
    Call<TokenResponse> signin(@Body LoginBody login_password);

    @POST("check")
    Call<CheckResponse> check(@Body CheckBody oauth_identificator);

    @GET("countries")
    Call<CountriesResponse> countries();

    @GET("cities/{countryCode}")
    Call<CitiesResponse> cities(@Path("countryCode") String countryCode);

    @POST("signup")
    Call<SignUpResponse> signup(@Body SignUpBody signUpBody);

    @GET("api/profile_info")
    Call<GetProfileInfoResponse> getProfileInfo(@Header("Authorization") String token);

    @GET("api/activity/{activity_id}")
    Call<GetActivityDataResponse> getActivityData(@Header("Authorization") String token, @Path("activity_id") int activity_id);

    @POST("api/activity")
    Call<SendTrainingInfoResponse> sendProfileInfo(@Header("Authorization") String token, @Body ActivityBody activityResponse);
}