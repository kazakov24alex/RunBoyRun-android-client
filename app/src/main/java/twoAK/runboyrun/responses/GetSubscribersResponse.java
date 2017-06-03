package twoAK.runboyrun.responses;

import java.util.ArrayList;

import twoAK.runboyrun.responses.objects.AthletePreviewObject;


public class GetSubscribersResponse extends BaseResponse {

    ArrayList<AthletePreviewObject> subscribers;

    public ArrayList<AthletePreviewObject> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(ArrayList<AthletePreviewObject> subscribers) {
        this.subscribers = subscribers;
    }
}
