package twoAK.runboyrun.request.body;


/** Body of LOGIN request */
public class LoginBody {
    private String oauth;
    private String identificator;
    private String password;

    public LoginBody(String oauth, String identificator, String password) {
        this.oauth = oauth;
        this.identificator = identificator;
        this.password = password;
    }
}