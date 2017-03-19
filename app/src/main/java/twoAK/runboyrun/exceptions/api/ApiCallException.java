package twoAK.runboyrun.exceptions.api;


import twoAK.runboyrun.exceptions.RunBoyRunException;


/** API Call error */
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