package twoAK.runboyrun.responses;

import java.util.ArrayList;

import twoAK.runboyrun.responses.objects.AthletePreviewObject;


public class GetSearchResponse extends BaseResponse {

    ArrayList<AthletePreviewObject> athletes;

    public ArrayList<AthletePreviewObject> getAthletes() {
        return athletes;
    }

    public void setAthletes(ArrayList<AthletePreviewObject> athletes) {
        this.athletes = athletes;
    }
}
