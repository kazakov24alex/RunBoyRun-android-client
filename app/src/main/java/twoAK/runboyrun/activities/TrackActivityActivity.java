package twoAK.runboyrun.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pathsense.android.sdk.location.PathsenseInVehicleLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import twoAK.runboyrun.R;
import twoAK.runboyrun.pathsense.FusedLocationManager;
import twoAK.runboyrun.pathsense.PathsenseInVehicleLocationUpdateRunnerService;


public class TrackActivityActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, TextToSpeech.OnInitListener {
    private Context ctx = this;

    private TextToSpeech mTTS;
    private Boolean speechIsAvailable;

    // Activity objects
    private TextView mDistanceText;
    private TextView mProviderTitle;
    private FloatingActionButton mStartButton;

    // locationManager objects
    private LocationManager mLocationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // The minimum distance to change Updates in meters
    private static final long MIN_TIME_BW_UPDATES = 0; // The minimum time between updates in milliseconds
    private double curLat;
    private double curLon;

    // map objects
    private GoogleMap mGoogleMap;
    private Marker mMarkerCurPos;
    private PolylineOptions rectOptions;
    private Polyline polyline;

    // activity attributes
    private boolean isTracked;
    private Chronometer mChronometer;
    List<LatLng> mRoutePoints;

    private float mDistance;
    private float mTempo;

    // alert intervals
    private int mAlertDistanceInterval;
    private int mAlertTimeInterval;
    private int mAlertTempoInterval;
    private int mAlertDistanceStep;
    private int mAlertTimeStep;
    private int mAlertTempoStep;

    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************
    static final String TAG = TrackActivityActivity.class.getName();
    // Messages
    static final int MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE = 0;
    static final int MESSAGE_ON_GROUND_TRUTH_LOCATION = 1;
    //
    double mHeadingGroundTruth;
    double mHeadingRoad;
    int mCreateFlag;
    //Button mButtonStart;
    GoogleMap mMap;
    InternalGroundTruthLocationUpdateReceiver mGroundTruthLocationUpdateReceiver;
    InternalHandler mHandler = new InternalHandler(this);
    InternalInVehicleLocationUpdateReceiver mInVehicleLocationUpdateReceiver;
    List<Circle> mInVehiclePointMarkers = new ArrayList<Circle>();
    List<Location> mGroundTruthLocations = new ArrayList<Location>();
    List<PathsenseInVehicleLocation> mInVehicleLocations = new ArrayList<PathsenseInVehicleLocation>();
    Marker mMarkerGroundTruth;
    Marker mMarkerInVehicle;
    Polyline mPolylineGroundTruth;
    Polyline mPolylineInVehicle;
    SharedPreferences mPreferences;

