package twoAK.runboyrun.request.body;


/** Body of LOGIN request */
public class LoginBody {
    private String identificator;
    private String password;

    public LoginBody(String login, String password) {
        this.identificator = login;
        this.password = password;
    }
}