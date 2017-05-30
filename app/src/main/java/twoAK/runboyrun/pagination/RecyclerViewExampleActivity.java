package twoAK.runboyrun.pagination;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.pagination.adapter.RecyclerPersonAdapter;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.objects.NewsObject;

public class RecyclerViewExampleActivity extends BasisActivity implements Paginate.Callbacks {

    private static final int GRID_SPAN = 3;

    private int NEWS_PER_PAGE = 3;

    private GetNewsPageTask mGetNewsPageTask;

    private RecyclerView recyclerView;
    private RecyclerPersonAdapter adapter;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;


    // Common options
    protected int threshold = 4;
    protected int totalPages = 3;
    protected int itemsPerPage = 5;
    protected int allItems = -1;
    protected long networkDelay = 5000;
    protected boolean addLoadingRow = true;
    protected boolean customLoadingListItem = false;
    protected boolean reverseLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.recycler_layout, getRecyclerContainer(), true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        handler = new Handler();


        mGetNewsPageTask = new GetNewsPageTask(1, NEWS_PER_PAGE, 1);
        mGetNewsPageTask.execute((Void)null);



    }

    @Override
    protected void setupPagination(List<NewsObject> newsList) {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate.unbind();
        }
        //handler.removeCallbacks(fakeCallback);
        adapter = new RecyclerPersonAdapter(newsList, getSupportFragmentManager());
        loading = false;
        page = 0;

        int layoutOrientation = OrientationHelper.VERTICAL;
        RecyclerView.LayoutManager layoutManager = layoutManager = new LinearLayoutManager(this, layoutOrientation, false);

        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        } else {
            ((StaggeredGridLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(adapter);

        paginate = Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(threshold)
                .addLoadingListItem(addLoadingRow)
                .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return GRID_SPAN;
                    }
                })
                .build();
    }

    @Override
    public synchronized void onLoadMore() {
        Log.d("Paginate", "onLoadMore");
        loading = true;
        // Fake asynchronous loading that will generate page of random data after some delay
        // handler.postDelayed(fakeCallback, networkDelay);
        // TODO:
        mGetNewsPageTask = new GetNewsPageTask(1, NEWS_PER_PAGE, 1);
        mGetNewsPageTask.execute((Void)null);
    }

    @Override
    public synchronized boolean isLoading() {
        return loading; // Return boolean weather data is already loading or not
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == totalPages; // If all pages are loaded return true
    }

   /* private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {
            page++;
            adapter.add(DataProvider.getRandomData(itemsPerPage));
            loading = false;
        }
    };*/

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
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

                    if(allItems == -1) {
                        allItems = newsResponse.getNews().get(0).getOrder();
                        totalPages =  allItems / NEWS_PER_PAGE + 1;
                        setupPagination(newsResponse.getNews());
                    } else {
                        page++;
                        adapter.add(newsResponse.getNews());
                        loading = false;
                    }

                } else {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news are absent");
                    Toast.makeText(getApplicationContext(), "news are absent", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


}