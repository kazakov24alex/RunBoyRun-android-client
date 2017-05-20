package twoAK.runboyrun.activities;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.GetActivityDataResponse;


public class ActivityPageActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private ActivityPageActivity.GetActivityDataTask mGetActivityDataTask;
    private SendValueTask mSendValueTask;
    private ProgressDialog mProgressDialog; // view of a progress spinner

    private TitleActivityFragment       mTitleActivityFragment;
    private ConditionPanelFragment      mConditionPanelFragment;
    private StatisticsPanelFragment     mStatisticsPanelFragment;
    private DescriptionPanelFragment    mDescriptionPanelFragment;
    private LikePanelFragment           mLikePanelFragment;
    private LastCommentsPanelFragment   mLastCommentsPanelFragment;

    private GetActivityDataResponse mActivityData;



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
//        mDescriptionPanelFragment = (DescriptionPanelFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.activity_page_fragment_description_panel);



        mLikePanelFragment = (LikePanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_like_panel);


        mLastCommentsPanelFragment = (LastCommentsPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_last_comments_panel);
        mLastCommentsPanelFragment.addCommentReview("Usain Bolt", "work out a little more...");
        mLastCommentsPanelFragment.addCommentReview("Leo Messi", "let's go for a run tomorrow morning");
        mLastCommentsPanelFragment.addCommentReview("Andrei Arshavin", "guys, get me jogging...");


        mGetActivityDataTask = new GetActivityDataTask(3);
        mGetActivityDataTask.execute((Void) null);
    }


    public void onLikeClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onLikeClick");
        mLikePanelFragment.onLikeClick();

        mSendValueTask = new SendValueTask(mActivityData.getId(), true);
        mSendValueTask.execute((Void) null);
    }

    public void onListClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onListClick");
    }

    public void onDislikeClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onDislikeClick");
        mLikePanelFragment.onDislikeClick();

        mSendValueTask = new SendValueTask(mActivityData.getId(), false);
        mSendValueTask.execute((Void) null);
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
            hideProgressDialog();

            if(activityData == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT);
                return;
            }

            mActivityData = activityData;

            mTitleActivityFragment.setSportValue(activityData.getSport_type());
            mTitleActivityFragment.setDateTimeStartValue(activityData.getDatetime_start());

            mConditionPanelFragment.setWeather(activityData.getWeather());
            mConditionPanelFragment.setRelief(activityData.getRelief());
            mConditionPanelFragment.setCondition(activityData.getCondition());
            mConditionPanelFragment.setTemp(activityData.getTemperature());

            mStatisticsPanelFragment.setTimeValue(activityData.getDuration());
            mStatisticsPanelFragment.setDistanceValue(activityData.getDistance());
            mStatisticsPanelFragment.setAvrSpeedValue(activityData.getAverage_speed());
            mStatisticsPanelFragment.setTempoValue(activityData.getTempo());

            if(activityData.getDescription()!=null){
                addCommentReviewPanel(activityData.getDescription());
            }

            mLikePanelFragment.setLikeNum(activityData.getLike_num());
            mLikePanelFragment.setDislikeNum(activityData.getDislike_num());
            mLikePanelFragment.setMyValue(activityData.getMy_value());


        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            hideProgressDialog();
        }
    }


    public class SendValueTask extends AsyncTask<Void, Void, Boolean> {
        private String errMes;  // error message possible
        private ValueBody valueBody;

        SendValueTask(int activity_id, boolean value) {
            errMes = null;
            valueBody = new ValueBody();
            valueBody.setActivity_id(activity_id);
            valueBody.setValue(value);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to send value");
            try {
                return ApiClient.instance().sendValue(valueBody);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            if(result == false) {
                Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT);

                mLikePanelFragment.setLikeNum(mActivityData.getLike_num());
                mLikePanelFragment.setDislikeNum(mActivityData.getDislike_num());
                mLikePanelFragment.setMyValue(mActivityData.getMy_value());

                return;
            } else {
                Log.i(APP_TAG, ACTIVITY_TAG + "value was sent");
            }

        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }



    public void addCommentReviewPanel(String description) {
        // получаем экземпляр FragmentTransaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // добавляем фрагмент
        mDescriptionPanelFragment = new DescriptionPanelFragment();
        mDescriptionPanelFragment.setDescriptionValue(description);

        fragmentTransaction.add(R.id.activity_page_container_fragment_description_panel, mDescriptionPanelFragment);
        fragmentTransaction.commit();
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
