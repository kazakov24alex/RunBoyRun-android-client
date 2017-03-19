package twoAK.runboyrun.responses;


/** CHECK response */
public class CheckResponse extends BaseResponse {

    private boolean busy;   // token of authentication


    public boolean getBusy() {
        return busy;
    }
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

}
