package twoAK.runboyrun.responses;



public class SendTrainingInfoResponse extends BaseResponse{
    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    int activityId;
}
