package twoAK.runboyrun.fragments.activity_page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.activities.RouteActivity;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;


public class RoutePanelFragment extends Fragment implements OnMapReadyCallback {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private static GetRouteTask mGetRouteTask;

    private int mActivityID;
    private boolean interactive;

    private List<List<Double>> mRoute;

    // Map objects
    private GoogleMap mGoogleMap;
    private Marker mMarkerCurPos;

    // Map polyline
    private PolylineOptions rectOptions;
    private Polyline mRoutePolyline;

    private double mDistanceMeters;
    private int mKmTraveled;

    private List<Marker> mKmLabelMarkerList = new ArrayList<Marker>();
    private boolean isKmLabelMarkersHidden = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_route_panel, container, false);

        mDistanceMeters = 0;
        mKmTraveled = 0;

        mGetRouteTask = new GetRouteTask(mActivityID);
        mGetRouteTask.execute((Void) null);

        return rootView;
    }

    public void setActivityID(int activityID) {
        mActivityID = activityID;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    private void setRoute(List<List<Double>> route) {
        mRoute = route;
    }

    private void drawMap() {
        // Google Maps view
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.route_panel_mapview_google);
        mapFragment.getMapAsync(this);
    }

    private void drawRoute() {
        for(int i = 0; i < mRoute.size(); i++) {
            LatLng lat_lng = new LatLng(mRoute.get(i).get(0), mRoute.get(i).get(1));
            drawNewPointOnTheMap(lat_lng);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        Log.i(APP_TAG, ACTIVITY_TAG + "GOOGLE MAP IS READY");

        mGoogleMap = map;
        float zoom = 13.0f;

        if(!interactive) {
            mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Intent intent = new Intent(getContext(), RouteActivity.class);
                    intent.putExtra("ACTIVITY_ID", mActivityID);
                    startActivity(intent);
                }
            });
        }

        LatLng middle = new LatLng(mRoute.get(mRoute.size()/2).get(0), mRoute.get(mRoute.size()/2).get(1));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(middle, zoom));


        // listener for map zoom (hides KM LABEL MARKERS)
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition = mGoogleMap.getCameraPosition();

                if(cameraPosition.zoom < 13.5 && !isKmLabelMarkersHidden) {
                    isKmLabelMarkersHidden = true;
                    for (int i = 0; i < mKmLabelMarkerList.size(); i++) {
                        mKmLabelMarkerList.get(i).setVisible(false);
                    }
                    return;
                }

                if(cameraPosition.zoom > 13.5 && isKmLabelMarkersHidden) {
                    isKmLabelMarkersHidden = false;
                    for (int i = 0; i < mKmLabelMarkerList.size(); i++) {
                        mKmLabelMarkerList.get(i).setVisible(true);
                    }
                    return;
                }
            }
        });

        // add primary position marker on map
        View marker = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_location_layout, null);
        mMarkerCurPos = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(mRoute.get(0).get(0), mRoute.get(0).get(1)))
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), marker)))
        );
        mMarkerCurPos.setVisible(false);

        // Add empty polyline of route on map
        rectOptions = new PolylineOptions()
                .color(getResources().getColor(R.color.ORANGE_custom_marker))
                .width(10);
        mRoutePolyline = mGoogleMap.addPolyline(rectOptions);


        drawRoute();

    }


    private void drawNewPointOnTheMap(LatLng newPoint) {

        // Add new point to polyline of route and to list of route
        List<LatLng> points = mRoutePolyline.getPoints();
        points.add(newPoint);
        mRoutePolyline.setPoints(points);

        // placement of flags to map
        if (points.size() > 1) {
            // distance calculating
            Location locationOld = new Location("LOCATION OLD");
            locationOld.setLatitude(points.get(points.size() - 2).latitude);
            locationOld.setLongitude(points.get(points.size() - 2).longitude);
            Location locationNew = new Location("LOCATION NEW");
            locationNew.setLatitude(newPoint.latitude);
            locationNew.setLongitude(newPoint.longitude);

            mDistanceMeters += locationNew.distanceTo(locationOld);

            if(mDistanceMeters *0.001 > mKmTraveled)
                setKmLabelMarker(newPoint);

        } else {
            setKmLabelMarker(newPoint);
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

    private void setKmLabelMarker(LatLng myLatLng) {
        View marker = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_distance_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
        numTxt.setText(Integer.toString(mKmTraveled));

        Marker mKmLabelMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(myLatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), marker)))
        );
        mKmLabelMarker.setVisible(true);

        mKmLabelMarkerList.add(mKmLabelMarker);
        mKmTraveled++;
    }



    private class GetRouteTask extends AsyncTask<Void, Void, List<List<Double>>> {
        private String errMes;  // error message possible
        private int activity_id;

        GetRouteTask(int activity_id) {
            errMes = null;
            this.activity_id = activity_id;
        }

        @Override
        protected List<List<Double>> doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to send value");
            try {
                return ApiClient.instance().getRoute(activity_id);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<List<Double>> route) {
            if(route == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: " + errMes);
                Toast.makeText(getContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.i(APP_TAG, ACTIVITY_TAG + "route was got");

                setRoute(route);
            }

            drawMap();

            mGetRouteTask = null;
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }

}