    private ProgressDialog mProgressDialog; // view of a progress spinner
    boolean startLocatingFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_activity);
        mPreferences = getSharedPreferences("PathsenseInVehicleLocationDemoPreferences", MODE_PRIVATE);

        mTTS = new TextToSpeech(this, this);

        isTracked = false;
        speechIsAvailable = false;

        mAlertDistanceInterval = 0;
        mAlertTimeInterval = 0;
        mAlertTempoInterval = 0;

        
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mDistanceText= (TextView) findViewById(R.id.track_activity_text_km_number);
        mDistanceText.setText(String.format("%.2f", mDistance));

        mChronometer = (Chronometer) findViewById(R.id.track_activity_chronometer);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();

                if ( (mAlertTimeInterval != 0) && (elapsedMillis > mAlertTimeStep) ) {
                    int hours = mAlertTimeStep / (1000*60*60);
                    int minutes = (mAlertTimeStep / (1000*60)) % 60;
                    Log.i("RUN-BOY-RUN", "[TrackActivity] TimeAlert: HOURS = "+hours+" MINUTES="+minutes);

                    if(hours == 0) {
                        String speakString = getOrdinalFromNumber(minutes)+" minute of training";
                        mTTS.speak(speakString, TextToSpeech.QUEUE_ADD, null);
                    } else if (minutes != 0){
                        String speakString = " Training lasts "+hours+" hours and "+minutes+" minutes";
                        mTTS.speak(speakString, TextToSpeech.QUEUE_ADD, null);
                    }

                    mAlertTimeStep += mAlertTimeInterval * (60*1000);
                }

                if ( (mAlertTempoInterval != 0) && (elapsedMillis > mAlertTempoStep) ) {
                    float tempo = 2.524f;

                    int integerPart = (int)Math.floor(tempo);
                    int fractionalPart = (int)Math.floor((tempo-integerPart)*10);
                    Log.i("RUN-BOY-RUN", "[TrackActivity] TempoAlert: TEMPO = "+integerPart+"."+fractionalPart);

                    if(mAlertTempoStep != 0) {
                        if (fractionalPart == 0) {
                            String speakString = "You pace is " + integerPart + " kilometers per minute";
                            mTTS.speak(speakString, TextToSpeech.QUEUE_ADD, null);
                        } else {
                            String speakString = "You pace is " + integerPart + "." + fractionalPart + " kilometers per minute";
                            mTTS.speak(speakString, TextToSpeech.QUEUE_ADD, null);
                        }
                    }

                    mAlertTempoStep += mAlertTempoInterval * (60*1000);
                }
            }
        });

        mProviderTitle = (TextView) findViewById(R.id.track_activity_text_provider_title);
        mStartButton = (FloatingActionButton) findViewById(R.id.track_activity_floatbut_start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTracked){
                    finishTracking();
                } else {
                    startTracking();
                }
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.track_activity_mapview_google);
        mapFragment.getMapAsync(this);
        mCreateFlag = 1;

        waitLocating();
    }

    private void waitLocating() {
        startLocatingFlag = true;
    }

    private void startTracking() {
        if(!checkProvider() || startLocatingFlag) {
            showNotProviderDialog();
        } else {
            startActivityTracking();
        }
    }

    private void finishTracking() {
        isTracked = false;
        mChronometer.stop();
        mStartButton.setBackgroundResource(R.color.GREEN_LIGHT);
        Toast.makeText(getApplicationContext(), "SEC="+(SystemClock.elapsedRealtime() - mChronometer.getBase()), Toast.LENGTH_SHORT).show();

        //*********************************************************
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("startedFlag", 0);
        editor.commit();
        // stop service
        Intent stopIntent = new Intent(TrackActivityActivity.this, PathsenseInVehicleLocationUpdateRunnerService.class);
        stopIntent.setAction("stop");
        startService(stopIntent);
        // stop updates
        stopUpdates();
        System.out.println("RUN-BOY-RUN: FINISH TRACKING");

    }


    private boolean checkProvider() {
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mProviderTitle.setText(getString(R.string.track_activity_text_status_gps));
            mProviderTitle.setTextColor(getResources().getColor(R.color.vk_white));
            mProviderTitle.setBackgroundResource(R.color.GPS_available_color);
            return true;
        }
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mProviderTitle.setText(getString(R.string.track_activity_text_status_network));
            mProviderTitle.setTextColor(getResources().getColor(R.color.vk_black));
            mProviderTitle.setBackgroundResource(R.color.NETWORK_available_color);
            return true;

        } else {
            mProviderTitle.setText(getString(R.string.track_activity_text_status_not_provider));
            mProviderTitle.setBackgroundResource(R.color.PROVIDER_not_available_color);
            mProviderTitle.setTextColor(getResources().getColor(R.color.vk_white));
            return false;
        }
    }


    private void showNotProviderDialog() {
        AlertDialog.Builder mNotProviderDialog;
        mNotProviderDialog = new AlertDialog.Builder(ctx);
        mNotProviderDialog.setCancelable(true);
        mNotProviderDialog.setTitle(getString(R.string.track_activity_dialog_notprovider_title));  // заголовок
        mNotProviderDialog.setMessage(getString(R.string.track_activity_dialog_notprovider_text)); // сообщение
        mNotProviderDialog.setPositiveButton(getString(R.string.track_activity_dialog_button_check_settings),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        mNotProviderDialog.setNegativeButton(getString(R.string.track_activity_dialog_button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) { }
                });

        mNotProviderDialog.show();
    }


    public void startActivityTracking() {
        View settingDialogContent = getLayoutInflater().inflate(R.layout.dialog_setting_activity, null);

        Switch mDistanceAlertSwitcher = (Switch) settingDialogContent.findViewById(R.id.dialog_setting_activity_switch_distance);
        Switch mTimeAlertSwitcher = (Switch) settingDialogContent.findViewById(R.id.dialog_setting_activity_switch_time);
        Switch mTempoAlertSwitcher = (Switch) settingDialogContent.findViewById(R.id.dialog_setting_activity_switch_tempo);

        final EditText mDistanceAlertEditText = (EditText) settingDialogContent.findViewById(R.id.dialog_setting_activity_edittext_distance);
        final EditText mTimeAlertEditText = (EditText) settingDialogContent.findViewById(R.id.dialog_setting_activity_edittext_time);
        final EditText mTempoAlertEditText = (EditText) settingDialogContent.findViewById(R.id.dialog_setting_activity_edittext_tempo);

        mDistanceAlertSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDistanceAlertEditText.setEnabled(isChecked);
            }
        });
        mTimeAlertSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTimeAlertEditText.setEnabled(isChecked);
            }
        });
        mTempoAlertSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTempoAlertEditText.setEnabled(isChecked);
            }
        });


        final AlertDialog settingActivityDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.track_activity_dialog_setting_title))
                .setView(settingDialogContent)
                .setPositiveButton(getString(R.string.track_activity_dialog_setting_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if(!mDistanceAlertEditText.getText().toString().equals("")) {
                                    mAlertDistanceInterval = Integer.parseInt(mDistanceAlertEditText.getText().toString());
                                }
                                if(!mTimeAlertEditText.getText().toString().equals("")) {
                                    mAlertTimeInterval = Integer.parseInt(mTimeAlertEditText.getText().toString());
                                }
                                if(!mTempoAlertEditText.getText().toString().equals("")) {
                                    mAlertTempoInterval = Integer.parseInt(mTempoAlertEditText.getText().toString());
                                }

                                View startDialogContent = getLayoutInflater().inflate(R.layout.dialog_start_activity, null);
                                final DonutProgress progressBar = (DonutProgress) startDialogContent.findViewById(R.id.donut_progress);
                                final AlertDialog startActivityDialog = new AlertDialog.Builder(ctx)
                                        .setIcon(android.R.drawable.btn_star_big_on)
                                        .setTitle(getString(R.string.track_activity_dialog_start_activity_title))
                                        .setView(startDialogContent)
                                        .create();
                                startActivityDialog.show();

                                new CountDownTimer(5000, 30) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        progressBar.setText(""+(millisUntilFinished/1000+1));
                                        progressBar.setProgress((int)millisUntilFinished/50);
                                    }

                                    @Override
                                    public void onFinish() {
                                        startActivityDialog.dismiss();
                                        mStartButton.setBackgroundColor(R.color.RED_LIGHT);
                                        mTTS.speak("We start training!", TextToSpeech.QUEUE_FLUSH, null);

                                        mDistance = 0;
                                        mDistanceText.setText(String.format("%.2f", mDistance));
                                        mTempo = 0;

                                        mAlertDistanceStep = 0;
                                        mAlertTimeStep = 0;
                                        mAlertTempoStep = 0;

                                        isTracked = true;

                                        mChronometer.setBase(SystemClock.elapsedRealtime());
                                        mChronometer.start();


                                        // turn-on switch
                                        SharedPreferences.Editor editor = mPreferences.edit();
                                        editor.putInt("startedFlag", 1);
                                        editor.commit();
                                        // start service
                                        Intent startIntent = new Intent(TrackActivityActivity.this, PathsenseInVehicleLocationUpdateRunnerService.class);
                                        startIntent.setAction("start");
                                        startService(startIntent);
                                        // start updates
                                        startUpdates();

                                        System.out.println("RUN-BOY-RUN: START TRACKING");
                                    }
                                }.start();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.track_activity_dialog_setting_negative),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
        settingActivityDialog.show();
    }


    public static String getOrdinalFromNumber(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("en");

            int result = mTTS.setLanguage(locale);
            mTTS.setLanguage(Locale.US);
            //int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                speechIsAvailable = false;
                Log.e("RUN-BOY-RUN", "TTS Извините, этот язык не поддерживается");
            } else {
                speechIsAvailable = true;
            }

        } else {
            Log.e("RUN-BOY-RUN", "TTS Ошибка!");
        }

    }

    @Override
    protected void onDestroy() {
        // Don't forget to shutdown mTTS!
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }


    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************
    // *********************************************************************************************

    static class InternalGroundTruthLocationUpdateReceiver extends BroadcastReceiver
    {
        TrackActivityActivity mActivity;
        //
        InternalGroundTruthLocationUpdateReceiver(TrackActivityActivity activity)
        {
            mActivity = activity;
        }
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final TrackActivityActivity activity = mActivity;
            final InternalHandler handler = activity != null ? activity.mHandler : null;
            //
            if (activity != null && handler != null)
            {
                Location groundTruthLocation = intent.getParcelableExtra("groundTruthLocation");
                Message msg = Message.obtain();
                msg.what = MESSAGE_ON_GROUND_TRUTH_LOCATION;
                msg.obj = groundTruthLocation;
                handler.sendMessage(msg);
            }
        }
    }
    static class InternalHandler extends Handler
    {
        TrackActivityActivity mActivity;
        //
        InternalHandler(TrackActivityActivity activity)
        {
            mActivity = activity;
        }
        @Override
        public void handleMessage(Message msg)
        {
            final TrackActivityActivity activity = mActivity;
            final GoogleMap map = activity != null ? activity.mMap : null;
            //
            if (activity != null && map != null)
            {
                switch (msg.what)
                {
                    case MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE:
                    {
                        PathsenseInVehicleLocation inVehicleLocation = (PathsenseInVehicleLocation) msg.obj;
                        LatLng position = new LatLng(inVehicleLocation.getLatitude(), inVehicleLocation.getLongitude());
                        Marker markerInVehicle = activity.mMarkerInVehicle;
                        if (markerInVehicle == null)
                        {
                            markerInVehicle = map.addMarker((new MarkerOptions()).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_arrow)).anchor(.5f, .5f).position(position).title("PathsenseInVehicle"));
                            activity.mMarkerInVehicle = markerInVehicle;
                        } else
                        {
                            markerInVehicle.setPosition(position);
                        }
                        activity.mHeadingRoad = activity.unwrapHeading(inVehicleLocation.getBearing(), activity.mHeadingGroundTruth);
                        markerInVehicle.setRotation((float) activity.mHeadingRoad + 90);
                        map.moveCamera(CameraUpdateFactory.newLatLng(position));
                        activity.drawPolylineInVehicle(inVehicleLocation);
                        break;
                    }
                    case MESSAGE_ON_GROUND_TRUTH_LOCATION:
                    {
                        final Marker groundTruthMarker = activity.mMarkerGroundTruth;
                        //
                        if (groundTruthMarker != null)
                        {
                            Location groundTruthLocation = (Location) msg.obj;
                            float bearing = groundTruthLocation.getBearing();
                            if (bearing != 0)
                            {
                                activity.mHeadingGroundTruth = groundTruthLocation.getBearing();
                            }
                            groundTruthMarker.setRotation((float) activity.mHeadingGroundTruth - 90);
                            LatLng position = new LatLng(groundTruthLocation.getLatitude(), groundTruthLocation.getLongitude());
                            groundTruthMarker.setPosition(position);
                            //map.moveCamera(CameraUpdateFactory.newLatLng(position));
                            activity.drawPolylineGroundTruth(groundTruthLocation);
                        }
                        break;
                    }
                }
            }
        }
    }
    static class InternalInVehicleLocationUpdateReceiver extends BroadcastReceiver
    {
        TrackActivityActivity mActivity;
        //
        InternalInVehicleLocationUpdateReceiver(TrackActivityActivity activity)
        {
            mActivity = activity;
        }
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final TrackActivityActivity activity = mActivity;
            final InternalHandler handler = activity != null ? activity.mHandler : null;
            //
            if (activity != null && handler != null)
            {
                PathsenseInVehicleLocation inVehicleLocationUpdate = intent.getParcelableExtra("inVehicleLocation");
                Message msg = Message.obtain();
                msg.what = MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE;
                msg.obj = inVehicleLocationUpdate;
                handler.sendMessage(msg);
            }
        }
    }


    @Override
    public void onLocationChanged(Location location)
    {
        System.out.println("RUN-BOY-RUN: LOCATION CHANGED");

        if(startLocatingFlag == true) {
            startLocatingFlag = false;
        }

        final GoogleMap map = mMap;
        final FloatingActionButton mButton = mStartButton;

        //
        if (map != null && mButton != null)
        {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(position).zoom(18).build()));
            // initialize markers
            mMarkerGroundTruth = mMap.addMarker((new MarkerOptions()).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_arrow)).anchor(.5f, .5f).position(position).title("GroundTruth"));
            //
            if (isStarted())
            {
                startUpdates();
            } else
            {
                stopUpdates();
            }
            mButton.setEnabled(true);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        FusedLocationManager.getInstance(this).requestLocationUpdate(this);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        /*//
        if (isStarted())
        {
            stopUpdates();
        }*/
    }
    @Override
    public void onProviderDisabled(String s)
    {
    }
    @Override
    public void onProviderEnabled(String s)
    {
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //
        if (mCreateFlag == 0)
        {
            if (isStarted())
            {
                startUpdates();
            }
        }
        mCreateFlag = 0;
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
    }



    void removePolylineGroundTruth()
    {
        final List<Location> groundTruthLocations = mGroundTruthLocations;
        //
        if (groundTruthLocations != null)
        {
            int numGroundTruthLocations = groundTruthLocations.size();
            if (numGroundTruthLocations > 0)
            {
                for (int i = numGroundTruthLocations - 1; i > -1; i--)
                {
                    groundTruthLocations.remove(i);
                }
            }
            if (mPolylineGroundTruth != null)
            {
                mPolylineGroundTruth.remove();
                mPolylineGroundTruth = null;
            }
        }
    }
    void removePolylineInVehicle()
    {
        final List<PathsenseInVehicleLocation> inVehicleLocations = mInVehicleLocations;
        final List<Circle> inVehiclePointMarkers = mInVehiclePointMarkers;
        //
        if (inVehicleLocations != null && inVehiclePointMarkers != null)
        {
            int numInVehicleLocations = inVehicleLocations.size();
            if (numInVehicleLocations > 0)
            {
                for (int i = numInVehicleLocations - 1; i > -1; i--)
                {
                    inVehicleLocations.remove(i);
                }
            }
            int numInVehiclePointMarkers = inVehiclePointMarkers.size();
            if (numInVehiclePointMarkers > 0)
            {
                for (int i = numInVehiclePointMarkers - 1; i > -1; i--)
                {
                    Circle inVehiclePointMarker = inVehiclePointMarkers.remove(i);
                    inVehiclePointMarker.remove();
                }
            }
            if (mPolylineInVehicle != null)
            {
                mPolylineInVehicle.remove();
                mPolylineInVehicle = null;
            }
        }
    }


    boolean isStarted()
    {
        final SharedPreferences preferences = mPreferences;
        //
        if (preferences != null)
        {
            return preferences.getInt("startedFlag", 0) == 1;
        }
        return false;
    }


    void startUpdates()
    {
        final FloatingActionButton buttonStart = mStartButton;
        //
        if (buttonStart != null)
        {
            // cleanup
            removePolylineInVehicle();
            removePolylineGroundTruth();
            // register for updates
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            if (mGroundTruthLocationUpdateReceiver == null)
            {
                mGroundTruthLocationUpdateReceiver = new InternalGroundTruthLocationUpdateReceiver(this);
            }
            localBroadcastManager.registerReceiver(mGroundTruthLocationUpdateReceiver, new IntentFilter("groundTruthLocationUpdate"));
            if (mInVehicleLocationUpdateReceiver == null)
            {
                mInVehicleLocationUpdateReceiver = new InternalInVehicleLocationUpdateReceiver(this);
            }
            localBroadcastManager.registerReceiver(mInVehicleLocationUpdateReceiver, new IntentFilter("inVehicleLocationUpdate"));
            // set stop button
            //buttonStart.setText("Stop");

            if(mMarkerGroundTruth == null) {
                System.out.println("RUN-BOY-RUN: NULL");
            } else {
                
            }
            System.out.println("RUN-BOY-RUN: START UPDATES");
        }
    }
    void stopUpdates()
    {
        final FloatingActionButton buttonStart = mStartButton;
        //
        if (buttonStart != null)
        {
            // unregister for updates
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            if (mGroundTruthLocationUpdateReceiver != null)
            {
                localBroadcastManager.unregisterReceiver(mGroundTruthLocationUpdateReceiver);
            }
            if (mInVehicleLocationUpdateReceiver != null)
            {
                localBroadcastManager.unregisterReceiver(mInVehicleLocationUpdateReceiver);
            }
            // set start button
            //buttonStart.setText("Start");

            System.out.println("RUN-BOY-RUN: STOP UPDATES");
        }
    }
    double unwrapHeading(double heading1, double heading2)
    {
        while (heading1 >= heading2 + 180)
        {
            heading1 -= 360;
        }
        while (heading1 < heading2 - 180)
        {
            heading1 += 360;
        }
        return heading1;
    }



    void drawPolylineGroundTruth(Location groundTruthLocation)
    {
        final List<Location> groundTruthLocations = mGroundTruthLocations;
        final GoogleMap map = mMap;
        //
        if (groundTruthLocations != null && map != null)
        {
            int numGroundTruthLocations = groundTruthLocations.size();
            if (numGroundTruthLocations > 0)
            {
                long timestamp = System.currentTimeMillis();
                for (int i = numGroundTruthLocations - 1; i > -1; i--)
                {
                    Location q_groundTruthLocation = groundTruthLocations.get(i);
                    if ((timestamp - q_groundTruthLocation.getTime()) > 60000)
                    {
                        groundTruthLocations.remove(i);
                        numGroundTruthLocations--;
                    }
                }
            }
            groundTruthLocations.add(0, groundTruthLocation);
            numGroundTruthLocations++;
            //
            if (numGroundTruthLocations > 1)
            {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.width(15);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
                for (int i = 0; i < numGroundTruthLocations; i++)
                {
                    Location q_groundTruthLocation = groundTruthLocations.get(i);
                    polylineOptions.add(new LatLng(q_groundTruthLocation.getLatitude(), q_groundTruthLocation.getLongitude()));
                }
                if (mPolylineGroundTruth != null)
                {
                    mPolylineGroundTruth.remove();
                }
                mPolylineGroundTruth = map.addPolyline(polylineOptions);
            }
        }
    }
    void drawPolylineInVehicle(PathsenseInVehicleLocation inVehicleLocation)
    {
        final List<PathsenseInVehicleLocation> inVehicleLocations = mInVehicleLocations;
        final List<Circle> inVehiclePointMarkers = mInVehiclePointMarkers;
        final GoogleMap map = mMap;
        //
        if (inVehicleLocations != null && inVehiclePointMarkers != null && map != null)
        {
            int numInVehicleLocations = inVehicleLocations.size();
            if (numInVehicleLocations > 0)
            {
                long timestamp = System.currentTimeMillis();
                for (int i = numInVehicleLocations - 1; i > -1; i--)
                {
                    PathsenseInVehicleLocation q_inVehicleLocation = inVehicleLocations.get(i);
                    if ((timestamp - q_inVehicleLocation.getTime()) > 60000)
                    {
                        inVehicleLocations.remove(i);
                    }
                }
            }
            int numInVehiclePointMarkers = inVehiclePointMarkers.size();
            if (numInVehiclePointMarkers > 0)
            {
                for (int i = numInVehiclePointMarkers - 1; i > -1; i--)
                {
                    Circle inVehiclePointMarker = inVehiclePointMarkers.remove(i);
                    inVehiclePointMarker.remove();
                }
            }
            List<PathsenseInVehicleLocation> points = inVehicleLocation.getPoints();
            int numPoints = points != null ? points.size() : 0;
            if (numPoints > 0)
            {
                for (int i = 0; i < numPoints; i++)
                {
                    PathsenseInVehicleLocation point = points.get(i);
                    inVehicleLocations.add(0, point);
                    //
                    Circle inVehiclePointMarker = map.addCircle((new CircleOptions()).center(new LatLng(point.getLatitude(), point.getLongitude())).fillColor(Color.BLACK).strokeColor(Color.BLACK).strokeWidth(5).radius(5));
                    inVehiclePointMarkers.add(inVehiclePointMarker);
                }
            }
            numInVehicleLocations = inVehicleLocations.size();
            //
            if (numInVehicleLocations > 1)
            {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
                for (int i = 0; i < numInVehicleLocations; i++)
                {
                    PathsenseInVehicleLocation q_inVehicleLocation = inVehicleLocations.get(i);
                    polylineOptions.add(new LatLng(q_inVehicleLocation.getLatitude(), q_inVehicleLocation.getLongitude()));
                }
                if (mPolylineInVehicle != null)
                {
                    mPolylineInVehicle.remove();
                }
                mPolylineInVehicle = map.addPolyline(polylineOptions);
            }
        }
    }


    /** Show the progress dialog.*/
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
}