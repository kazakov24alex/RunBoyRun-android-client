package twoAK.runboyrun.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import twoAK.runboyrun.request.body.ActivityBody;
import twoAK.runboyrun.request.body.CheckBody;
import twoAK.runboyrun.request.body.CommentBody;
import twoAK.runboyrun.request.body.LoginBody;
import twoAK.runboyrun.request.body.SignUpBody;
import twoAK.runboyrun.request.body.SubscribeBody;
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.BaseResponse;
import twoAK.runboyrun.responses.CheckResponse;
import twoAK.runboyrun.responses.CitiesResponse;
import twoAK.runboyrun.responses.CountriesResponse;
import twoAK.runboyrun.responses.GetActivityDataResponse;
import twoAK.runboyrun.responses.GetCommentsResponse;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.GetProfileResponse;
import twoAK.runboyrun.responses.GetSubscribersResponse;
import twoAK.runboyrun.responses.GetValuesResponse;
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

    @GET("api/profile")
    Call<GetProfileResponse> getYourProfile(@Header("Authorization") String token);

    @GET("api/profile/{athlete_id}")
    Call<GetProfileResponse> getProfile(@Header("Authorization") String token,
                                        @Path("athlete_id") int athlete_id);

    @GET("api/activity/{activity_id}")
    Call<GetActivityDataResponse> getActivityData(@Header("Authorization") String token,
                                                  @Path("activity_id") int activity_id);

    @POST("api/activity")
    Call<SendTrainingInfoResponse> sendProfileInfo(@Header("Authorization") String token,
                                                   @Body ActivityBody activityResponse);

    @GET("api/value/{activity_id}")
    Call<GetValuesResponse> getValues(@Header("Authorization") String token,
                                      @Path("activity_id") int activity_id);

    @POST("api/value")
    Call<BaseResponse> sendValue(@Header("Authorization") String token,
                                 @Body ValueBody valueBody);

    @GET("api/comment/{activity_id}/{comments_num}")
    Call<GetCommentsResponse> getComments(@Header("Authorization") String token,
                                          @Path("activity_id") int activity_id,
                                          @Path("comments_num") int comments_num);

    @GET("api/comment/{activity_id}/{comments_num}/{page_num}")
    Call<GetCommentsResponse> getCommentsPage(@Header("Authorization")  String token,
                                              @Path("activity_id")      int activity_id,
                                              @Path("comments_num")     int comments_num,
                                              @Path("page_num")         int page_num);


    @POST("api/comment")
    Call<BaseResponse> sendComment(@Header("Authorization") String token,
                                   @Body CommentBody commentBody);

    @GET("api/activity/{athlete_id}/{newsNum}/{pageNum}")
    Call<GetNewsResponse> getNewsPage(@Header("Authorization") String token,
                                      @Path("athlete_id")      int athlete_id,
                                      @Path("newsNum")         int newsNum,
                                      @Path("pageNum")         int pageNum);


    @GET("api/subs/{athlete_id}")
    Call<GetSubscribersResponse> getSubscribers(@Header("Authorization") String token,
                                                @Path("athlete_id")      int athlete_id);

    @POST("api/subs")
    Call<BaseResponse> sendSubscribe(@Header("Authorization") String token,
                                     @Body SubscribeBody subscribeBody);
}