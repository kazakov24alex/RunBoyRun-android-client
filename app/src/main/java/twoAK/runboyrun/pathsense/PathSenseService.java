/*
 * Copyright (c) 2016 PathSense, Inc.
 */
package twoAK.runboyrun.pathsense;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.pathsense.android.sdk.location.PathsenseInVehicleLocation;

import twoAK.runboyrun.activities.TrackActivityActivity;

public class PathSenseService extends Service implements LocationListener {
	static final String TAG = PathSenseService.class.getName();

	// Messages
	static final int MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE = 0;
	static final int MESSAGE_ON_GROUND_TRUTH_LOCATION = 1;

	double mHeadingGroundTruth;
	double mHeadingRoad;
	int mCreateFlag;
	Location mCurrentLocation;

	InternalGroundTruthLocationUpdateReceiver mGroundTruthLocationUpdateReceiver;
	InternalHandler mHandler = new InternalHandler(this);
	InternalInVehicleLocationUpdateReceiver mInVehicleLocationUpdateReceiver;

	SharedPreferences mPreferences;

	NotificationManager notificationManager;
	NotificationCompat.Builder mBuilder;
	Callbacks activity;
	private final IBinder mBinder = new LocalBinder();
	Handler handler = new Handler();


//**************************************************************************************************
//  PATHSENSE SERVICE (OVERRIDE METHODS)
//**************************************************************************************************

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("RUN-BOY-RUN: SERVICE WAS CREATED");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		System.out.println("RUN-BOY-RUN: SERVICE WAS STARTED");

		mPreferences = getSharedPreferences("PathsenseInVehicleLocationDemoPreferences", MODE_PRIVATE);

		final SharedPreferences preferences = mPreferences;
		//
		if (preferences != null) {
			if (isStarted()) {
				System.out.println("RUN-BOY-RUN: IF");
				// turn-off switch
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("startedFlag", 0);
				editor.commit();
				// stop service
				Intent stopIntent = new Intent(PathSenseService.this, PathsenseInVehicleLocationUpdateRunnerService.class);
				stopIntent.setAction("stop");
				startService(stopIntent);
				// stop updates
				stopUpdates();
			} else {
				System.out.println("RUN-BOY-RUN: ELSE");
				// turn-on switch
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("startedFlag", 1);
				editor.commit();
				// start service
				Intent startIntent = new Intent(PathSenseService.this, PathsenseInVehicleLocationUpdateRunnerService.class);
				startIntent.setAction("start");
				startService(startIntent);
				// start updates
				startUpdates();
			}
		}

		mCreateFlag = 1;

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mPreferences = getSharedPreferences("PathsenseInVehicleLocationDemoPreferences", MODE_PRIVATE);
		final SharedPreferences preferences = mPreferences;

		if (preferences != null) {
			if (!isStarted()) {
				// turn-off switch
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("startedFlag", 0);
				editor.commit();
				// stop service
				Intent stopIntent = new Intent(PathSenseService.this, PathsenseInVehicleLocationUpdateRunnerService.class);
				stopIntent.setAction("stop");
				startService(stopIntent);
				// stop updates
				stopUpdates();
			}
		}

		System.out.println("RUN-BOY-RUN: SERVICE WAS DESTROYED");
	}


//**************************************************************************************************
//  BINDING SERVICE TO ACTIVITY
//**************************************************************************************************

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public PathSenseService getServiceInstance(){
            return PathSenseService.this;
        }
    }

    //Here Activity register to the service as Callbacks client
    public void registerClient(TrackActivityActivity activity){
        this.activity = (Callbacks)activity;
    }

    //callbacks interface for communication with service clients!
    public interface Callbacks{
        public void updateClient(Location newLocation);
    }

    public int getCurrentLocation(){
        return 555;
    }



