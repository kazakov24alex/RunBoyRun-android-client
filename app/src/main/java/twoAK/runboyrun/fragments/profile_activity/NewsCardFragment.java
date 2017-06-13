package twoAK.runboyrun.fragments.profile_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ActivityPageActivity;
import twoAK.runboyrun.fragments.activity_page.LikePanelFragment;
import twoAK.runboyrun.fragments.news.SportTypeNewsPanelFragment;
import twoAK.runboyrun.fragments.news.StatsPanelNewsFragment;
import twoAK.runboyrun.fragments.news.TitleNewsFragment;
import twoAK.runboyrun.responses.objects.NewsObject;


public class NewsCardFragment extends Fragment {
    private View rootView;

    static final String ENUM_SPORT_RUNNING = "RUNNING";
    static final String ENUM_SPORT_CYCLING = "CYCLING";
    static final String ENUM_SPORT_WALKING = "WALKING";
    static final String ENUM_SPORT_SKIRUN  = "SKIRUN";

    private NewsObject mNews;
    private String mDescription;

    private LinearLayout mLayoutActivity;

    private TitleNewsFragment           mTitleNewsFragment;
    private SportTypeNewsPanelFragment  mNewsSportTypePanelFragment;
    private TextView                    mDescriptionText;
    private StatsPanelNewsFragment      mStatsPanelNewsFragment;
    private LikePanelFragment           mLikePanelFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_news_card, container, false);


        // Initialization and filling content of fragments
        FragmentManager fm = getChildFragmentManager();
        mTitleNewsFragment = (TitleNewsFragment) fm.findFragmentById(R.id.newscard_fragment_news_title);
        mTitleNewsFragment.setAthleteID(mNews.getAthlete_id());
        mTitleNewsFragment.setName(mNews.getName(), mNews.getSurname());
        mTitleNewsFragment.setDateTimeStart(mNews.getDatetime_start());

        mNewsSportTypePanelFragment = (SportTypeNewsPanelFragment) fm.findFragmentById(R.id.newscard_fragment_news_sporttype_panel);
        mNewsSportTypePanelFragment.setSportType(mNews.getSport_type());
        mNewsSportTypePanelFragment.setPoints((int) mNews.getDistance()/1); // TODO: mNews.getPoints()

        mDescriptionText = (TextView) rootView.findViewById(R.id.newscard_text_description);
        if(mDescription != null) {
            mDescriptionText.setText(mDescription);
        } else {
            mDescriptionText.setVisibility(View.GONE);
        }

        mStatsPanelNewsFragment = (StatsPanelNewsFragment) fm.findFragmentById(R.id.newscard_fragment_news_stats_panel);
        mStatsPanelNewsFragment.setDuration(mNews.getDuration());
        mStatsPanelNewsFragment.setDistance(mNews.getDistance());

        mLikePanelFragment = (LikePanelFragment) fm.findFragmentById(R.id.newscard_fragment_news_value_panel);
        mLikePanelFragment.setActivityID(mNews.getId());
        mLikePanelFragment.setLikeNum(mNews.getLike_num());
        mLikePanelFragment.setDislikeNum(mNews.getDislike_num());
        mLikePanelFragment.setMyValue(mNews.getMy_value());

        // on click on news content
        mLayoutActivity = (LinearLayout) rootView.findViewById(R.id.newscard_layout_activity);
        mLayoutActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), ActivityPageActivity.class);
                intent.putExtra("ACTIVITY_ID", mNews.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void setContent(NewsObject news) {
        mNews = news;

        mDescription    = news.getDescription();
    }

}
