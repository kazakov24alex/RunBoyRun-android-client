package twoAK.runboyrun.activities;


import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.TimeAdapter;
import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CustomDividerItemDecoration;
import twoAK.runboyrun.adapters.DataAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.fragments.comment_activity.CommentRecyclerFragment;
import twoAK.runboyrun.fragments.profile_activity.ProfilePanelFragment;
import twoAK.runboyrun.interfaces.OnLoadMoreListener;
import twoAK.runboyrun.request.body.CommentBody;
import twoAK.runboyrun.responses.GetCommentsResponse;
import twoAK.runboyrun.responses.objects.CommentObject;

public class CommentActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "[" + ConditionActivity.class.getName() + "]: ";
    private int mActivityID;

    private CommentRecyclerFragment mCommentRecyclerFragment;

    private SendCommentTask mSendCommentTask;
    private EditText mEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(1).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_comment_activity);
        View inflated = stub.inflate();

        try {
            mActivityID = getIntent().getExtras().getInt("ACTIVITY_ID", -1);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ATHLETE_ID");
            finish();
        }
        mCommentRecyclerFragment = (CommentRecyclerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.comment_activity_fragment_recycler);
        mCommentRecyclerFragment.setActivityID(mActivityID);

        mEditText = (EditText)findViewById(R.id.edittext_chatbox);
    }

    public void onSendButtonClick(View view){
        mSendCommentTask = new SendCommentTask(mActivityID, mEditText.getText().toString(), this);
        mSendCommentTask.execute((Void)null);
    }

    private class SendCommentTask extends AsyncTask<Void, Void, Boolean> {
        private String errMes;  // error message possible
        private CommentBody commentBody;
        private Activity activity;

        SendCommentTask(int activity_id, String message, Activity activity) {
            errMes = null;
            this.activity = activity;
            commentBody = new CommentBody();
            commentBody.setActivity_id(activity_id);
            commentBody.setText(message);

            //mLastCommentsPanelFragment.showProgressCircle(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get comments page");
            try {
                return ApiClient.instance().sendComment(commentBody);
            } catch (RequestFailedException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_request_failed);
            } catch (InsuccessfulResponseException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            if (result == false) {
                Log.i(APP_TAG, ACTIVITY_TAG + "GETTING COMMENTS ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;

            } else {
                activity.recreate();
                return;
            }
        }
    }
    }


    