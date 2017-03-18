package twoAK.runboyrun.exception.api;


public class InsuccessfulResponseException extends ApiCallException {
    public InsuccessfulResponseException() {
        super();
    }

    public InsuccessfulResponseException(String message) {
        super(message);
    }

    public InsuccessfulResponseException(Throwable throwable) {
        super(throwable);
    }
}