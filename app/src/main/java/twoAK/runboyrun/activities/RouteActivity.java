package twoAK.runboyrun.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import twoAK.runboyrun.R;
import twoAK.runboyrun.fragments.activity_page.RoutePanelFragment;


public class RouteActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "[" + ConditionActivity.class.getName() + "]: ";

    private int mActivityID;

    private RoutePanelFragment mRoutePanelFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(3).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_route_activity);
        View inflated = stub.inflate();



        try {
            mActivityID = getIntent().getExtras().getInt("ACTIVITY_ID", -1);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ROUTE");
            finish();
        }

        // получаем экземпляр FragmentTransaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // добавляем фрагмент
        mRoutePanelFragment = new RoutePanelFragment();
        mRoutePanelFragment.setActivityID(mActivityID);
        mRoutePanelFragment.setInteractive(true);

        fragmentTransaction.add(R.id.route_activity_container_fragment_route_panel, mRoutePanelFragment);
        fragmentTransaction.commit();
    }




}
