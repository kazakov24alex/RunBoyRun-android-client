package twoAK.runboyrun.api;

import twoAK.runboyrun.exceptions.api.LoginFailedException;

/*
import com.et.exception.api.RequestFailedException;
import com.et.exception.api.SignupFailedException;
*/


/** Interface of IApiClient */
public interface IApiClient {

    public String login(String login, String password) throws LoginFailedException;
    //public String signup(String login, String password) throws SignupFailedException;

}