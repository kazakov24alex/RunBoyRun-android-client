package twoAK.runboyrun.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.fragments.profile_activity.ButtonPanelFragment;
import twoAK.runboyrun.fragments.profile_activity.ProfilePanelFragment;
import twoAK.runboyrun.responses.GetProfileResponse;
import twoAK.runboyrun.responses.objects.NewsObject;


public abstract class ProfileActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private GetProfileTask  mGetProfileTask;

    static final int NEWS_PER_PAGE = 4;
    private int mAthleteID;
    private String mToolbarTitle;
    private int mAllNewsNum;
    private int mLastLoadedPage;
    private List<NewsObject> mNewsList;

    private View mFormView;
    private View mProgressView;
    private FrameLayout recyclerViewContainer;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private ProfilePanelFragment mProfilePanelFragment;
    private ButtonPanelFragment mButtonPanelFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(1).setChecked(true);
        getActionBarToolbar().setVisibility(View.GONE);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_profile_activity);
        View inflated = stub.inflate();

        initToolbar();

        try {
            mAthleteID = getIntent().getExtras().getInt("ATHLETE_ID", 0);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ATHLETE_ID");
            finish();
        }

        mNewsList = new ArrayList<NewsObject>();
        mAllNewsNum = -1;
        mLastLoadedPage = 0;

        recyclerViewContainer = (FrameLayout) findViewById(R.id.profile_activity_recycler_container);
        mProgressView   = findViewById(R.id.profile_activity_loading_progress);
        mFormView       = findViewById(R.id.profile_activity_coordinator_layout);

        mProfilePanelFragment = (ProfilePanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profile_activity_fragment_profile);

        /*mButtonPanelFragment = (ButtonPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profile_activity_fragment_buttons);
        mButtonPanelFragment.linkAthleteID(mAthleteID)*/;


        mGetProfileTask = new GetProfileTask(mAthleteID);
        mGetProfileTask.execute((Void) null);
    }


    protected ViewGroup getRecyclerContainer() {
        return recyclerViewContainer;
    }

    protected abstract void setupPagination(List<NewsObject> newsList);

    private void initToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        mToolbarTitle = " ";

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.profile_activity_collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.profile_activity_app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(mToolbarTitle);

                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

    }



    public class GetProfileTask extends AsyncTask<Void, Void, GetProfileResponse> {
        private String errMes;  // error message possible
        private int athlete_id;

        GetProfileTask(int athlete_id) {
            errMes = null;
            this.athlete_id = athlete_id;

            showProgressCircle(true);
        }

        @Override
        protected GetProfileResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get profile data");
            try {
                if(athlete_id == 0) {
                    return ApiClient.instance().getYourProfile();
                } else {
                    return ApiClient.instance().getProfile(athlete_id);
                }
            } catch(RequestFailedException e) {
                errMes = getString(R.string.profile_activity_error_loading_profile_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.profile_activity_error_loading_profile_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetProfileResponse profileResponse) {
            if (profileResponse == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "Getting profile data error: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.i(APP_TAG, ACTIVITY_TAG + "profile data was loaded");
            Toast.makeText(getApplicationContext(), "profile data was loaded", Toast.LENGTH_SHORT).show();

            mProfilePanelFragment.setProfileData(profileResponse);
            mToolbarTitle = profileResponse.getName() + " " + profileResponse.getSurname();

        }

    }


    /** Shows the progress UI and hides the UI form. */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgressCircle(final boolean show) {
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
