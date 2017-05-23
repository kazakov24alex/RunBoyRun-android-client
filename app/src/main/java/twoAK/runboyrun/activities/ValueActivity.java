package twoAK.runboyrun.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.ViewPagerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.fragments.activity_page.LikePanelFragment;
import twoAK.runboyrun.fragments.value_activity.LikeListFragment;
import twoAK.runboyrun.responses.GetValuesResponse;
import twoAK.runboyrun.responses.objects.ValueObject;


public class ValueActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private LikeListFragment mLikeListFragment;

    private int mActivityID;

    private GetValuesTask mGatValuesTask;

    private View mFormView;
    private View mProgressView;

    private Toolbar     mToolbar;
    private TabLayout   mTabLayout;
    private ViewPager   mViewPager;

    private ArrayList<ValueObject> mLikeList;
    private ArrayList<ValueObject> mDislikeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_value_activity);
        View inflated = stub.inflate();

        try {
            mActivityID = getIntent().getExtras().getInt("ACTIVITY_ID", -1);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ACTIVITY_ID");
            finish();
        }

        mFormView = (View) findViewById(R.id.value_activity_form);
        mProgressView = (View) findViewById(R.id.value_activity_progress_circle);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        mViewPager = (ViewPager) findViewById(R.id.value_activity_viewpager);

        mTabLayout = (TabLayout) findViewById(R.id.value_activity_tablayout);
        mTabLayout.setupWithViewPager(mViewPager);


        mGatValuesTask = new GetValuesTask(mActivityID);
        mGatValuesTask.execute((Void) null);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mLikeListFragment, "Like");
        adapter.addFragment(new LikePanelFragment(), "Dislike");
        viewPager.setAdapter(adapter);
    }


    public class GetValuesTask extends AsyncTask<Void, Void, GetValuesResponse> {
        private String errMes;  // error message possible
        private int activity_id;

        GetValuesTask(int activity_id) {
            errMes = null;
            this.activity_id = activity_id;

            showProgressCircle(true);
        }

        @Override
        protected GetValuesResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get values");
            try {
                return ApiClient.instance().getValues(activity_id);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.value_activity_error_loading_values_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.value_activity_error_loading_values_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetValuesResponse response) {
            if(response == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "GETTING VALUES ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            } else {
                ArrayList<ValueObject> valuesList = response.getValues();
                mLikeList = new ArrayList<ValueObject>();
                mDislikeList = new ArrayList<ValueObject>();

                for(int i = 0; i < valuesList.size(); i++) {
                    if(valuesList.get(i).getValue() == true) {
                        mLikeList.add(valuesList.get(i));
                    } else {
                        mDislikeList.add(valuesList.get(i));
                    }
                }

                mLikeListFragment = new LikeListFragment();
                mLikeListFragment.setValues(mLikeList);

                if(mViewPager != null) {
                    setupViewPager(mViewPager);
                } else {
                    Log.i(APP_TAG, ACTIVITY_TAG + " ViewPager == null !");
                }

                showProgressCircle(false);
            }

        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }


    /**
     * Shows the progress UI and hides the UI form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgressCircle(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
