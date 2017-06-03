package twoAK.runboyrun.responses;



public class SendTrainingInfoResponse extends BaseResponse{
    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    int activity_id;
}
