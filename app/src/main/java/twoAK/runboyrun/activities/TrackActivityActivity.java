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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import twoAK.runboyrun.R;



public class TrackActivityActivity extends AppCompatActivity implements OnMapReadyCallback {
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 5 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 minutes

    private Context ctx = this;
    private LocationManager mLocationManager;
    
    private GoogleMap mGoogleMap;
    private Marker mMarkerCurPos;
    private PolylineOptions rectOptions;
    private Polyline polyline;
    List<LatLng> routePoints;

    private double lat;
    private double lon;


    private boolean isTracked;

    private TextView mProviderTitle;
    private FloatingActionButton mStartButton;



    public static ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(2);

    private void startThreadOnce() {

        scheduleTaskExecutor.scheduleAtFixedRate(
                new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("THREAD:  LAT="+lat+" LON="+lon);
                    }
                }, 0, 3, TimeUnit.SECONDS); // 0 >> initial delay, 10 >> every x., TimeUnit.SECONDS >> in seconds

    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_activity);
        isTracked = false;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.track_activity_mapview_google);
        mapFragment.getMapAsync(this);
        
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
            showStartDialog();
        }
    }

    private void finishTracking() {

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

        startThreadOnce();
    }


    @Override
    protected void onResume() {
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

            }

            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {

            }

            lat = location.getLatitude();
            lon = location.getLongitude();

            System.out.println("LAT = "+ location.getLatitude());
            System.out.println("LON = "+ location.getLongitude());

            if(rectOptions != null) {
                LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                List<LatLng> points = polyline.getPoints();
                points.add(myLatLng);
                polyline.setPoints(points);
            }


            LatLng myplace = new LatLng(location.getLatitude(), location.getLongitude());

            mMarkerCurPos.setPosition(myplace);
            mMarkerCurPos.setVisible(true);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myplace, 16));

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


    public void showStartDialog() {

        final AlertDialog.Builder startDialog = new AlertDialog.Builder(this);
        startDialog.setIcon(android.R.drawable.btn_star_big_on);
        startDialog.setTitle("Начинаем активность!");
        View dialogContent = getLayoutInflater().inflate(R.layout.dialog_start_activity, null);
        startDialog.setView(dialogContent);

        final TextView counter = (TextView) dialogContent.findViewById(R.id.dialog_start_activity_text_counter);
        final ProgressBar progressBar = (ProgressBar) dialogContent.findViewById(R.id.dialog_start_activity_progressbar_circle);
        startDialog.create();
        startDialog.show();

        new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter.setText(""+millisUntilFinished/1000);;
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
        scheduleTaskExecutor.shutdownNow();
        scheduleTaskExecutor = null;
    }
}