package twoAK.runboyrun.request.body;


/** Body of LOGIN request */
public class LoginBody {
    private String identificator;
    private String password;

    public LoginBody(String identificator, String password) {
        this.identificator = identificator;
        this.password = password;
    }
}