//**************************************************************************************************
//  LOCATION LISTENER (OVERRIDE METHODS)
//**************************************************************************************************

	static class InternalGroundTruthLocationUpdateReceiver extends BroadcastReceiver {
		PathSenseService mPathSenseService;
		//
		InternalGroundTruthLocationUpdateReceiver(PathSenseService service)
		{
			mPathSenseService = service;
		}
		@Override
		public void onReceive(Context context, Intent intent)
		{
			System.out.println("RUN-BOY-RUN: ON RECEIVE GROUND TRUTH");
			final PathSenseService service = mPathSenseService;
			final InternalHandler handler = service != null ? service.mHandler : null;
			//
			if (service != null && handler != null)
			{
				Location groundTruthLocation = intent.getParcelableExtra("groundTruthLocation");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_GROUND_TRUTH_LOCATION;
				msg.obj = groundTruthLocation;
				handler.sendMessage(msg);
			}
		}
	}

	static class InternalInVehicleLocationUpdateReceiver extends BroadcastReceiver {
		PathSenseService mService;
		//
		InternalInVehicleLocationUpdateReceiver(PathSenseService service)
		{
			mService = service;
		}
		@Override
		public void onReceive(Context context, Intent intent)
		{
			System.out.println("RUN-BOY-RUN: ON RECEIVE IN VEHICLE");
			final PathSenseService service = mService;
			final InternalHandler handler = service != null ? service.mHandler : null;
			//
			if (service != null && handler != null)
			{
				PathsenseInVehicleLocation inVehicleLocationUpdate = intent.getParcelableExtra("inVehicleLocation");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE;
				msg.obj = inVehicleLocationUpdate;
				handler.sendMessage(msg);
			}
		}
	}

	static class InternalHandler extends Handler {
		PathSenseService mPathSenseService;
		//
		InternalHandler(PathSenseService service)
		{
			mPathSenseService = service;
		}
		@Override
		public void handleMessage(Message msg)
		{
			System.out.println("RUN-BOY-RUN: HANDLE MESSAGE");
			final PathSenseService service = mPathSenseService;

			if (service != null)
			{
				switch (msg.what)
				{
					case MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE:
					{
						PathsenseInVehicleLocation inVehicleLocation = (PathsenseInVehicleLocation) msg.obj;
						LatLng position = new LatLng(inVehicleLocation.getLatitude(), inVehicleLocation.getLongitude());
						System.out.println("RUN-BOY-RUN: InternalHandler VEHICLE (Lat="+position.latitude+" Lng="+position.longitude+")");
						break;
					}
					case MESSAGE_ON_GROUND_TRUTH_LOCATION:
					{
						Location groundTruthLocation = (Location) msg.obj;
						float bearing = groundTruthLocation.getBearing();
						if (bearing != 0) {
							service.mHeadingGroundTruth = groundTruthLocation.getBearing();
						}
						LatLng position = new LatLng(groundTruthLocation.getLatitude(), groundTruthLocation.getLongitude());
						System.out.println("RUN-BOY-RUN: InternalHandler GROUND (Lat="+position.latitude+" Lng="+position.longitude+")");
						break;
					}
				}
			}
		}
	}



//**************************************************************************************************
//  LOCATION LISTENER (OVERRIDE METHODS)
//**************************************************************************************************

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
		System.out.println("RUN-BOY-RUN: LOCATION CHANGED");
		activity.updateClient(location);

		if (isStarted())
		{
			startUpdates();
		} else
		{
			stopUpdates();
		}
	}

	@Override
	public void onProviderDisabled(String s) {
		System.out.println("RUN-BOY-RUN: PROVIDER DISABLED");
	}

	@Override
	public void onProviderEnabled(String s) {
		System.out.println("RUN-BOY-RUN: PROVIDER ENABLED");
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		System.out.println("RUN-BOY-RUN: STATUS CHANGED");
	}


//**************************************************************************************************
//  LOCATING METHODS
//**************************************************************************************************

	boolean isStarted() {
		final SharedPreferences preferences = mPreferences;
		//
		if (preferences != null)
		{
			return preferences.getInt("startedFlag", 0) == 1;
		}
		return false;
	}

	void startUpdates() {
		System.out.println("RUN-BOY-RUN: START UPDATES");

		// register for updates
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

		if (mGroundTruthLocationUpdateReceiver == null) {
			mGroundTruthLocationUpdateReceiver = new InternalGroundTruthLocationUpdateReceiver(this);
		}
		localBroadcastManager.registerReceiver(mGroundTruthLocationUpdateReceiver, new IntentFilter("groundTruthLocationUpdate"));
		if (mInVehicleLocationUpdateReceiver == null) {
			mInVehicleLocationUpdateReceiver = new InternalInVehicleLocationUpdateReceiver(this);
		}
		localBroadcastManager.registerReceiver(mInVehicleLocationUpdateReceiver, new IntentFilter("inVehicleLocationUpdate"));
	}

	void stopUpdates() {
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
	}

	double unwrapHeading(double heading1, double heading2) {
		while (heading1 >= heading2 + 180) {
			heading1 -= 360;
		}
		while (heading1 < heading2 - 180) {
			heading1 += 360;
		}
		return heading1;
	}

}
