package twoAK.runboyrun.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CustomDividerItemDecoration;
import twoAK.runboyrun.adapters.PersonalPageRecyclerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.interfaces.OnLoadMoreListener;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.GetProfileResponse;
import twoAK.runboyrun.responses.objects.NewsObject;

public class ProfileActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    static final int NEWS_PER_PAGE = 3;

    private GetProfileInfoTask  mGetProfileTask;
    private GetNewsPageTask     mGetNewsPageTask;

    private RecyclerView mRecyclerView;
    private View mProgressView;

    private int mAthleteID;
    private int mAllNewsNum;
    private int mLastLoadedPage;

    protected Handler mHandler;

    private LinearLayoutManager mLinearLayoutManager;
    private PersonalPageRecyclerAdapter mPersonalPageRecyclerAdapter;

    private List<NewsObject> mNewsList;


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
            mAthleteID = getIntent().getExtras().getInt("ATHLETE_ID", 0);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ATHLETE_ID");
            finish();
        }

        mAllNewsNum = -1;

        mProgressView = findViewById(R.id.profile_activity_loading_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.profile_activity_recycler_view);


        mGetProfileTask = new GetProfileInfoTask(mAthleteID);
        mGetProfileTask.execute((Void) null);
    }


    private void recyclerSetting(GetProfileResponse profileResponse) {

        mHandler = new Handler();
        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new CustomDividerItemDecoration(this));

        mNewsList = new ArrayList<NewsObject>();
        mPersonalPageRecyclerAdapter = new PersonalPageRecyclerAdapter(mNewsList, profileResponse, mRecyclerView);
        mPersonalPageRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(mNewsList.size() < mAllNewsNum) {
                    //add null , so the adapter will check view_type and show progress bar at bottom
                    /*mNewsList.add(null);
                    mPersonalPageRecyclerAdapter.notifyItemInserted(mNewsList.size() - 1);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // remove progress item
                            mNewsList.remove(mNewsList.size() - 1);
                            mPersonalPageRecyclerAdapter.notifyItemRemoved(mNewsList.size());
                            //add items one by one
                            *//*mGetCommentsPageTask = new CommentRecyclerFragment.GetCommentsPageTask(mActivityID, COMMENTS_PER_PAGE, ++mLastLoadedPage);
                            mGetCommentsPageTask.execute((Void) null);
*//*
                        }
                    }, 3000);*/
                }
            }
        });
        mRecyclerView.setAdapter(mPersonalPageRecyclerAdapter);

        // installation "header of activity page" on top of recycler view
        mNewsList.add(null);
        mPersonalPageRecyclerAdapter.notifyItemInserted(mNewsList.size() - 1);

    }

/*    public void onFriendsClick(View view) {
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
    }*/



    public class GetProfileInfoTask extends AsyncTask<Void, Void, GetProfileResponse> {
        private String errMes;  // error message possible
        private int athlete_id;

        GetProfileInfoTask(int athlete_id) {
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

            // setting of recycler view (adaper, scroll listener, etc.
            recyclerSetting(profileResponse);

            mGetNewsPageTask = new GetNewsPageTask(mAthleteID, NEWS_PER_PAGE, 1);
            mGetNewsPageTask.execute((Void)null);
        }

    }


    private class GetNewsPageTask extends AsyncTask<Void, Void, GetNewsResponse> {
        private String errMes;  // error message possible
        private int athlete_id;
        private int news_num;
        private int page_num;

        GetNewsPageTask(int athlete_id, int news_num, int page_num) {
            errMes = null;
            this.athlete_id = athlete_id;
            this.news_num = news_num;
            this.page_num = page_num;
        }

        @Override
        protected GetNewsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get news page");
            try {
                return ApiClient.instance().getNewsPage(athlete_id, news_num, page_num);
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
                finish();

            } else {
                if (newsResponse.getNews() != null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news page was loaded");
                    Toast.makeText(getApplicationContext(), "" + newsResponse.getNews().size(), Toast.LENGTH_SHORT).show();

                    if (mAllNewsNum == -1) {
                        mAllNewsNum = newsResponse.getNews().get(0).getOrder();
                        int pagesNum = mAllNewsNum / NEWS_PER_PAGE + 1;
                    }


                    mNewsList.remove(mNewsList.size() - 1);
                    mPersonalPageRecyclerAdapter.notifyItemRemoved(mNewsList.size());

                    for (int i = 0; i < newsResponse.getNews().size(); i++) {
                        mNewsList.add(newsResponse.getNews().get(i));
                    }

                    mNewsList = newsResponse.getNews();

                    Log.i(APP_TAG, ACTIVITY_TAG + mNewsList.size());
                    mPersonalPageRecyclerAdapter.notifyItemInserted(3);
                    //mPersonalPageRecyclerAdapter.setLoaded();


                } else {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news are absent");
                    Toast.makeText(getApplicationContext(), "news are absent", Toast.LENGTH_SHORT).show();
                }
            }

            showProgressCircle(false);
        }
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

            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
