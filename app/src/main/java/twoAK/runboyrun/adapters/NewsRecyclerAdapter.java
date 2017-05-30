
package twoAK.runboyrun.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.activities.ProfileActivity;
import twoAK.runboyrun.fragments.profile_activity.NewsCardFragment;
import twoAK.runboyrun.interfaces.OnLoadMoreListener;
import twoAK.runboyrun.responses.objects.NewsObject;


public class NewsRecyclerAdapter extends RecyclerView.Adapter {
    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "[" + ConditionActivity.class.getName() + "]: ";

    private final int VIEW_PROGRESS_BAR = 0;
    private final int VIEW_NEWS         = 1;

    private int ownID = 1;

    private Context mContext;

    private List<NewsObject> mNewsList;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 0;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;


    public NewsRecyclerAdapter(List<NewsObject> news, RecyclerView recyclerView, Context context) {
        mNewsList = news;
        mContext = context;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                Log.i(APP_TAG, ACTIVITY_TAG + "THE END!");
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null && mNewsList.size() != 0) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mNewsList.get(position) != null ? VIEW_NEWS : VIEW_PROGRESS_BAR;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_NEWS) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newsfeed_list, parent, false);
            v.setId(View.generateViewId()*2);
            vh = new NewsHolder(v, parent);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsHolder) {
            NewsObject news = (NewsObject) mNewsList.get(position);
            ((NewsHolder) holder).setNewsObject(news);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    class NewsHolder extends RecyclerView.ViewHolder {
        private FragmentManager fm;
        private NewsCardFragment mNewsCardFragment;

        private NewsObject newsObject;
        private View mView;
        private ViewGroup mParent;

        public NewsHolder(View view, ViewGroup parent) {
            super(view);
            mView = view;
            this.mParent = parent;
        }

        public void setNewsObject(NewsObject newsObject) {
            this.newsObject = newsObject;

            NewsCardFragment mNewsCardFragment = new NewsCardFragment();

            Log.i(APP_TAG, ACTIVITY_TAG + "VIEW_ID=" + mView.getId());
            ((ProfileActivity) mView.getContext()).getSupportFragmentManager().beginTransaction()
                    .add(mView.getId(), mNewsCardFragment).commit();

            mNewsCardFragment.setContent(newsObject);
        }

    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.item_progress_bar);
        }
    }


}