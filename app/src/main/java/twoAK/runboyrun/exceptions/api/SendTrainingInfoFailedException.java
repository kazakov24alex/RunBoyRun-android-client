package twoAK.runboyrun.exceptions.api;


public class SendTrainingInfoFailedException extends InsuccessfulResponseException {
    public SendTrainingInfoFailedException() {
        super();
    }

    public SendTrainingInfoFailedException(String message) {super(message);
    }

    public SendTrainingInfoFailedException(Throwable throwable) {
        super(throwable);
    }
}
