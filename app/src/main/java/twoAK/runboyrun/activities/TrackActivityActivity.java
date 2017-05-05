package twoAK.runboyrun.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import twoAK.runboyrun.R;
import twoAK.runboyrun.objects.route.PointTime;
import twoAK.runboyrun.pathsense.PathsenseService;


public class TrackActivityActivity extends AppCompatActivity
        implements OnMapReadyCallback, TextToSpeech.OnInitListener, PathsenseService.Callbacks {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+TrackActivityActivity.class.getName()+"]: ";

    private Context ctx = this;

    private TextToSpeech mTTS;
    private Intent serviceIntent;

    private PathsenseService mPathSenseService;
    private LocationManager locationManager;
    private Runnable mGPSrun;

    private Boolean speechIsAvailable;

    // Activity objects
    private TextView mDistanceText;
    private TextView mTempoText;
    private TextView mProviderStatus;
    private FloatingActionButton mStartButton;
    private LatLng curPosition;

    // Map objects
    private GoogleMap mGoogleMap;
    private Marker mMarkerCurPos;
    private PolylineOptions rectOptions;
    private Polyline mRoutePolyline;
    private List<PointTime> mRoutePointTimeList;

    // activity attributes
    private boolean isTracked;
    private boolean isResult;
    private Chronometer mTrackChronometer;
    private List<LatLng> mRoutePoints;

    private List<Marker> mKmLabelMarkerList;
    private boolean isKmLabelMarkersHidden;


    private int mKmTraveled;
    private float mDistanceMeters;
    private float mTempo;

    // alert intervals
    private int mAlertDistanceInterval;
    private int mAlertTimeInterval;
    private int mAlertTempoInterval;
    private int mAlertDistanceStep;
    private int mAlertTimeStep;
    private int mAlertTempoStep;




    //**************************************************************************************************
//  BINDING ACTIVITY TO PATHSENSE SERVICE
//**************************************************************************************************
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(APP_TAG, ACTIVITY_TAG + "PATHSENSE SERVICE CONNECTED");
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            PathsenseService.LocalBinder binder = (PathsenseService.LocalBinder) service;
            mPathSenseService = binder.getServiceInstance();                // Get instance of your service!
            mPathSenseService.registerClient(TrackActivityActivity.this);   // Activity register in the service as client for callabacks!
            mGPSrun.run();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(APP_TAG, ACTIVITY_TAG + "PATHSENSE SERVICE DISCONNECTED");
        }
    };

    @Override
    public void updateClient(Location newLocation) {
        if(curPosition.latitude == newLocation.getLatitude() && curPosition.longitude == newLocation.getLongitude())
            if(!isTracked || mRoutePointTimeList.size()!=0)
                return;

        setCurrentPositionMarker(newLocation);
        if(isTracked)
            drawNewPointOnTheMap(newLocation);
    }


