package twoAK.runboyrun.exceptions.api;


/** Login failed*/
public class LoginFailedException extends InsuccessfulResponseException {
    public LoginFailedException() {
        super();
    }

    public LoginFailedException(String message) {
        super(message);
    }

    public LoginFailedException(Throwable throwable) {
        super(throwable);
    }
}