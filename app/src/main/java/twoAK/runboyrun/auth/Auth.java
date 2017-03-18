
package twoAK.runboyrun.auth;


import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exception.api.LoginFailedException;
//import exception.api.SignupFailedException;

public class Auth {
    private static String token = "";



    public static String getToken() {
        return token;
    }



    public static void setToken(String token) {
        Auth.token = token;
    }



    public static void logout() {
        token = "";
    }



    public static String getHeaderField() {
        return "JWT " + getToken();
    }



    public static boolean signin(String login, String password) {
        try {
            token = ApiClient.instance().login(login, password);
            return true;
        }
        catch (LoginFailedException e) {
            return false;
        }
    }



    /*public static boolean signup(String login, String password) {
        try {
            signin = ApiClient.instance().signup(login, password);
            return true;
        }
        catch (SignupFailedException e) {
            return false;
        }
    }*/
}