//**************************************************************************************************
//  ACTIVITY OVERRIDE METHODS
//**************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY WAS CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_activity);

        mTTS = new TextToSpeech(this, this); // Google Voice
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // GPS Manager

        // Pathsense service start
        serviceIntent = new Intent(TrackActivityActivity.this, PathsenseService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!
        startService(serviceIntent);

        // Variable-flags
        isTracked = false;
        isResult = true;
        speechIsAvailable = false;
        isKmLabelMarkersHidden = false;

        // Google Voice time-intervals
        mAlertDistanceInterval  = 0;
        mAlertTimeInterval      = 0;
        mAlertTempoInterval     = 0;

        mDistanceMeters = 0;
        mTempo = 0;
        mKmTraveled = 0;

        mRoutePointTimeList = new ArrayList<PointTime>();
        mKmLabelMarkerList = new ArrayList<Marker>();


        // Google Maps view
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.track_activity_mapview_google);
        mapFragment.getMapAsync(this);

        // View-elements initialization
        mDistanceText = (TextView) findViewById(R.id.track_activity_text_km_number);
        mDistanceText.setText(String.format(Locale.US, "%.2f", mDistanceMeters));
        mTempoText = (TextView) findViewById(R.id.track_activity_text_tempo_number);
        mTempoText.setText("0:00");

        mProviderStatus = (TextView) findViewById(R.id.track_activity_text_provider_status);

        mStartButton = (FloatingActionButton) findViewById(R.id.track_activity_floatbut_start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTracked) {
                    finishTracking();
                } else {
                    startTracking();
                }
            }
        });

        mTrackChronometer = (Chronometer) findViewById(R.id.track_activity_chronometer);
        mTrackChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - mTrackChronometer.getBase();

                makeTimeVoicePrompt(elapsedMillis);
                makeTempoVoicePrompt(elapsedMillis);
                makeDistanceVoicePrompt(elapsedMillis);

                isLocatingAvailable();
                mPathSenseService.getCurrentLocation();
            }
        });


        final Handler h = new Handler();
        mGPSrun = new Runnable() {
            @Override
            public void run() {
                isLocatingAvailable();
                if(mPathSenseService != null && !isTracked) {
                    mPathSenseService.getCurrentLocation();
                }
                h.postDelayed(this, 1000);
            }
        };
    }

    @Override
    protected void onResume() {
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY ON RESUME");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY ON PAUSE");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(APP_TAG, ACTIVITY_TAG + "ACTIVITY WAS DESTROYED");

        // Shutdown Pathsense service
        unbindService(mConnection);
        stopService(serviceIntent);

        // Shutdown Google Voice
        if (mTTS != null) {
            Log.i(APP_TAG, ACTIVITY_TAG + "GOOGLE VOICE SHUTDOWN");
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        if(isTracked) {
            AlertDialog.Builder mNotProviderDialog;
            mNotProviderDialog = new AlertDialog.Builder(ctx);
            mNotProviderDialog.setCancelable(true);
            mNotProviderDialog.setTitle(getString(R.string.track_activity_dialog_close_title));
            mNotProviderDialog.setMessage(getString(R.string.track_activity_dialog_close_message));
            mNotProviderDialog.setPositiveButton(getString(R.string.track_activity_dialog_close_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            finish();
                        }
                    });
            mNotProviderDialog.setNegativeButton(getString(R.string.track_activity_dialog_close_no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            if(isResult) {
                                showFinishActivityDialogs();
                            }
                        }
                    });
            mNotProviderDialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i(APP_TAG, ACTIVITY_TAG + "GOOGLE MAP IS READY");

        mGoogleMap = map;
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        // listener for map zoom (hides KM LABEL MARKERS)
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition = mGoogleMap.getCameraPosition();

                if(cameraPosition.zoom < 12.5 && !isKmLabelMarkersHidden) {
                    isKmLabelMarkersHidden = true;
                    for (int i = 0; i < mKmLabelMarkerList.size(); i++) {
                        mKmLabelMarkerList.get(i).setVisible(false);
                    }
                    return;
                }

                if(cameraPosition.zoom > 12.5 && isKmLabelMarkersHidden) {
                    isKmLabelMarkersHidden = false;
                    for (int i = 0; i < mKmLabelMarkerList.size(); i++) {
                        mKmLabelMarkerList.get(i).setVisible(true);
                    }
                    return;
                }
            }
        });

        // Add primary position marker on map
        curPosition = new LatLng(10, 10);
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_location_layout, null);
        mMarkerCurPos = mGoogleMap.addMarker(new MarkerOptions()
                .position(curPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
        );
        mMarkerCurPos.setVisible(false);

        // Add empty polyline of route on map
        rectOptions = new PolylineOptions()
                .color(getResources().getColor(R.color.ORANGE_custom_marker))
                .width(10);
        mRoutePolyline = mGoogleMap.addPolyline(rectOptions);
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
                Log.e(APP_TAG, ACTIVITY_TAG + "GOOGLE VOICE ERROR: THIS LANGUAGE NOT SUPPORTED");
            } else {
                speechIsAvailable = true;
            }

        } else {
            Log.i(APP_TAG, ACTIVITY_TAG + "GOOGLE VOICE ERROR");
        }

    }


