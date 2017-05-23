package twoAK.runboyrun.responses;


import java.util.ArrayList;

import twoAK.runboyrun.responses.objects.ValueObject;

public class GetValuesResponse extends BaseResponse {

    ArrayList<ValueObject> values;

    public ArrayList<ValueObject> getValues() {
        return values;
    }

    public void setValues(ArrayList<ValueObject> values) {
        this.values = values;
    }
}
