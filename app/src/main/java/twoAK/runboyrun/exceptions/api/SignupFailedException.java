package twoAK.runboyrun.exceptions.api;


public class SignupFailedException  extends InsuccessfulResponseException {
    public SignupFailedException() {
        super();
    }

    public SignupFailedException(String message) {
        super(message);
    }

    public SignupFailedException(Throwable throwable) {
        super(throwable);
    }
}
