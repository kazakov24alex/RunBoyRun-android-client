package twoAK.runboyrun.responses;


/** SignUp response */
public class SignUpResponse extends BaseResponse {

    private String token;   // token of authentication


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

