package twoAK.runboyrun.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.responses.TokenResponse;

//import com.et.response.BaseResponse;
//import com.et.response.SignupResponse;



/** The interface is description of application server REST-ful API */
public interface RunBoyRunServerApi {

/* OPEN ROUTES: */

    @POST("signin")
    Call<TokenResponse> signin(@Body LoginBody login_password);

    /*
    @POST("signup")
    Call<SignupResponse> signup(@Body LoginBody login_password);
    */
}