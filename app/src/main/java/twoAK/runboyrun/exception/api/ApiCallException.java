package twoAK.runboyrun.exception.api;


import twoAK.runboyrun.exception.RunBoyRunException;

public class ApiCallException extends RunBoyRunException {
    public ApiCallException() {
        super();
    }

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(Throwable throwable) {
        super(throwable);
    }
}