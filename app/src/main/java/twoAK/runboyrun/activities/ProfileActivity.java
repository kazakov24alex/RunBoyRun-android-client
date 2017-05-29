package twoAK.runboyrun.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.NewsRecyclerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.fragments.profile_activity.ProfilePanelFragment;
import twoAK.runboyrun.interfaces.OnLoadMoreListener;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.GetProfileResponse;
import twoAK.runboyrun.responses.objects.NewsObject;

public class ProfileActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    static final int NEWS_PER_PAGE = 2;

    private GetProfileTask  mGetProfileTask;
    private GetNewsPageTask mGetNewsPageTask;

    private View mFormView;
    private View mProgressView;
    private RecyclerView mRecyclerView;
    private ProfilePanelFragment mProfilePanelFragment;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    protected Handler mHandler;
    private LinearLayoutManager mLinearLayoutManager;
    private NewsRecyclerAdapter mNewsRecyclerAdapter;


    private int mAthleteID;
    private String mToolbarTitle;
    private int mAllNewsNum;
    private int mLastLoadedPage;
    private List<NewsObject> mNewsList;


    private ListView listView;
    private int preLast;


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

        try {
            mAthleteID = getIntent().getExtras().getInt("ATHLETE_ID", 0);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ATHLETE_ID");
            finish();
        }


        mAllNewsNum = -1;
        mLastLoadedPage = 0;

        mProgressView   = findViewById(R.id.profile_activity_loading_progress);
        mFormView       = findViewById(R.id.profile_activity_coordinator_layout);
        mProfilePanelFragment = (ProfilePanelFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profile_activity_fragment_profile);

        initToolbar();
        initRecycler();

        mGetProfileTask = new GetProfileTask(mAthleteID);
        mGetProfileTask.execute((Void) null);
    }

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



    private void initRecycler() {

        mHandler = new Handler();
        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.profile_activity_recycler_view);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mNewsList = new ArrayList<NewsObject>();
        mNewsRecyclerAdapter = new NewsRecyclerAdapter(mNewsList, mRecyclerView, this);
        mNewsRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(APP_TAG, ACTIVITY_TAG + "onLoadMore");
                if (mNewsList.size() < mAllNewsNum) {
                    //add null , so the adapter will check view_type and show progress bar at bottom
                    mNewsList.add(null);
                    mNewsRecyclerAdapter.notifyItemInserted(mNewsList.size() - 1);


                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //   remove progress item
                            mNewsList.remove(mNewsList.size() - 1);
                            mNewsRecyclerAdapter.notifyItemRemoved(mNewsList.size());
                            //add items one by one
                            mGetNewsPageTask = new GetNewsPageTask(mAthleteID, NEWS_PER_PAGE, ++mLastLoadedPage);
                            mGetNewsPageTask.execute((Void) null);

                            //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                        }
                    }, 3000);
                }
            }
        });
        mRecyclerView.setAdapter(mNewsRecyclerAdapter);

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

            //add null , so the adapter will check view_type and show progress bar at bottom
            mNewsList.add(null);
            mNewsRecyclerAdapter.notifyItemInserted(mNewsList.size());
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

            } else {
                if (newsResponse.getNews() != null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news page was loaded");
                    Toast.makeText(getApplicationContext(), "" + newsResponse.getNews().size(), Toast.LENGTH_SHORT).show();

                    if (mAllNewsNum == -1) {
                        //mAllNewsNum = newsResponse.getNews().get(0).getOrder();
                        mAllNewsNum = newsResponse.getNews().get(0).getOrder();
                        int pagesNum = mAllNewsNum / NEWS_PER_PAGE + 1;
                    }

                    //   remove progress item
                    mNewsList.remove(mNewsList.size() - 1);
                    mNewsRecyclerAdapter.notifyItemRemoved(mNewsList.size());

                    for (int i = 0; i < newsResponse.getNews().size(); i++) {
                        mNewsList.add(newsResponse.getNews().get(i));
                    }

                    mNewsRecyclerAdapter.notifyItemInserted(mNewsList.size());
                    mNewsRecyclerAdapter.setLoaded();


                } else {
                    mNewsRecyclerAdapter.notifyItemInserted(mNewsList.size());
                    mNewsRecyclerAdapter.setLoaded();

                    Log.i(APP_TAG, ACTIVITY_TAG + "news are absent");
                    Toast.makeText(getApplicationContext(), "news are absent", Toast.LENGTH_SHORT).show();
                }
            }

            showProgressCircle(false);
        }
    }

    /*public void addCard() {
        // получаем экземпляр FragmentTransaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // добавляем фрагмент
        NewsCardFragment mNewsCardFragment = new NewsCardFragment();

        fragmentTransaction.add(R.id.profile_activity_scroll_view, mNewsCardFragment);
        fragmentTransaction.commit();
    }*/


    /** Shows the progress UI and hides the UI form. */
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

}