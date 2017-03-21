package twoAK.runboyrun.exceptions.api;


/** Check failed response */
public class CountriesFailedException extends ApiCallException {
    public CountriesFailedException() {
        super();
    }

    public CountriesFailedException(String message) {
        super(message);
    }

    public CountriesFailedException(Throwable throwable) {
        super(throwable);
    }
}

