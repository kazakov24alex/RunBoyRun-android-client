package twoAK.runboyrun.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.objects.route.PointTime;

/**
 * Created by alex on 05.05.17.
 */

public class ConditionActivity extends AppCompatActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    List<PointTime> mRoutePointTimeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY WAS CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

        Intent i = getIntent();
        mRoutePointTimeList = (List<PointTime>) i.getSerializableExtra("ROUTE");


    }
}