//**************************************************************************************************
//  ACTIVITY OVERRIDE METHODS
//**************************************************************************************************

    private void startTracking() {
        if(!isLocatingAvailable()) {
            showNotProviderDialog();
        } else {
            showStartActivityDialogs();
        }
    }

    private void finishTracking() {
        mTrackChronometer.stop();
        mStartButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.GREEN_LIGHT)));;
        showFinishActivityDialogs();
    }

    private void showStartActivityDialogs() {
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
                                        .setCancelable(false)
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

                                        startActivity();
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

    private void startActivity() {
        mStartButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.RED_LIGHT)));
        mTTS.speak("We start training!", TextToSpeech.QUEUE_FLUSH, null);

        mDistanceText.setText(String.format("%.2f", mDistanceMeters));

        mAlertDistanceStep = mAlertDistanceInterval;
        mAlertTimeStep = 0;
        mAlertTempoStep = 0;

        isTracked = true;

        mTrackChronometer.setBase(SystemClock.elapsedRealtime());
        mTrackChronometer.start();
    }

    private void drawNewPointOnTheMap(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.track_activity_toast_not_location), Toast.LENGTH_LONG).show();
            return;
        }

        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        int elapsedSeconds = (int) ( (SystemClock.elapsedRealtime() - mTrackChronometer.getBase()) / 1000 );

        // Add new point to polyline of route and to list of route
        List<LatLng> points = mRoutePolyline.getPoints();
        points.add(newLatLng);
        mRoutePolyline.setPoints(points);
        mRoutePointTimeList.add(new PointTime(newLatLng, elapsedSeconds));

        if (points.size() > 1) {
            // distance calculating
            Location locationOld = new Location("LOCATION OLD");
            locationOld.setLatitude(points.get(points.size() - 2).latitude);
            locationOld.setLongitude(points.get(points.size() - 2).longitude);
            mDistanceMeters += location.distanceTo(locationOld);
            mDistanceText.setText(String.format(Locale.US, "%.2f", mDistanceMeters*0.001));

            calculateTempo(elapsedSeconds);

            if(mDistanceMeters *0.001 > mKmTraveled)
                setKmLabelMarker(newLatLng);

        } else {
            setKmLabelMarker(newLatLng);
        }

        Log.i(APP_TAG, ACTIVITY_TAG + "LAT="+newLatLng.latitude+"\tLNG="+newLatLng.longitude+"\tSEC="+elapsedSeconds);
    }

    private void setCurrentPositionMarker(Location newLocation) {
        LatLng position = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        mMarkerCurPos.setPosition(position);
        mMarkerCurPos.setVisible(true);

        if(isLocatingAvailable()) {
            if(curPosition.latitude == 10 && curPosition.longitude == 10) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16.5f));
            }
            /*else {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            }*/
        }

        curPosition = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
    }

    private void setKmLabelMarker(LatLng myLatLng) {
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_distance_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
        numTxt.setText(Integer.toString(mKmTraveled));

        Marker mKmLabelMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(myLatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
        );
        mKmLabelMarker.setVisible(true);

        mKmLabelMarkerList.add(mKmLabelMarker);
        mKmTraveled++;
    }

    private void calculateTempo(long elapsedSeconds) {
        if(mDistanceMeters < 1000)
            return;

        double tempoTime = elapsedSeconds / (mDistanceMeters *0.001);

        String resultTempoText = "";
        if( (tempoTime / 3600) > 1) {
            resultTempoText += String.format("%02d:", tempoTime / 3600);
        }
        resultTempoText += String.format("%02d:%02d", tempoTime / 60 % 60, tempoTime % 60);

        mTempoText.setText(resultTempoText);
    }

    private void showFinishActivityDialogs() {
        isResult = true;

        View resultDialogContent = getLayoutInflater().inflate(R.layout.dialog_result_activity, null);

        final AlertDialog settingActivityDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.track_activity_dialog_setting_title))
                .setView(resultDialogContent)
                .setPositiveButton(getString(R.string.track_activity_dialog_result_button_record),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                isTracked = false;
                                resultProcessing();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.track_activity_dialog_result_button_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //dialog.dismiss();
                                onBackPressed();
                            }
                        }
                )
                .create();
        settingActivityDialog.show();
    }

    private void resultProcessing() {
        finish();
        Intent intent = new Intent(TrackActivityActivity.this, ConditionActivity.class);

        intent.putExtra("ROUTE", (Serializable) mRoutePointTimeList);

        startActivity(intent);

    }

//**************************************************************************************************
//  VOICE PROMPT METHODS
//**************************************************************************************************

    private void makeTimeVoicePrompt(long elapsedMillis) {
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
    }

    private void makeTempoVoicePrompt(long elapsedMillis) {
        if ((mAlertTempoInterval != 0) && (elapsedMillis > mAlertTempoStep)) {
            if(mAlertTempoStep != 0 && mDistanceMeters > 1000) {
                String speakString = "Your pace is ";
                double tempoTime = (elapsedMillis/1000) / (mDistanceMeters *0.001);
                if( (tempoTime / 3600) > 1) {
                    speakString += (tempoTime / 3600) + " hours ";
                }
                speakString += (tempoTime / 60 % 60) + " minutes and ";
                speakString += (tempoTime % 60) + " seconds";

                mTTS.speak(speakString, TextToSpeech.QUEUE_ADD, null);
            }

            mAlertTempoStep += mAlertTempoInterval * (60*1000);
        }
    }

    private void makeDistanceVoicePrompt(long elapsedMillis) {
        if ( (mAlertDistanceInterval != 0) && (mDistanceMeters > mAlertDistanceStep) ) {
            String speakString = "You ran ";
            speakString += (mDistanceMeters / 1000) + " kilometers and ";
            speakString += (mDistanceMeters % 1000) + " meters";

            mTTS.speak(speakString, TextToSpeech.QUEUE_ADD, null);

            Log.i(APP_TAG, ACTIVITY_TAG + speakString);

            mAlertDistanceStep += mAlertDistanceInterval;
        }
    }


//**************************************************************************************************
//  AUXILIARY METHODS
//**************************************************************************************************

    private boolean isLocatingAvailable() {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mProviderStatus.setText(getString(R.string.track_activity_text_provider_status_yes));
            mProviderStatus.setBackgroundResource(R.color.GREEN_LIGHT);
            return true;
        } else {
            mProviderStatus.setText(getString(R.string.track_activity_text_provider_status_no));
            mProviderStatus.setBackgroundResource(R.color.RED_LIGHT);
            return false;
        }

    }

    private void showNotProviderDialog() {
        AlertDialog.Builder mNotProviderDialog;
        mNotProviderDialog = new AlertDialog.Builder(ctx);
        mNotProviderDialog.setCancelable(true);
        mNotProviderDialog.setTitle(getString(R.string.track_activity_dialog_notprovider_title));
        mNotProviderDialog.setMessage(getString(R.string.track_activity_dialog_notprovider_text));
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

    private String getOrdinalFromNumber(int i) {
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

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


}