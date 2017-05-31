package twoAK.runboyrun.pagination.adapter;


import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.fragments.profile_activity.NewsCardFragment;
import twoAK.runboyrun.responses.objects.NewsObject;

public class RecyclerPersonAdapter extends RecyclerView.Adapter<RecyclerPersonAdapter.NewsHolder> implements RecyclerOnItemClickListener {

    private final List<NewsObject> data;
    private FragmentManager mFragmentManager;

    public RecyclerPersonAdapter(List<NewsObject> data, FragmentManager fm) {
        this.data = data;
        this.mFragmentManager = fm;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_list_item, parent, false);
        view.setId(View.generateViewId());
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, final int position) {
        NewsObject news = data.get(position);
        holder.setNewsObject(news, mFragmentManager);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onItemClicked(View view, int position) {
        Toast.makeText(view.getContext(), "Clicked position: " + position, Toast.LENGTH_SHORT).show();
        this.data.remove(position);
        notifyItemRemoved(position);
    }

    public void add(List<NewsObject> items) {
        int previousDataSize = this.data.size();
        this.data.addAll(items);
        notifyItemRangeInserted(previousDataSize, items.size());
    }

    public static class NewsHolder extends RecyclerView.ViewHolder {
        private FragmentManager fm;
        private NewsCardFragment mNewsCardFragment;

        private NewsObject newsObject;
        private View mView;
        private ViewGroup mParent;

        public NewsHolder(View view) {
            super(view);
            mView = view;
        }

        public void setNewsObject(NewsObject newsObject, FragmentManager fm) {


            this.newsObject = newsObject;

            NewsCardFragment mNewsCardFragment = new NewsCardFragment();

            fm.beginTransaction().add(mView.getId(), mNewsCardFragment).commit();

            mNewsCardFragment.setContent(newsObject);
        }

    }

}