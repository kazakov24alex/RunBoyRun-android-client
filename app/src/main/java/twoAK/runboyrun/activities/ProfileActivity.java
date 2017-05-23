package twoAK.runboyrun.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.fragments.profile_activity.ButtonPanelFragment;
import twoAK.runboyrun.fragments.profile_activity.ProfilePanelFragment;
import twoAK.runboyrun.responses.GetProfileResponse;

public class ProfileActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private int mAthleteID;

    private ProfilePanelFragment mProfilePanelFragment;
    private ButtonPanelFragment  mButtonPanelFragment;

    private GetProfileInfoTask mGetProfileTask;

    private ProgressDialog mProgressDialog; // view of a progress spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(1).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_profile_activity);
        View inflated = stub.inflate();

        try {
            mAthleteID = getIntent().getExtras().getInt("ATHLETE_ID", -1);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ATHLETE_ID");
            finish();
        }

        mProfilePanelFragment = (ProfilePanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profile_activity_fragment_profile_panel);

        mButtonPanelFragment = (ButtonPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profile_activity_fragment_buttons_panel);


        mGetProfileTask = new GetProfileInfoTask(mAthleteID);
        mGetProfileTask.execute((Void) null);
    }


    public void onFriendsClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onFriendsClick");
    }

    public void onStatsClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onStatsClick");
    }

    public void onRecordsClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onRecordsClick");
    }

    public void onVictoriesClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onVictioriesClick");
    }



    public class GetProfileInfoTask extends AsyncTask<Void, Void, GetProfileResponse> {
        private String errMes;  // error message possible
        private int athlete_id;

        GetProfileInfoTask(int athlete_id) {
            errMes = null;
            this.athlete_id = athlete_id;

            showProgress(getString(R.string.profile_loading_progress_dialog));
        }

        @Override
        protected GetProfileResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get profile data");
            try {
                if(athlete_id == -1) {
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
                Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i(APP_TAG, ACTIVITY_TAG + "profile data was loaded");

            mProfilePanelFragment.setProfileData(profileResponse);

            hideProgressDialog();

        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            hideProgressDialog();
        }
    }

    protected void showProgress(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    /** Hide the progress dialog.*/
    protected void hideProgressDialog() {
        mProgressDialog.dismiss();
    }



}
