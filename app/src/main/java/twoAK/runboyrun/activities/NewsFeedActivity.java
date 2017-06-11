package twoAK.runboyrun.activities;


import android.os.AsyncTask;
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
    static final String ACTIVITY_TAG = "["+ActivityPageActivity.class.getName()+"]: ";


    // Common options;
    private static final int ITEMS_PER_PAGE = 1000;

    private GetNewsFeedPageTask mGetNewsFeedPageTask;
    private GetNewsResponse mNewsResponse;

    private ListView mListView;

    private FragmentManager mFM;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(5).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_newsfeed_activity);
        View inflated = stub.inflate();

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
                    Log.i(APP_TAG, ACTIVITY_TAG + "SIZE = "+newsResponse.getNews().size());

                    mNewsResponse = newsResponse;
                    setAdapter();

                } else {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news are absent");
                }
            }



            mGetNewsFeedPageTask = null;

        }
    }
}
