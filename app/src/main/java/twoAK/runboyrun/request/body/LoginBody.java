package twoAK.runboyrun.request.body;



public class LoginBody {
    private String identificator;
    private String password;

    public LoginBody(String login, String password) {
        this.identificator = login;
        this.password = password;
    }
}