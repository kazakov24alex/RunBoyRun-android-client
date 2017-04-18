package twoAK.runboyrun.exceptions.api;


public class GetProfileInfoFailedException extends InsuccessfulResponseException  {
    public GetProfileInfoFailedException() {
        super();
    }

    public GetProfileInfoFailedException(String message) {
        super(message);
    }

    public GetProfileInfoFailedException(Throwable throwable) {
        super(throwable);
    }
}
