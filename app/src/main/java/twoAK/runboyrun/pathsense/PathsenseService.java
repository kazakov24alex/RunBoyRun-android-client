
package twoAK.runboyrun.pathsense;

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pathsense.android.sdk.location.PathsenseInVehicleLocation;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import twoAK.runboyrun.activities.TrackActivityActivity;


public class PathsenseService extends Service implements LocationListener {
	static final String APP_TAG = "RUN-BOY-RUN";
	static final String ACTIVITY_TAG = "["+PathsenseService.class.getName()+"]: ";

	// Binding service to activity
	private final IBinder mBinder = new LocalBinder();
	Callbacks mActivity;

	// ID of broadcast messages
	static final int MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE 	= 0;
	static final int MESSAGE_ON_GROUND_TRUTH_LOCATION 		= 1;

	// location receviers and handler
	InternalHandler mHandler = new InternalHandler(this);
	InternalGroundTruthLocationUpdateReceiver mGroundTruthLocationUpdateReceiver;
	InternalInVehicleLocationUpdateReceiver mInVehicleLocationUpdateReceiver;

	SharedPreferences mPreferences;
	Location mCurrentLocation;
	double mHeadingGroundTruth; // ???

	PathsenseLocationProviderApi api;
	FusedLocationManager mFusedLocationManager;


//**************************************************************************************************
//  PATHSENSE SERVICE (OVERRIDE METHODS)
//**************************************************************************************************

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(APP_TAG, ACTIVITY_TAG + "PATHSENSE SERVICE WAS CREATED");

		mGroundTruthLocationUpdateReceiver = null;
		mInVehicleLocationUpdateReceiver = null;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(APP_TAG, ACTIVITY_TAG + "PATHSENSE SERVICE WAS STARTED");

		mFusedLocationManager = FusedLocationManager.getInstance(this);
		//mFusedLocationManager.requestLocationUpdate(this);

		api = PathsenseLocationProviderApi.getInstance(this);
		api.requestInVehicleLocationUpdates(PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver.class);

		mPreferences = getSharedPreferences("PathsenseLocationPreferences", MODE_PRIVATE);
		final SharedPreferences preferences = mPreferences;

		if (preferences != null) {
			if (isStarted()) {
				// turn-off switch
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("startedPathsenseFlag", 0);
				editor.commit();
				// stop service
				Intent stopIntent = new Intent(PathsenseService.this, PathsenseInVehicleLocationUpdateRunnerService.class);
				stopIntent.setAction("stop");
				startService(stopIntent);
				// stop updates
				stopUpdates();
			} else {
				// turn-on switch
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("startedPathsenseFlag", 1);
				editor.commit();
				// start service
				Intent startIntent = new Intent(PathsenseService.this, PathsenseInVehicleLocationUpdateRunnerService.class);
				startIntent.setAction("start");
				startService(startIntent);
				// start updates
				startUpdates();
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mPreferences = getSharedPreferences("PathsenseLocationPreferences", MODE_PRIVATE);
		final SharedPreferences preferences = mPreferences;

/*		if (preferences != null) {
			if (!isStarted()) {*/

		// turn-off switch
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("startedFlag", 0);
		editor.commit();
		// stop service
		Intent stopIntent = new Intent(PathsenseService.this, PathsenseInVehicleLocationUpdateRunnerService.class);
		stopIntent.setAction("stop");
		startService(stopIntent);
		// stop updates
		stopUpdates();

		Log.i(APP_TAG, ACTIVITY_TAG + "PATHSENSE SERVICE WAS DESTROYED");
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
		public PathsenseService getServiceInstance(){
			return PathsenseService.this;
		}
	}

	//Here Activity register to the service as Callbacks client
	public void registerClient(TrackActivityActivity activity){
		this.mActivity = (Callbacks)activity;
	}

	//callbacks interface for communication with service clients!
	public interface Callbacks {
		public void updateClient(Location newLocation);
	}

	public void getCurrentLocation(){
		startUpdates();
	}



//**************************************************************************************************
//  LOCATION LISTENER (OVERRIDE METHODS)
//**************************************************************************************************

	class InternalGroundTruthLocationUpdateReceiver extends BroadcastReceiver {
		PathsenseService mPathsenseService;
		//
		InternalGroundTruthLocationUpdateReceiver(PathsenseService service) {
			mPathsenseService = service;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			final PathsenseService service = mPathsenseService;
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

	class InternalInVehicleLocationUpdateReceiver extends BroadcastReceiver {
		PathsenseService mPathsenseService;
		//
		InternalInVehicleLocationUpdateReceiver(PathsenseService service)
		{
			mPathsenseService = service;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			final PathsenseService service = mPathsenseService;
			final InternalHandler handler = service != null ? service.mHandler : null;
			//
			if (service != null && handler != null) {
				PathsenseInVehicleLocation inVehicleLocationUpdate = intent.getParcelableExtra("inVehicleLocation");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE;
				msg.obj = inVehicleLocationUpdate;
				handler.sendMessage(msg);
			}
		}
	}

	class InternalHandler extends Handler {
		PathsenseService mPathsenseService;
		//
		InternalHandler(PathsenseService service)
		{
			mPathsenseService = service;
		}
		@Override
		public void handleMessage(Message msg) {
			final PathsenseService service = mPathsenseService;

			if (service != null) {
				switch (msg.what) {
					case MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE:
					{
						PathsenseInVehicleLocation inVehicleLocation = (PathsenseInVehicleLocation) msg.obj;
						LatLng position = new LatLng(inVehicleLocation.getLatitude(), inVehicleLocation.getLongitude());
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

		if(mActivity != null)
			mActivity.updateClient(location);
	}

	@Override
	public void onProviderDisabled(String s) {
		Log.i(APP_TAG, ACTIVITY_TAG + "PROVIDER DISABLED");
	}

	@Override
	public void onProviderEnabled(String s) {
		Log.i(APP_TAG, ACTIVITY_TAG + "PROVIDER ENABLED");
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		Log.i(APP_TAG, ACTIVITY_TAG + "STATUS CHANGED");
	}


//**************************************************************************************************
//  LOCATING METHODS
//**************************************************************************************************

	boolean isStarted() {
		final SharedPreferences preferences = mPreferences;
		//
		if (preferences != null) {
			return preferences.getInt("startedPathsenseFlag", 0) == 1;
		}

		return false;
	}

	void startUpdates() {
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

		mFusedLocationManager.requestLocationUpdate(this);
	}

	void stopUpdates() {
		// unregister for updates
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

		if (mGroundTruthLocationUpdateReceiver != null) {
			localBroadcastManager.unregisterReceiver(mGroundTruthLocationUpdateReceiver);
		}

		if (mInVehicleLocationUpdateReceiver != null) {
			localBroadcastManager.unregisterReceiver(mInVehicleLocationUpdateReceiver);
		}

		mFusedLocationManager.removeUpdates(this);
	}

}