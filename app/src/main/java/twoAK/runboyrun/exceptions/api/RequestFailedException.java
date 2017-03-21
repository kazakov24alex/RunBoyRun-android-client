package twoAK.runboyrun.exceptions.api;


public class RequestFailedException extends ApiCallException {
    public RequestFailedException() {
        super();
    }

    public RequestFailedException(String message) {
        super(message);
    }

    public RequestFailedException(Throwable throwable) {
        super(throwable);
    }
}