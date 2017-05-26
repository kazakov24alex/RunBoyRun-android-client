
package twoAK.runboyrun.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
import twoAK.runboyrun.fragments.profile_activity.ButtonPanelFragment;
import twoAK.runboyrun.fragments.profile_activity.NewsCardFragment;
import twoAK.runboyrun.fragments.profile_activity.ProfilePanelFragment;
import twoAK.runboyrun.interfaces.OnLoadMoreListener;
import twoAK.runboyrun.responses.GetProfileResponse;
import twoAK.runboyrun.responses.objects.NewsObject;


public class PersonalPageRecyclerAdapter extends RecyclerView.Adapter {
    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "[" + ConditionActivity.class.getName() + "]: ";

    private final int VIEW_PROFILE      = 0;
    private final int VIEW_PROGRESS_BAR = 1;
    private final int VIEW_NEWS         = 2;

    private boolean isProfileLoaded;


    private List<NewsObject> mNewsList;
    private GetProfileResponse mProfile;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;



    public PersonalPageRecyclerAdapter(List<NewsObject> news, GetProfileResponse profileResponse, RecyclerView recyclerView) {
        isProfileLoaded = false;

        mNewsList = news;
        mProfile = profileResponse;

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
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
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
        Log.i(APP_TAG, ACTIVITY_TAG + "GET ITEM TYPE");
        if (mNewsList.get(position) == null) {
            if(!isProfileLoaded) {
                isProfileLoaded = true;
                return VIEW_PROFILE;
            } else {
                return VIEW_PROGRESS_BAR;
            }
        } else {
            return VIEW_NEWS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        View view;

        Log.i(APP_TAG, ACTIVITY_TAG + "OPAPA!");

        switch (viewType) {
            case VIEW_PROFILE:
                Log.i(APP_TAG, ACTIVITY_TAG + "PROFILE VIEW LOADED");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_panel, parent, false);
                viewHolder = new ProfilePageHeaderHolder(view);
                return viewHolder;

            case VIEW_NEWS:
                Log.i(APP_TAG, ACTIVITY_TAG + "NEWS VIEW LOADED");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newsfeed_list, parent, false);
                viewHolder = new NewsHolder(view);
                return viewHolder;

            case VIEW_PROGRESS_BAR:
                Log.i(APP_TAG, ACTIVITY_TAG + "PROGRESS VIEW LOADED");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
                viewHolder = new ProgressViewHolder(view);
                return viewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProfilePageHeaderHolder) {
            ((ProfilePageHeaderHolder) holder).setProfile(mProfile);
        }

        if (holder instanceof NewsHolder) {
            NewsObject news = (NewsObject) mNewsList.get(position);
            ((NewsHolder) holder).setNewsObject(news);
        }

        if (holder instanceof ProgressViewHolder) {
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

        public NewsHolder(View view) {
            super(view);
            mView = view;

            fm = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
            mNewsCardFragment = (NewsCardFragment) fm.findFragmentById(R.id.item_news_feed_cardview);

        }

        public void setNewsObject(NewsObject newsObject) {
            this.newsObject = newsObject;

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


    public static class ProfilePageHeaderHolder extends RecyclerView.ViewHolder {
        FragmentManager fm;
        public ProfilePanelFragment profilePanelFragment;
        public ButtonPanelFragment buttonPanelFragment;

        public ProfilePageHeaderHolder(View view) {
            super(view);

            fm = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();

            profilePanelFragment = (ProfilePanelFragment) fm.findFragmentById(R.id.activity_page_recycleritem_profile_panel_fragment);
            buttonPanelFragment = (ButtonPanelFragment) fm.findFragmentById(R.id.iactivity_page_recycleritem_buttons_panel_fragment);
        }

        public void setProfile(GetProfileResponse profile) {
            profilePanelFragment.setProfileData(profile);
        }
    }

}