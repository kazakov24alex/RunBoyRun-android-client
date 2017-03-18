package twoAK.runboyrun.api;


import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.response.SignupResponse;
import twoAK.runboyrun.response.TokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//import com.et.response.BaseResponse;
//import com.et.response.SignupResponse;




public interface RunBoyRunServerApi {
    @POST("signin")
    Call<TokenResponse> signin(@Body LoginBody login_password);

    @POST("registration")
    Call<SignupResponse> registration(@Body LoginBody login_password);
}