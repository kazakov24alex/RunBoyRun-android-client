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

import twoAK.runboyrun.R;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.fragments.subscribers_activity.SubscribersListFragment;
import twoAK.runboyrun.responses.GetSubscribersResponse;

public class SubscribersActivity extends BaseActivity{


    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private SubscribersListFragment mSubscribersListFragment;

    private int mAthleteID;

    private GetSubscribersTask mGetSubscribersTask;

    private View mFormView;
    private View mProgressView;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_subscribers_activity);
        View inflated = stub.inflate();

        try {
            mAthleteID = getIntent().getExtras().getInt("ATHLETE_ID", -1);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ACTIVITY_ID");
            finish();
        }

        mFormView = (View) findViewById(R.id.subscribers_activity_form);
        mProgressView = (View) findViewById(R.id.subscribers_activity_progress_circle);

        mSubscribersListFragment = (SubscribersListFragment) getSupportFragmentManager().findFragmentById(R.id.subscribers_activity_fragment_subscribers_list);

        mGetSubscribersTask = new GetSubscribersTask(2);
        mGetSubscribersTask.execute((Void) null);
    }


    public class GetSubscribersTask extends AsyncTask<Void, Void, GetSubscribersResponse> {
        private String errMes;  // error message possible
        private int athlete_id;

        GetSubscribersTask(int athlete_id) {
            errMes = null;
            this.athlete_id = athlete_id;

            showProgressCircle(true);
        }

        @Override
        protected GetSubscribersResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get values");
            try {
                return ApiClient.instance().getSubscribers(athlete_id);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.value_activity_error_loading_values_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.value_activity_error_loading_values_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetSubscribersResponse response) {
            if(response == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "GETTING VALUES ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.i(APP_TAG, ACTIVITY_TAG + "ID="+athlete_id);
                Log.i(APP_TAG, ACTIVITY_TAG + "SIZE="+response.getSubscribers().size());

                mSubscribersListFragment.setSubscribers(response.getSubscribers());

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

