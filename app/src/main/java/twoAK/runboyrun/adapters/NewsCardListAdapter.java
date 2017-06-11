package twoAK.runboyrun.adapters;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.fragments.profile_activity.NewsCardFragment;
import twoAK.runboyrun.responses.objects.NewsObject;


public class NewsCardListAdapter extends BaseAdapter {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private Context ctx;
    private LayoutInflater lInflater;
    private List<NewsObject> newsList;
    private FragmentManager fm;

    public NewsCardListAdapter(Context context, FragmentManager fm, List<NewsObject> newsList) {
        this.newsList = newsList;
        this.fm = fm;
        ctx = context;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public NewsObject getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_news, parent, false);
        }

        NewsObject news = getItem(position);

        NewsCardFragment mNewsCardFragment = (NewsCardFragment) fm.findFragmentById(R.id.fragmentNEWS);
        mNewsCardFragment.setContent(news);

        return view;
    }

}
