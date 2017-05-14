package twoAK.runboyrun.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import antistatic.spinnerwheel.AbstractWheel;
import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.MetersWheelAdapter;
import twoAK.runboyrun.adapters.TimeWheelAdapter;

public class EnterCompletedActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private TimePicker mTimePicker;
    private DatePicker mDatePicker;

    private AbstractWheel mKilometersWheel;
    private AbstractWheel mMetersWheel;

    private AbstractWheel mHoursWheel;
    private AbstractWheel mMinutesWheel;

    private String mSportType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_enter_completed);
        View inflated = stub.inflate();

        mSportType = getIntent().getStringExtra("SPORT_TYPE");
        mTimePicker = (TimePicker) findViewById(R.id.enter_completed_timepicker_starttime);
        mDatePicker = (DatePicker) findViewById(R.id.enter_completed_datepicker_starttime);

        // Kilometers horizontal wheel
        mKilometersWheel = (AbstractWheel) findViewById(R.id.enter_completed_wheel_km);
        antistatic.spinnerwheel.adapters.TimeAdapter kilometersAdapter = new antistatic.spinnerwheel.adapters.TimeAdapter(this, 1,100, "%d");
        kilometersAdapter.setItemResource(R.layout.wheel_text_centered);
        kilometersAdapter.setItemTextResource(R.id.text);
        mKilometersWheel.setViewAdapter(kilometersAdapter);
        mKilometersWheel.setCurrentItem(2);

        // Meters horizontal wheel
        mMetersWheel = (AbstractWheel) findViewById(R.id.enter_completed_wheel_meters);
        MetersWheelAdapter metersAdapter = new MetersWheelAdapter(this, 1, 20, "%d");
        metersAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        metersAdapter.setItemTextResource(R.id.text);
        mMetersWheel.setViewAdapter(metersAdapter);
        mMetersWheel.setCurrentItem(2);

        // Hours vertical wheel
        mHoursWheel = (AbstractWheel) findViewById(R.id.enter_completed_wheel_hours);
        mHoursWheel.setViewAdapter(new TimeWheelAdapter(this, 23, getString(R.string.enter_completed_text_label_hours)));
        mHoursWheel.setVisibleItems(4);
        mHoursWheel.setCyclic(false);
        mHoursWheel.setCurrentItem(1);

        // Minutes vertical wheel
        mMinutesWheel = (AbstractWheel) findViewById(R.id.enter_completed_wheel_minutes);
        mMinutesWheel.setViewAdapter(new TimeWheelAdapter(this, 60, getString(R.string.enter_completed_text_label_minutes)));
        mMinutesWheel.setVisibleItems(4);
        mMinutesWheel.setCyclic(false);
        mMinutesWheel.setCurrentItem(15);

    }


    public void onRecordButtonClick(View view) {
        // check distance
        int distance = (mKilometersWheel.getCurrentItem()+50)*1000 + (mMetersWheel.getCurrentItem()+1)*50;
        if(distance < 1000) {
            Toast.makeText(this, getString(R.string.enter_completed_toast_error_short_distance), Toast.LENGTH_SHORT).show();
            return;
        }

        // check spent time
        int spentTime = 1000*60*60*mHoursWheel.getCurrentItem()+1000*60*mMinutesWheel.getCurrentItem();
        if(spentTime < (1000*60*10)) {
            Toast.makeText(this, getString(R.string.enter_completed_toast_error_short_activity), Toast.LENGTH_SHORT).show();
            return;
        }

        // check start time
        Calendar calendar = new GregorianCalendar(
                mDatePicker.getYear(),
                mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(),
                mTimePicker.getCurrentHour(),
                mTimePicker.getCurrentMinute());
        long setTime = calendar.getTimeInMillis();

        calendar = new GregorianCalendar();
        long curTime = calendar.getTimeInMillis();

        long weekInMiliSec = 1000*60*60*24*7;

        if( (curTime-setTime) > weekInMiliSec ) {
            Toast.makeText(this, getString(R.string.enter_completed_toast_error_past), Toast.LENGTH_SHORT).show();
            return;
        }

        if(curTime < setTime) {
            Toast.makeText(this, getString(R.string.enter_completed_toast_error_future), Toast.LENGTH_SHORT).show();
            return;
        }


        // send data to condition activity
        Intent intent = new Intent(EnterCompletedActivity.this, ConditionActivity.class);
        intent.putExtra("TRACK", false);
        intent.putExtra("SPORT_TYPE", mSportType);
        intent.putExtra("DISTANCE", distance);
        intent.putExtra("START_TIME", setTime);
        intent.putExtra("SPENT_TIME", spentTime);
        startActivity(intent);

    }


}
