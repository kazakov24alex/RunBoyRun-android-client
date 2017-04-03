package twoAK.runboyrun.auth;


import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.CheckFailedException;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
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



    public Boolean checkToken(String token) {
        try {
            return ApiClient.instance().checkToken(token);
        } catch(RequestFailedException e) {
            System.out.println("EXCEPTION!!!!!!!!!! RequestFailedException");
            return false;
        } catch(InsuccessfulResponseException e) {
            System.out.println("EXCEPTION!!!!!!!!!! RequestFailedException");
            return false;
        }
    }

    /** Sending a request to the server to receive a token.
     * @param login     - login of user
     * @param password  - password of user
     * @exception LoginFailedException login failed
     * @return the success of obtaining a token
     * */
    public String signin(String oauth, String login, String password)  {
        try {
            return ApiClient.instance().login(oauth, login, password);
        }
        catch (LoginFailedException e) {
            return null;
        }
    }


    public static boolean check(String oauth, String identificator) {
        try {
            return ApiClient.instance().check(oauth, identificator);
        }
        catch (CheckFailedException e) {
            return false;
        }
    }


    public static String signup(String oAuth, String identificator,String password,String name,String surname,String country,
                                 String city, String birthday, String sex) {
        try {
            return token = ApiClient.instance().signup(oAuth, identificator, password, name, surname, country, city, birthday, sex);
        }
        catch (SignupFailedException e) {
            e.printStackTrace();
            return null;
        }
    }
}