package twoAK.runboyrun.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lantouzi.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.GetProfileInfoFailedException;
import twoAK.runboyrun.exceptions.api.SendTrainingInfoFailedException;
import twoAK.runboyrun.objects.route.PointTime;
import twoAK.runboyrun.request.body.ActivityBody;
import twoAK.runboyrun.responses.GetProfileInfoResponse;
import twoAK.runboyrun.responses.SendTrainingInfoResponse;


public class ConditionActivity extends AppCompatActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";


    static final String ENUM_WEATHER_SUNNY  = "SUNNY";
    static final String ENUM_WEATHER_CLOUDY = "CLOUDY";
    static final String ENUM_WEATHER_RAINY  = "RAINY";
    static final String ENUM_WEATHER_SNOWY  = "SNOWY";

    static final String ENUM_RELIEF_STADIUM = "STADIUM";
    static final String ENUM_RELIEF_PARK    = "PARK";
    static final String ENUM_RELIEF_CROSS   = "CROSS";
    static final String ENUM_RELIEF_HILLS   = "HILLS";

    static final String ENUM_CONDITIOM_GOOD     = "GOOD";
    static final String ENUM_CONDITIOM_MEDIUM   = "MEDIUM";
    static final String ENUM_CONDITIOM_TIRED    = "TIRED";
    static final String ENUM_CONDITIOM_BEATED   = "BEATED";



    private List<PointTime> mRoutePointTimeList;

    private List<SquareImageView> mWeatherButtonsList;
    private List<SquareImageView> mReliefButtonsList;
    private List<SquareImageView> mConditionButtonsList;

    private int mWeatherFlag;
    private int mReliefFlag;
    private int mConditionFlag;

    private String mWeatherValue;
    private String mReliefValue;
    private String mConditionValue;
    private int mTemperatureValue;

    private WheelView mTemperatureWheelView;

    private SquareImageView mWeatherSunnyButton;
    private SquareImageView mWeatherCloudyButton;
    private SquareImageView mWeatherRainyButton;
    private SquareImageView mWeatherSnowyButton;

    private SquareImageView mReliefStadiumButton;
    private SquareImageView mReliefParkButton;
    private SquareImageView mReliefCrossButton;
    private SquareImageView mReliefHillsButton;

    private SquareImageView mConditionGoodButton;
    private SquareImageView mConditionMediumButton;
    private SquareImageView mConditionTiredButton;
    private SquareImageView mConditionBeatedButton;


    private TextView mWeatherSunnyText;
    private TextView mWeatherCloudyText;
    private TextView mWeatherRainyText;
    private TextView mWeatherSnowyText;

    private TextView mReliefStadiumText;
    private TextView mReliefCrossText;
    private TextView mReliefHillsText;
    private TextView mReliefParkText;

    private TextView mConditionGoodText;
    private TextView mConditionMediumText;
    private TextView mConditionTiredText;
    private TextView mConditionBeatedText;

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY WAS CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

        Intent intent = getIntent();
        mRoutePointTimeList = (List<PointTime>) intent.getSerializableExtra("ROUTE");

        mWeatherButtonsList = new ArrayList<SquareImageView>();
        mReliefButtonsList = new ArrayList<SquareImageView>();
        mConditionButtonsList = new ArrayList<SquareImageView>();

        mWeatherFlag    = -1;
        mReliefFlag     = -1;
        mConditionFlag  = -1;

        mWeatherValue   = "";
        mReliefValue    = "";
        mConditionValue = "";
        mTemperatureValue = 0;


        // Initialization images of button panel
        mWeatherSunnyButton = (SquareImageView) findViewById(R.id.condition_squareimage_weather_sunny);
        mWeatherSunnyButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mWeatherButtonsList.add(mWeatherSunnyButton);
        mWeatherCloudyButton = (SquareImageView) findViewById(R.id.condition_squareimage_weather_cloudy);
        mWeatherCloudyButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mWeatherButtonsList.add(mWeatherCloudyButton);
        mWeatherRainyButton = (SquareImageView) findViewById(R.id.condition_squareimage_weather_rainy);
        mWeatherRainyButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mWeatherButtonsList.add(mWeatherRainyButton);
        mWeatherSnowyButton = (SquareImageView) findViewById(R.id.condition_squareimage_weather_snowy);
        mWeatherSnowyButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mWeatherButtonsList.add(mWeatherSnowyButton);

        mReliefStadiumButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_stadium);
        mReliefStadiumButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mReliefButtonsList.add(mReliefStadiumButton);
        mReliefParkButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_park);
        mReliefParkButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mReliefButtonsList.add(mReliefParkButton);
        mReliefCrossButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_cross);
        mReliefCrossButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mReliefButtonsList.add(mReliefCrossButton);
        mReliefHillsButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_hills);
        mReliefHillsButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mReliefButtonsList.add(mReliefHillsButton);

        mConditionGoodButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_good);
        mConditionGoodButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mConditionButtonsList.add(mConditionGoodButton);
        mConditionMediumButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_medium);
        mConditionMediumButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mConditionButtonsList.add(mConditionMediumButton);
        mConditionTiredButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_tired);
        mConditionTiredButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mConditionButtonsList.add(mConditionTiredButton);
        mConditionBeatedButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_beated);
        mConditionBeatedButton.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        mConditionButtonsList.add(mConditionBeatedButton);

        // initialization text of button panel and setting custom font
        Typeface squareFont = Typeface.createFromAsset(getAssets(), "fonts/square.ttf");

        mWeatherSunnyText = (TextView) findViewById(R.id.condition_text_weather_sunny);
        mWeatherSunnyText.setTypeface(squareFont);
        mWeatherCloudyText = (TextView) findViewById(R.id.condition_text_weather_cloudy);
        mWeatherCloudyText.setTypeface(squareFont);
        mWeatherRainyText = (TextView) findViewById(R.id.condition_text_weather_rainy);
        mWeatherRainyText.setTypeface(squareFont);
        mWeatherSnowyText = (TextView) findViewById(R.id.condition_text_weather_snowy);
        mWeatherSnowyText.setTypeface(squareFont);

        mReliefStadiumText = (TextView) findViewById(R.id.condition_text_relief_stadium);
        mReliefStadiumText.setTypeface(squareFont);
        mReliefParkText = (TextView) findViewById(R.id.condition_text_relief_park);
        mReliefParkText.setTypeface(squareFont);
        mReliefCrossText = (TextView) findViewById(R.id.condition_text_relief_cross);
        mReliefCrossText.setTypeface(squareFont);
        mReliefHillsText = (TextView) findViewById(R.id.condition_text_relief_hills);
        mReliefHillsText.setTypeface(squareFont);

        mConditionGoodText = (TextView) findViewById(R.id.condition_text_condition_good);
        mConditionGoodText.setTypeface(squareFont);
        mConditionMediumText = (TextView) findViewById(R.id.condition_text_condition_medium);
        mConditionMediumText.setTypeface(squareFont);
        mConditionTiredText = (TextView) findViewById(R.id.condition_text_condition_tired);
        mConditionTiredText.setTypeface(squareFont);
        mConditionBeatedText = (TextView) findViewById(R.id.condition_text_condition_beated);
        mConditionBeatedText.setTypeface(squareFont);

        mTemperatureWheelView = (WheelView) findViewById(R.id.condition_wheelview_temperature);
        List<String> items = new ArrayList<>();
        for(int i=-50; i<=50; i++) {
            items.add(Integer.toString(i));
        }
        mTemperatureWheelView.setItems(items);
        mTemperatureWheelView.setAdditionCenterMark("°C");
        mTemperatureWheelView.selectIndex(60);
        mTemperatureWheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                mTemperatureValue = position-50;
            }
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) { }
        });
    }


    public class SendTrainingInfoTask extends AsyncTask<Void, Void, SendTrainingInfoResponse> {
        private String errMes;  // error message possible
        private ActivityBody body;

        SendTrainingInfoTask(ActivityBody _body) {
            errMes = null;
            body = _body;
            showProgress(getString(R.string.profile_loading_progress_dialog));
        }

        @Override
        protected SendTrainingInfoResponse doInBackground(Void... params) {
            Log.i("SendingTrainingInfoAct", "Trying to send training info.");
            try {
                return ApiClient.instance().sendTrainingInfo(body);
            } catch (SendTrainingInfoFailedException e) {
                errMes = getString(R.string.sendtraininginfo_error);
            }
            return null;
        }

        @Override
        protected void onPostExecute(SendTrainingInfoResponse response) {
            if (response.getActivityId() != 0) {
                Toast.makeText(getApplicationContext(), "SUCCESS RECORD", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * The task was canceled.
         */
        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            hideProgressDialog();
        }
    }

    protected void showProgress(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    /** Hide the progress dialog.*/
    protected void hideProgressDialog() {
        mProgressDialog.dismiss();
    }


    public void onWeatherButtonClick(View view) {
        if(mWeatherFlag != -1) {
            mWeatherButtonsList.get(mWeatherFlag).setBackgroundResource(0);
        }

        switch(view.getId()) {
            case R.id.condition_squareimage_weather_sunny:
                mWeatherFlag = 0;
                mWeatherValue = ENUM_WEATHER_SUNNY;
                break;
            case R.id.condition_squareimage_weather_cloudy:
                mWeatherFlag = 1;
                mWeatherValue = ENUM_WEATHER_CLOUDY;
                break;
            case R.id.condition_squareimage_weather_rainy:
                mWeatherFlag = 2;
                mWeatherValue = ENUM_WEATHER_RAINY;
                break;
            case R.id.condition_squareimage_weather_snowy:
                mWeatherFlag = 3;
                mWeatherValue = ENUM_WEATHER_SNOWY;
                break;
            default:
                mWeatherFlag = -1;
                mWeatherValue = "";
                break;
        }

        view.setBackgroundResource(R.color.IMAGE_BUTTON_SELECT);
    }

    public void onReliefButtonClick(View view) {
        if(mReliefFlag != -1) {
            mReliefButtonsList.get(mReliefFlag).setBackgroundResource(0);
        }

        switch(view.getId()) {
            case R.id.condition_squareimage_relief_stadium:
                mReliefFlag = 0;
                mReliefValue = ENUM_RELIEF_STADIUM;
                break;
            case R.id.condition_squareimage_relief_park:
                mReliefFlag = 1;
                mReliefValue = ENUM_RELIEF_PARK;
                break;
            case R.id.condition_squareimage_relief_cross:
                mReliefFlag = 2;
                mReliefValue = ENUM_RELIEF_CROSS;
                break;
            case R.id.condition_squareimage_relief_hills:
                mReliefFlag = 3;
                mReliefValue = ENUM_RELIEF_HILLS;
                break;
            default:
                mReliefFlag = -1;
                mReliefValue = "";
                break;
        }

        view.setBackgroundResource(R.color.IMAGE_BUTTON_SELECT);
    }

    public void onConditionButtonClick(View view) {
        if(mConditionFlag != -1) {
            mConditionButtonsList.get(mConditionFlag).setBackgroundResource(0);
        }

        switch(view.getId()) {
            case R.id.condition_squareimage_condition_good:
                mConditionFlag = 0;
                mConditionValue = ENUM_CONDITIOM_GOOD;
                break;
            case R.id.condition_squareimage_condition_medium:
                mConditionFlag = 1;
                mConditionValue = ENUM_CONDITIOM_MEDIUM;
                break;
            case R.id.condition_squareimage_condition_tired:
                mConditionFlag = 2;
                mConditionValue = ENUM_CONDITIOM_TIRED;
                break;
            case R.id.condition_squareimage_condition_beated:
                mConditionFlag = 3;
                mConditionValue = ENUM_CONDITIOM_BEATED;
                break;
            default:
                mConditionFlag = -1;
                mConditionValue = "";
                break;
        }

        view.setBackgroundResource(R.color.IMAGE_BUTTON_SELECT);
    }

    public void onRecordButtonClick(View view){
        ActivityBody body = new ActivityBody();
        Intent intent = getIntent();

        boolean track = intent.getBooleanExtra("TRACK",false);
        body.setTrack(track);

        String sport_type = intent.getStringExtra("SPORT_TYPE");
        if(sport_type!=null){
            body.setSport_type(sport_type);
        }
    }

}
