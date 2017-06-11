package twoAK.runboyrun.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lantouzi.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.SendTrainingInfoFailedException;
import twoAK.runboyrun.objects.route.RoutePoint;
import twoAK.runboyrun.request.body.ActivityBody;
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



    private List<RoutePoint> mRoutePointList;
    private List<RoutePoint> mRouteTimeList;


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

    private EditText mDescriptionText;

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY WAS CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

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
        mWeatherSunnyButton.setImageResource(R.drawable.weather_sunny);
        mWeatherButtonsList.add(mWeatherSunnyButton);
        mWeatherCloudyButton = (SquareImageView) findViewById(R.id.condition_squareimage_weather_cloudy);
        mWeatherCloudyButton.setImageResource(R.drawable.weather_cloudy);
        mWeatherButtonsList.add(mWeatherCloudyButton);
        mWeatherRainyButton = (SquareImageView) findViewById(R.id.condition_squareimage_weather_rainy);
        mWeatherRainyButton.setImageResource(R.drawable.weather_rainy);
        mWeatherButtonsList.add(mWeatherRainyButton);
        mWeatherSnowyButton = (SquareImageView) findViewById(R.id.condition_squareimage_weather_snowy);
        mWeatherSnowyButton.setImageResource(R.drawable.weather_snowy);
        mWeatherButtonsList.add(mWeatherSnowyButton);

        mReliefStadiumButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_stadium);
        mReliefStadiumButton.setImageResource(R.drawable.relief_stadium);
        mReliefButtonsList.add(mReliefStadiumButton);
        mReliefParkButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_park);
        mReliefParkButton.setImageResource(R.drawable.relief_park);
        mReliefButtonsList.add(mReliefParkButton);
        mReliefCrossButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_cross);
        mReliefCrossButton.setImageResource(R.drawable.relief_cross_country);
        mReliefButtonsList.add(mReliefCrossButton);
        mReliefHillsButton = (SquareImageView) findViewById(R.id.condition_squareimage_relief_hills);
        mReliefHillsButton.setImageResource(R.drawable.relief_hills);
        mReliefButtonsList.add(mReliefHillsButton);

        mConditionGoodButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_good);
        mConditionGoodButton.setImageResource(R.drawable.condition_good);
        mConditionButtonsList.add(mConditionGoodButton);
        mConditionMediumButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_medium);
        mConditionMediumButton.setImageResource(R.drawable.condition_medium);
        mConditionButtonsList.add(mConditionMediumButton);
        mConditionTiredButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_tired);
        mConditionTiredButton.setImageResource(R.drawable.condition_tired);
        mConditionButtonsList.add(mConditionTiredButton);
        mConditionBeatedButton = (SquareImageView) findViewById(R.id.condition_squareimage_condition_beated);
        mConditionBeatedButton.setImageResource(R.drawable.condition_beated);
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

        mDescriptionText = (EditText) findViewById(R.id.activity_description);

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
            if (response==null){
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_LONG).show();
                Log.i(APP_TAG,ACTIVITY_TAG + "Sending activity ERROR = "+errMes);
            }
            else{
                Log.i(APP_TAG,ACTIVITY_TAG+"ACTIVITY IS RECORDED!");

                Intent intent = new Intent(getApplicationContext(),ActivityPageActivity.class);
                intent.putExtra("ACTIVITY_ID",response.getActivity_id());
                startActivity(intent);
            }

            hideProgressDialog();
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
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY RECORDING...");

        Intent intent = getIntent();
        ActivityBody body = new ActivityBody();

        String sport_type = intent.getStringExtra("SPORT_TYPE");
        if(sport_type!=null){
            body.setSport_type(sport_type);
            Log.i(APP_TAG, ACTIVITY_TAG + "sport type = "+sport_type);
        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: sport type is absent");
            Toast.makeText(this, "SPORT_TYPE IS ABSENT", Toast.LENGTH_SHORT).show();
            finish();
        }

        boolean track = intent.getBooleanExtra("TRACK", false);
        Log.i(APP_TAG, ACTIVITY_TAG + "track = "+ track);
        body.setTrack(track);

        if(track == true) {
            mRoutePointList = (List<RoutePoint>) intent.getSerializableExtra("ROUTE");
            body.setRoute(mRoutePointList);

            mRouteTimeList = (List<RoutePoint>) intent.getSerializableExtra("TIMELINE");
            body.setTimeline(mRouteTimeList);
        }


        String datetime_start = intent.getStringExtra("START_TIME");
        if(datetime_start!=null){
            body.setDatetime_start(datetime_start);
            Log.i(APP_TAG, ACTIVITY_TAG + "datetime start = "+datetime_start);
        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: datetime_start is absent");
            Toast.makeText(this, "START_TIME IS ABSENT", Toast.LENGTH_SHORT).show();
            finish();
        }

        body.setTemperature(mTemperatureValue);
        Log.i(APP_TAG, ACTIVITY_TAG + "temperature = "+mTemperatureValue);

        if(!mWeatherValue.equals("")){
            Log.i(APP_TAG, ACTIVITY_TAG + "weather = "+mWeatherValue);
            body.setWeather(mWeatherValue);
        } else{
            Toast.makeText(this, getString(R.string.condition_panel_toast_select_condition), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mReliefValue.equals("")) {
            Log.i(APP_TAG, ACTIVITY_TAG + "relief = "+mReliefValue);
            body.setRelief(mReliefValue);
        } else {
            Toast.makeText(this, getString(R.string.condition_panel_toast_select_relief), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mConditionValue.equals("")) {
            Log.i(APP_TAG, ACTIVITY_TAG + "condition = "+mConditionValue);
            body.setCondition(mConditionValue);
        } else {
            Toast.makeText(this, getString(R.string.condition_panel_toast_select_condition), Toast.LENGTH_SHORT).show();
            return;
        }

        String description = mDescriptionText.getText().toString().trim();
        if(!description.equals("")) {
            Log.i(APP_TAG, ACTIVITY_TAG + "description = "+description);
            body.setDescription(description);
        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "description = NULL");
            body.setDescription(null);
        }

        String duration = intent.getStringExtra("DURATION");
        if(duration != null){
            Log.i(APP_TAG, ACTIVITY_TAG + "duration = "+duration);
            body.setDuration(duration);
        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: duration is absent");
            Toast.makeText(this, "DURATION IS ABSENT", Toast.LENGTH_SHORT).show();
            finish();
        }

        double distance = intent.getDoubleExtra("DISTANCE",-1);
        if(distance !=-1 ){
            Log.i(APP_TAG, ACTIVITY_TAG + "distance = " + distance);
            body.setDistance(distance);
        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: distance is absent");
            Toast.makeText(this, "DISTANCE IS ABSENT", Toast.LENGTH_SHORT).show();
            finish();
        }

        double avg_speed =  intent.getDoubleExtra("AVG_SPEED",-1);
        if(avg_speed != -1){
            Log.i(APP_TAG, ACTIVITY_TAG + "average speed = " + avg_speed);
            body.setAverage_speed(avg_speed);
        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: average speed is absent");
            Toast.makeText(this, "AVERAGE SPEED IS ABSENT", Toast.LENGTH_SHORT).show();
            finish();
        }

        double tempo =  intent.getDoubleExtra("TEMPO",-1);
        if(tempo !=- 1){
            Log.i(APP_TAG, ACTIVITY_TAG + "tempo = " + tempo);
            body.setTempo(tempo);
        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: tempo is absent");
            Toast.makeText(this, "TEMPO IS ABSENT", Toast.LENGTH_SHORT).show();
            finish();
        }


        SendTrainingInfoTask mSendTrainingInfoTask = new SendTrainingInfoTask(body);
        mSendTrainingInfoTask.execute((Void) null);
    }

}
