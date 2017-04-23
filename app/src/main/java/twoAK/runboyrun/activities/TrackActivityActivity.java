package twoAK.runboyrun.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;

import twoAK.runboyrun.R;


public class TrackActivityActivity extends AppCompatActivity implements OnMapReadyCallback, TextToSpeech.OnInitListener {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_activity);
        mTTS = new TextToSpeech(this, this);

        isTracked = false;
        speechIsAvailable = false;

        mAlertDistanceInterval = 0;
        mAlertTimeInterval = 0;
        mAlertTempoInterval = 0;


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.track_activity_mapview_google);
        mapFragment.getMapAsync(this);
        
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
    }

    private void startTracking() {
        if(!checkProvider()) {
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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        mMarkerCurPos = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Start it!"));
        mMarkerCurPos.setVisible(false);

        rectOptions = new PolylineOptions().color(Color.RED).width(10);
        polyline = mGoogleMap.addPolyline(rectOptions);

    }


    @Override
    protected void onResume() {
        System.out.println("RUN-BOY-RUN : ON RESUME!!!");
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener);
        checkProvider();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.track_activity_toast_not_location), Toast.LENGTH_LONG).show();
                return;
            }

            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                // TODO: WHAT FOR
            }

            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                // TODO: WHAT FOR
            }


            // getting current position
            curLat = location.getLatitude();
            curLon = location.getLongitude();
            LatLng myplace = new LatLng(curLat, curLon);

            // marker movement
            mMarkerCurPos.setPosition(myplace);
            mMarkerCurPos.setVisible(true);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myplace, 16));

            if(isTracked) {
                // polyline updating
                if (rectOptions != null) {
                    LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    List<LatLng> points = polyline.getPoints();
                    points.add(myLatLng);
                    polyline.setPoints(points);

                    if (points.size() > 1) {
                        Location locationA = new Location("point A");
                        locationA.setLatitude(points.get(points.size() - 2).latitude);
                        locationA.setLongitude(points.get(points.size() - 2).longitude);

                        System.out.println("RUN-BOY-RUN LAT=" + points.get(points.size() - 2).latitude);
                        System.out.println("RUN-BOY-RUN LON=" + points.get(points.size() - 2).longitude);

                        Location locationB = new Location("point B");
                        locationB.setLatitude(curLat);
                        locationB.setLongitude(curLon);

                        mDistance += locationA.distanceTo(locationB);
                        mDistanceText.setText(String.format("%.2f", mDistance*0.001));

                        Log.i("RUN-BOY-RUN", "[TrackActivity] TRACKED POSITION: lat=" + curLat + " lon=" + curLon + " DISTANCE=" + locationA.distanceTo(locationB));
                    }
                }
            } else {
                Log.i("RUN-BOY-RUN", "[TrackActivity] untracked position: lat="+curLat+" lon="+curLon);
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
            checkProvider();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkProvider();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            checkProvider();
        }
    };


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
}