package twoAK.runboyrun.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.NewsFeedRecyclerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.objects.NewsObject;

public class NewsFeedProfileActivity extends ProfileActivity implements Paginate.Callbacks {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    // Common options
    private static final int GRID_SPAN = 3;
    private static final int THRESHOLD = 4;
    private static final int ITEMS_PER_PAGE = 3;
    private static final boolean ADD_LOADING_ROW = true;
    private static final boolean CUSTOM_LOADING_LIST_ITEM = false;
    protected boolean reverseLayout = false;

    private int mAthleteID;

    private GetNewsPageTask mGetNewsPageTask;

    private Paginate mPaginate;
    private RecyclerView mRecyclerView;
    private NewsFeedRecyclerAdapter mAdapter;

    private boolean mLoading;
    private int mTotalPages;
    private int mLoadedPage;
    private int mAllItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAllItems   = -1;
        mLoadedPage = 0;
        mLoading = false;

        try {
            mAthleteID = getIntent().getExtras().getInt("ATHLETE_ID", 0);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ATHLETE_ID");
            finish();
        }

        LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.recycler_layout, getRecyclerContainer(), true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mGetNewsPageTask = new GetNewsPageTask(mAthleteID, ITEMS_PER_PAGE, 1);
        mGetNewsPageTask.execute((Void)null);

    }

    @Override
    protected void setupPagination(List<NewsObject> newsList) {
        // If RecyclerView was recently bound, unbind
        if (mPaginate != null) {
            mPaginate.unbind();
        }
        //handler.removeCallbacks(fakeCallback);
        mAdapter = new NewsFeedRecyclerAdapter(newsList, getSupportFragmentManager());
        //mLoading = false;

        int layoutOrientation = OrientationHelper.VERTICAL;
        RecyclerView.LayoutManager layoutManager = layoutManager = new LinearLayoutManager(this, layoutOrientation, false);

        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        } else {
            ((StaggeredGridLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mPaginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(THRESHOLD)
                .addLoadingListItem(ADD_LOADING_ROW)
                .setLoadingListItemCreator(CUSTOM_LOADING_LIST_ITEM ? new CustomLoadingListItemCreator() : null)
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return GRID_SPAN;
                    }
                })
                .build();
        mPaginate.setHasMoreDataToLoad(mLoading);
    }

    @Override
    public synchronized void onLoadMore() {

        //if((mLoadedPage < mTotalPages) && (mGetNewsPageTask == null) ) {
            mGetNewsPageTask = new GetNewsPageTask(mAthleteID, ITEMS_PER_PAGE, mLoadedPage + 1);
            mGetNewsPageTask.execute((Void) null);
        //}
    }

    @Override
    public synchronized boolean isLoading() {
        return mLoading; // Return boolean weather data is already mLoading or not
    }

    @Override
    public boolean hasLoadedAllItems() {
        return mLoadedPage == mTotalPages; // If all pages are loaded return true
    }


    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", mAdapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;

        public VH(View itemView) {
            super(itemView);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading_text);
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

            mLoading = true;
        }

        @Override
        protected GetNewsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get news page="+page_num);
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
                    Log.i(APP_TAG, ACTIVITY_TAG + "Mews page="+page_num+" was loaded");
                    mLoading = false;

                    if(mAllItems == -1) {
                        mLoadedPage++;
                        mAllItems = newsResponse.getNews().get(0).getOrder();
                        mTotalPages =  mAllItems / ITEMS_PER_PAGE + 1;
                        setupPagination(newsResponse.getNews());
                    } else {
                        mLoadedPage++;
                        mAdapter.add(newsResponse.getNews());
                    }

                } else {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news are absent");
                }
            }

            showProgressCircle(false);
            mGetNewsPageTask = null;

        }
    }


}