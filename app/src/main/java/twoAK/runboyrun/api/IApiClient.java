package twoAK.runboyrun.api;

import twoAK.runboyrun.exception.api.LoginFailedException;

/*
        import com.et.exception.api.RequestFailedException;
        import com.et.exception.api.SignupFailedException;
        import com.et.response.object.StatisticsObject;
        import com.et.response.object.RouteObject;
        import com.et.response.object.StationObject;
    */

public interface IApiClient {

    public String login(String login, String password) throws LoginFailedException;


    //public String signup(String login, String password) throws SignupFailedException;

}