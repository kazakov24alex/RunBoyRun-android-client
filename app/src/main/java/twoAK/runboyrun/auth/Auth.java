package twoAK.runboyrun.auth;


import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.CheckFailedException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.exceptions.api.SignupFailedException;
//import exception.api.SignupFailedException;


/** This class is designed to manage registration, login and logout operations.
 *  This class stores token and work with it.
 *   @version in process
 */
public class Auth {

    private static String token = "";   // stored token


    /** Get available token.
     * @return token
     * */
    public static String getToken() {
        return token;
    }


    /** Set the token.
     * @param token - received token
     */
    public static void setToken(String token) {
        Auth.token = token;
    }


    /** Operation Logout - token of forgotten */
    public static void logout() {
        token = "";
    }


    /** Binds the required header field for sending responses.
     * @return ready request header
     * */
    public static String getHeaderField() {
        return "JWT " + getToken();
    }


    /** Sending a request to the server to receive a token.
     * @param login     - login of user
     * @param password  - password of user
     * @exception LoginFailedException login failed
     * @return the success of obtaining a token
     * */
    public static boolean signin(String oauth, String login, String password)  {
        try {
            token = ApiClient.instance().login(oauth, login, password);
            return true;
        }
        catch (LoginFailedException e) {
            return false;
        }
    }


    public static boolean check(String identificator) {
        try {
            return ApiClient.instance().check(identificator);
        }
        catch (CheckFailedException e) {
            return false;
        }
    }


    public static boolean signup(String oAuth, String identificator,String password,String name,String surname,String country,
                                 String city, String birthday, String sex) {
        try {
            token = ApiClient.instance().signup(oAuth, identificator, password, name, surname, country, city, birthday, sex);
            return true;
        }
        catch (SignupFailedException e) {
            e.printStackTrace();
            return false;
        }
    }
}