package twoAK.runboyrun.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.NewsCardListAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.GetNewsResponse;

public class NewsFeedActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+NewsFeedActivity.class.getName()+"]: ";


    // Common options;
    private static final int ITEMS_PER_PAGE = 1000;

    private GetNewsFeedPageTask mGetNewsFeedPageTask;
    private GetNewsResponse mNewsResponse;

    private View mFormView;
    private View mProgressView;

    private ListView mListView;

    private FragmentManager mFM;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(2).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_newsfeed_activity);
        View inflated = stub.inflate();

        mProgressView   = findViewById(R.id.newsfeed_activity_loading_progress);
        mFormView       = findViewById(R.id.newsfeed_activity_form);

        mListView = (ListView) findViewById(R.id.newsfeed_activity_recycler_view);

        mFM = getSupportFragmentManager();

        mGetNewsFeedPageTask = new GetNewsFeedPageTask(0, ITEMS_PER_PAGE, 1);
        mGetNewsFeedPageTask.execute((Void)null);

    }

    private void setAdapter() {
        NewsCardListAdapter adapter = new NewsCardListAdapter(getApplicationContext(), mFM, mNewsResponse.getNews());
        mListView.setAdapter(adapter);
    }

    private class GetNewsFeedPageTask extends AsyncTask<Void, Void, GetNewsResponse> {
        private String errMes;  // error message possible
        private int start_id;
        private int news_num;
        private int page_num;

        GetNewsFeedPageTask(int start_id, int news_num, int page_num) {
            errMes = null;
            this.start_id = start_id;
            this.news_num = news_num;
            this.page_num = page_num;

            showProgressCircle(true);
        }

        @Override
        protected GetNewsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get news page="+page_num);
            try {
                return ApiClient.instance().getNewsFeedPage(start_id, news_num, page_num);
            } catch (RequestFailedException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_request_failed);
            } catch (InsuccessfulResponseException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetNewsResponse newsResponse) {
            if (newsResponse == null)   {
                Log.i(APP_TAG, ACTIVITY_TAG + "getting news error: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();

            } else {
                if (newsResponse.getNews() != null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "Mews page="+page_num+" was loaded");

                    mNewsResponse = newsResponse;
                    setAdapter();

                } else {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news are absent");
                }

                showProgressCircle(false);
            }



            mGetNewsFeedPageTask = null;

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
