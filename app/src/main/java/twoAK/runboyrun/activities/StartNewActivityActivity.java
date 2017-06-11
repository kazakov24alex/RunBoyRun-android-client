package twoAK.runboyrun.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;

public class StartNewActivityActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    static final String ENUM_SPORT_RUNNING = "RUNNING";
    static final String ENUM_SPORT_CYCLING = "CYCLING";
    static final String ENUM_SPORT_WALKING = "WALKING";
    static final String ENUM_SPORT_SKIRUN  = "SKIRUN";

    private int mSportFlag;
    private String mSportValue;
    private List<LinearLayout> mSportButtonsList;

    private LinearLayout mSportRunningLayout;
    private LinearLayout mSportCyclingLayout;
    private LinearLayout mSportWalkingLayout;
    private LinearLayout mSportSkirunLayout;

    private SquareImageView mSportRunningButton;
    private SquareImageView mSportCyclingButton;
    private SquareImageView mSportWalkingButton;
    private SquareImageView mSportSkirunButton;

    private TextView mSportRunningText;
    private TextView mSportCyclingText;
    private TextView mSportWalkingText;
    private TextView mSportSkirunText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_start_activity);
        View inflated = stub.inflate();

        //Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(0).setChecked(true);


        mSportFlag = -1;
        mSportValue = "";
        mSportButtonsList = new ArrayList<LinearLayout>();

        // Initialization images of button panel
        mSportRunningButton = (SquareImageView) findViewById(R.id.start_activity_imagebutton_sport_running);
        mSportRunningButton.setImageResource(R.drawable.sport_type_running);

        mSportCyclingButton = (SquareImageView) findViewById(R.id.start_activity_imagebutton_sport_cycling);
        mSportCyclingButton.setImageResource(R.drawable.sport_type_cycling);

        mSportWalkingButton = (SquareImageView) findViewById(R.id.start_activity_imagebutton_sport_walking);
        mSportWalkingButton.setImageResource(R.drawable.sport_type_walking);

        mSportSkirunButton = (SquareImageView) findViewById(R.id.start_activity_imagebutton_sport_skirun);
        mSportSkirunButton.setImageResource(R.drawable.sport_type_skirun);


        // initialization text of button panel and setting custom font
        Typeface squareFont = Typeface.createFromAsset(getAssets(), "fonts/square.ttf");

        mSportRunningText = (TextView) findViewById(R.id.start_activity_text_sport_running);
        mSportRunningText.setTypeface(squareFont);
        mSportCyclingText = (TextView) findViewById(R.id.start_activity_text_sport_cycling);
        mSportCyclingText.setTypeface(squareFont);
        mSportWalkingText = (TextView) findViewById(R.id.start_activity_text_sport_walking);
        mSportWalkingText.setTypeface(squareFont);
        mSportSkirunText = (TextView) findViewById(R.id.start_activity_text_sport_skirun);
        mSportSkirunText.setTypeface(squareFont);

        // initialization layouts

        mSportRunningLayout = (LinearLayout) findViewById(R.id.start_activity_layout_sport_running);
        mSportButtonsList.add(mSportRunningLayout);
        mSportCyclingLayout = (LinearLayout) findViewById(R.id.start_activity_layout_sport_cycling);
        mSportButtonsList.add(mSportCyclingLayout);
        mSportWalkingLayout = (LinearLayout) findViewById(R.id.start_activity_layout_sport_walking);
        mSportButtonsList.add(mSportWalkingLayout);
        mSportSkirunLayout = (LinearLayout) findViewById(R.id.start_activity_layout_sport_skirun);
        mSportButtonsList.add(mSportSkirunLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View view) {
        if(mSportFlag == -1) {
            Toast.makeText(this, getString(R.string.start_activity_toast_select_sport), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (view.getId()) {
            case R.id.start_activity_button_enter_stat:
                Intent intentCompleted = new Intent(StartNewActivityActivity.this, EnterCompletedActivity.class);
                intentCompleted.putExtra("SPORT_TYPE", mSportValue);
                startActivity(intentCompleted);
                break;
            case R.id.start_activity_button_track_activity:
                Intent intentTrack = new Intent(StartNewActivityActivity.this, TrackActivityActivity.class);
                intentTrack.putExtra("SPORT_TYPE", mSportValue);
                startActivity(intentTrack);
                break;
            default:
                break;
        }
    }


    public void onSportSelectorClick(View view) {
        if(mSportFlag != -1) {
            mSportButtonsList.get(mSportFlag).setBackgroundResource(0);
        }

        switch(view.getId()) {
            case R.id.start_activity_layout_sport_running:
                mSportFlag = 0;
                mSportValue = ENUM_SPORT_RUNNING;
                break;
            case R.id.start_activity_layout_sport_cycling:
                mSportFlag = 1;
                mSportValue = ENUM_SPORT_CYCLING;
                break;
            case R.id.start_activity_layout_sport_walking:
                mSportFlag = 2;
                mSportValue = ENUM_SPORT_WALKING;
                break;
            case R.id.start_activity_layout_sport_skirun:
                mSportFlag = 3;
                mSportValue = ENUM_SPORT_SKIRUN;
                break;
            default:
                mSportFlag = -1;
                mSportValue = "";
                break;
        }

        view.setBackgroundResource(R.color.ORANGE);
    }
}
