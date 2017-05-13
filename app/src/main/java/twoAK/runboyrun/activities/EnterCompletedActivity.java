package twoAK.runboyrun.activities;


import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import antistatic.spinnerwheel.AbstractWheel;
import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.MetersWheelAdapter;
import twoAK.runboyrun.adapters.TimeWheelAdapter;

public class EnterCompletedActivity extends BaseActivity {

    private AbstractWheel mKilometersWheel;
    private AbstractWheel mMetersWheel;

    private AbstractWheel mHoursWheel;
    private AbstractWheel mMinutesWheel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_enter_completed);
        View inflated = stub.inflate();


        mKilometersWheel = (AbstractWheel) findViewById(R.id.hour_horizontal);
        antistatic.spinnerwheel.adapters.TimeAdapter kilometersAdapter = new antistatic.spinnerwheel.adapters.TimeAdapter(this, 1,100, "%d");
        kilometersAdapter.setItemResource(R.layout.wheel_text_centered);
        kilometersAdapter.setItemTextResource(R.id.text);
        mKilometersWheel.setViewAdapter(kilometersAdapter);

        mMetersWheel = (AbstractWheel) findViewById(R.id.mins);
        MetersWheelAdapter metersAdapter = new MetersWheelAdapter(this, 1, 20, "%d");
        metersAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        metersAdapter.setItemTextResource(R.id.text);
        mMetersWheel.setViewAdapter(metersAdapter);




        mHoursWheel = (AbstractWheel) findViewById(R.id.hour);
        mHoursWheel.setViewAdapter(new TimeWheelAdapter(this, 23, "hours"));
        mHoursWheel.setVisibleItems(4);
        mHoursWheel.setCyclic(false);
        mHoursWheel.setCurrentItem(1);


        mMinutesWheel = (AbstractWheel) findViewById(R.id.minutes);
        mMinutesWheel.setViewAdapter(new TimeWheelAdapter(this, 60, "min"));
        mMinutesWheel.setVisibleItems(4);
        mMinutesWheel.setCyclic(false);
        mMinutesWheel.setCurrentItem(15);



       /* OnWheelClickedListener click = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };
        mHoursWheel.addClickingListener(click);
        mMinutesWheel.addClickingListener(click);*/





    }



}
