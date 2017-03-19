package twoAK.runboyrun.exceptions.api;


/** Check failed response */
public class CheckFailedException extends ApiCallException {
    public CheckFailedException() {
        super();
    }

    public CheckFailedException(String message) {
        super(message);
    }

    public CheckFailedException(Throwable throwable) {
        super(throwable);
    }
}