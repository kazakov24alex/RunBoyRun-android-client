package twoAK.runboyrun.exceptions;


/** Parent of all custom exceptions. */
public class RunBoyRunException extends Exception {
    public  RunBoyRunException() {
        super();
    }

    public  RunBoyRunException(String message) {
        super(message);
    }

    public  RunBoyRunException(Throwable throwable) {
        super(throwable);
    }

}
