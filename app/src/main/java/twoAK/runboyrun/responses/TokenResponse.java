package twoAK.runboyrun.responses;


/** Token response */
public class TokenResponse extends BaseResponse {

    private String token;   // token of authentication
    private String role;    // role of user


    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }
}
