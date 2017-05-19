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
import twoAK.runboyrun.fragments.activity_page.ConditionPanelFragment;
import twoAK.runboyrun.fragments.activity_page.DescriptionPanelFragment;
import twoAK.runboyrun.fragments.activity_page.LastCommentsPanelFragment;
import twoAK.runboyrun.fragments.activity_page.LikePanelFragment;
import twoAK.runboyrun.fragments.activity_page.StatisticsPanelFragment;
import twoAK.runboyrun.fragments.activity_page.TitleActivityFragment;
import twoAK.runboyrun.responses.GetActivityDataResponse;


public class ActivityPageActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private ActivityPageActivity.GetActivityDataTask mGetActivityDataTask;
    private ProgressDialog mProgressDialog; // view of a progress spinner

    private TitleActivityFragment       mTitleActivityFragment;
    private ConditionPanelFragment      mConditionPanelFragment;
    private StatisticsPanelFragment     mStatisticsPanelFragment;
    private DescriptionPanelFragment    mDescriptionPanelFragment;
    private LikePanelFragment           mLikePanelFragment;
    private LastCommentsPanelFragment   mLastCommentsPanelFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(3).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_activity_page);
        View inflated = stub.inflate();

        // Fragments initialization
        mTitleActivityFragment = (TitleActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_title_activity);
        mConditionPanelFragment = (ConditionPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_condition_panel);
        mStatisticsPanelFragment = (StatisticsPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_statistics_panel);
        mDescriptionPanelFragment = (DescriptionPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_description_panel);



        mLikePanelFragment = (LikePanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_like_panel);
        mLikePanelFragment.setLikeValue("142");
        mLikePanelFragment.setLikeSelected(true);
        mLikePanelFragment.setDislikeValue("28");


        mLastCommentsPanelFragment = (LastCommentsPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_last_comments_panel);
        mLastCommentsPanelFragment.addCommentReview("Usain Bolt", "work out a little more...");
        mLastCommentsPanelFragment.addCommentReview("Leo Messi", "let's go for a run tomorrow morning");
        mLastCommentsPanelFragment.addCommentReview("Andrei Arshavin", "guys, get me jogging...");


        mGetActivityDataTask = new GetActivityDataTask(2);
        mGetActivityDataTask.execute((Void) null);
    }


    public void onLikeClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onLikeClick");

        mLikePanelFragment.setLikeSelected(mLikePanelFragment.getLikeState());
    }

    public void onListClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onListClick");
    }

    public void onDislikeClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onDislikeClick");

        mLikePanelFragment.setDislikeSelected(mLikePanelFragment.getDislikeState());
    }



    public class GetActivityDataTask extends AsyncTask<Void, Void, GetActivityDataResponse> {
        private String errMes;  // error message possible
        private int activity_id;

        GetActivityDataTask(int activity_id) {
            errMes = null;
            this.activity_id = activity_id;

            showProgress(getString(R.string.activity_page_dialog_loading_activity_data));
        }

        @Override
        protected GetActivityDataResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get activity data");
            try {
                return ApiClient.instance().getActivityData(activity_id);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetActivityDataResponse activityData) {
            if(activityData == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT);
                return;
            }

            mTitleActivityFragment.setSportValue(activityData.getSport_type());
            mTitleActivityFragment.setDateTimeStartValue(activityData.getDatetime_start());

            mConditionPanelFragment.setWeather(activityData.getWeather());
            mConditionPanelFragment.setRelief(activityData.getRelief());
            mConditionPanelFragment.setCondition(activityData.getCondition());

            mStatisticsPanelFragment.setTimeValue(activityData.getDuration());
            mStatisticsPanelFragment.setDistanceValue(activityData.getDistance());
            mStatisticsPanelFragment.setAvrSpeedValue(activityData.getAverage_speed());
            mStatisticsPanelFragment.setTempoValue(activityData.getTempo());

            mDescriptionPanelFragment.setDescriptionValue(activityData.getDescription());

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
