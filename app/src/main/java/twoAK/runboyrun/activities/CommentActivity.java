package twoAK.runboyrun.activities;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CommentRecycleAdapter;
import twoAK.runboyrun.adapters.CustomDividerItemDecoration;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.GetCommentsResponse;

public class CommentActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    static final int COMMENTS_PER_PAGE = 10;

    private Context context;

    private int mActivityID;

    private GetCommentsPageTask mGetCommentsPageTask;

    private RecyclerView mRecyclerView;
    //private CommentAdapter mCommentAdapted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);
        context = this;

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


        mRecyclerView = (RecyclerView) findViewById(R.id.comment_activity_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new CustomDividerItemDecoration(this));


        mGetCommentsPageTask = new GetCommentsPageTask(mActivityID, COMMENTS_PER_PAGE, 1);
        mGetCommentsPageTask.execute((Void) null);

    }



    private class GetCommentsPageTask extends AsyncTask<Void, Void, GetCommentsResponse> {
        private String errMes;  // error message possible
        private int activity_id;
        private int comments_num;
        private int page_num;

        GetCommentsPageTask(int activity_id, int comments_num, int page_num) {
            errMes = null;
            this.activity_id    = activity_id;
            this.comments_num   = comments_num;
            this.page_num       = page_num;

            //mLastCommentsPanelFragment.showProgressCircle(true);
        }

        @Override
        protected GetCommentsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get comments page");
            try {
                return ApiClient.instance().getCommentsPage(activity_id, comments_num, page_num);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_insuccessful);
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
                Log.i(APP_TAG, ACTIVITY_TAG + "COMMENTS SIZE = "+ commentsResponse.getComments().size());

                CommentRecycleAdapter mCommentRecycleAdapter = new CommentRecycleAdapter(commentsResponse.getComments(), context);
                mRecyclerView.setAdapter(mCommentRecycleAdapter);
            }

        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }










}
