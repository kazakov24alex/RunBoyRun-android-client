package twoAK.runboyrun.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
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
import twoAK.runboyrun.request.body.CommentBody;
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.GetActivityDataResponse;
import twoAK.runboyrun.responses.GetCommentsResponse;
import twoAK.runboyrun.responses.objects.CommentObject;


public class ActivityPageActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    static final int COMMENTS_NUM_MAX = 3;

    private int mActivityID;

    private GetActivityDataTask mGetActivityDataTask;
    private SendValueTask       mSendValueTask;
    private GetCommentsTask     mGetCommentsTask;
    private SendCommentTask     mSendCommentTask;

    private ProgressDialog mProgressDialog; // view of a progress spinner'

    private View mFormView;
    private View mProgressView;

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

        try {
            mActivityID = getIntent().getExtras().getInt("ACTIVITY_ID", -1);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ACTIVITY_ID");
            finish();
        }

        // progress circle
        mFormView  = findViewById(R.id.activity_page_sclroll_view);
        mProgressView = findViewById(R.id.activity_page_progress_circle);

        // Fragments initialization
        mTitleActivityFragment = (TitleActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_title_activity);

        mConditionPanelFragment = (ConditionPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_condition_panel);

        mStatisticsPanelFragment = (StatisticsPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_statistics_panel);

        mLikePanelFragment = (LikePanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_like_panel);

        mLastCommentsPanelFragment = (LastCommentsPanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_page_fragment_last_comments_panel);


        loadActivityData();
    }


    public void loadActivityData() {
        mGetActivityDataTask = new GetActivityDataTask(mActivityID);
        mGetActivityDataTask.execute((Void) null);
    }

    public void loadComments() {
        mGetCommentsTask = new GetCommentsTask(mActivityID, COMMENTS_NUM_MAX);
        mGetCommentsTask.execute((Void) null);
    }


    public void onLikeClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onLikeClick");
        mLikePanelFragment.onLikeClick();

        mSendValueTask = new SendValueTask(mActivityData.getId(), true);
        mSendValueTask.execute((Void) null);
    }

    public void onListClick(View view) {
        Intent intent = new Intent(ActivityPageActivity.this, ValueActivity.class);
        intent.putExtra("ACTIVITY_ID", mActivityID);
        startActivity(intent);
    }

    public void onDislikeClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onDislikeClick");
        mLikePanelFragment.onDislikeClick();

        mSendValueTask = new SendValueTask(mActivityData.getId(), false);
        mSendValueTask.execute((Void) null);
    }

    public void onWriteCommentButtonClick(View view) {
        View settingDialogContent = getLayoutInflater().inflate(R.layout.dialog_comment_write, null);
        final EditText editText = (EditText) settingDialogContent.findViewById(R.id.dialog_comment_edittext_write_comment);

        final AlertDialog commentWriteDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.activity_page_dialog_comment_title_write_comment))
                .setView(settingDialogContent)
                .setPositiveButton(getString(R.string.activity_page_dialog_comment_button_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                )
                .setNegativeButton(getString(R.string.activity_page_dialog_comment_button_negative),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
        commentWriteDialog.show();


        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        commentWriteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(editText.getText().length() > 0 && editText.getText().length() <= 140) {
                    commentWriteDialog.dismiss();
                    showProgress(getString(R.string.activity_page_dialog_comment_sending_progress_text));

                    mSendCommentTask = new SendCommentTask(mActivityID, editText.getText().toString());
                    mSendCommentTask.execute((Void) null);
                }
            }
        });
    }

    public void onLastCommentFragmentClick(View view) {
        Intent intent = new Intent(ActivityPageActivity.this, CommentActivity.class);
        intent.putExtra("ACTIVITY_ID", mActivityID);
        startActivity(intent);
    }


    private class GetActivityDataTask extends AsyncTask<Void, Void, GetActivityDataResponse> {
        private String errMes;  // error message possible
        private int activity_id;

        GetActivityDataTask(int activity_id) {
            errMes = null;
            this.activity_id = activity_id;

            showProgressCircle(true);
        }

        @Override
        protected GetActivityDataResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get profile data");
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
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i(APP_TAG, ACTIVITY_TAG + "activity data was loaded");

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

            if(activityData.getDescription() != null){
                addCommentReviewPanel(activityData.getDescription());
            }

            mLikePanelFragment.setLikeNum(activityData.getLike_num());
            mLikePanelFragment.setDislikeNum(activityData.getDislike_num());
            mLikePanelFragment.setMyValue(activityData.getMy_value());

            showProgressCircle(false);

            loadComments();
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() {
            showProgressCircle(false);
        }
    }

    private class SendValueTask extends AsyncTask<Void, Void, Boolean> {
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
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();

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

    private class GetCommentsTask extends AsyncTask<Void, Void, GetCommentsResponse> {
        private String errMes;  // error message possible
        private int activity_id;
        private int comments_num;

        GetCommentsTask(int activity_id, int comments_num) {
            errMes = null;
            this.activity_id    = activity_id;
            this.comments_num   = comments_num;

            mLastCommentsPanelFragment.showProgressCircle(true);
        }

        @Override
        protected GetCommentsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get comments preview");
            try {
                return ApiClient.instance().getComments(activity_id, comments_num);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetCommentsResponse commentsResponse) {
            if(commentsResponse == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "GETTING COMMENTS ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            } else {
                if(commentsResponse.getComments() != null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "Comments was got!");
                    mLastCommentsPanelFragment.showWriteCommentButton(false);
                    for (int i = (commentsResponse.getComments().size() - 1); i >= 0; i--) {
                        CommentObject comment = commentsResponse.getComments().get(i);
                        mLastCommentsPanelFragment.addCommentReview(comment.getSurname() + " " + comment.getName(), comment.getText());
                    }
                } else {
                    mLastCommentsPanelFragment.showWriteCommentButton(true);
                }

                mLastCommentsPanelFragment.showProgressCircle(false);
            }

        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }

    private class SendCommentTask extends AsyncTask<Void, Void, Boolean> {
        private String errMes;  // error message possible
        private CommentBody commentBody;

        SendCommentTask(int activity_id, String text) {
            errMes = null;
            commentBody = new CommentBody();
            commentBody.setActivity_id(activity_id);
            commentBody.setText(text);

            mLastCommentsPanelFragment.showProgressCircle(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get comments preview");
            try {
                return ApiClient.instance().sendComment(commentBody);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success == false) {
                Log.i(APP_TAG, ACTIVITY_TAG + "SENDING COMMENT ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            } else {
                hideProgressDialog();

                loadComments();
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


    /** Show the progress dialog.*/
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
