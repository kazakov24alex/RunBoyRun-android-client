package twoAK.runboyrun.fragments.profile_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.fragments.activity_page.LikePanelFragment;
import twoAK.runboyrun.fragments.news.TitleNewsFragment;
import twoAK.runboyrun.responses.objects.NewsObject;


public class NewsCardFragment extends Fragment {

    static final String ENUM_SPORT_RUNNING = "RUNNING";
    static final String ENUM_SPORT_CYCLING = "CYCLING";
    static final String ENUM_SPORT_WALKING = "WALKING";
    static final String ENUM_SPORT_SKIRUN  = "SKIRUN";

    private View rootView;

    private NewsObject mNews;


    private String mSportType;
    private String mPoints = "+10 pts";
    private String mDescription;
    private String mTimeValue;
    private double mDisctanceValue;




    private SquareImageView mSportTypeImage;
    private TextView mSportTypeText;
    private TextView mPointsText;

    private TextView mDescriptionText;

    private SquareImageView mTimeImage;
    private TextView mTimeValueText;
    private SquareImageView mDistanceImage;
    private TextView mDistanceValueText;

    private TitleNewsFragment mTitleNewsFragment;
    private LikePanelFragment mLikePanelFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_news_card, container, false);


        FragmentManager fm = getChildFragmentManager();
        mTitleNewsFragment = (TitleNewsFragment) fm.findFragmentById(R.id.newscard_cardview_title_fragment);
        mTitleNewsFragment.setContent(mNews.getAthlete_id(), mNews.getName(), mNews.getSurname(), mNews.getDatetime_start());

        mLikePanelFragment = (LikePanelFragment) fm.findFragmentById(R.id.newscard_fragment_value_panel);
        mLikePanelFragment.setActivityID(mNews.getId());
        mLikePanelFragment.setLikeNum(mNews.getLike_num());
        mLikePanelFragment.setDislikeNum(mNews.getDislike_num());
        mLikePanelFragment.setMyValue(mNews.getMy_value());

        mTimeImage = (SquareImageView) rootView.findViewById(R.id.newscard_squareimage_time);
        mTimeImage.setImageResource(R.drawable.result_activity_panel_time);
        mDistanceImage = (SquareImageView) rootView.findViewById(R.id.newscard_squareimage_distance);
        mDistanceImage.setImageResource(R.drawable.result_activity_panel_distance);

        mSportTypeImage = (SquareImageView) rootView.findViewById(R.id.newscard_squareimage_sporttype);
        mSportTypeText = (TextView) rootView.findViewById(R.id.newscard_text_sport_value);
        switch(mSportType) {
            case ENUM_SPORT_RUNNING:
                mSportTypeImage.setImageResource(R.drawable.sport_type_running);
                mSportTypeText.setText(getString(R.string.title_activity_text_title_running));
                break;
            case ENUM_SPORT_CYCLING:
                mSportTypeImage.setImageResource(R.drawable.sport_type_cycling);
                mSportTypeText.setText(getString(R.string.title_activity_text_title_cycling));
                break;
            case ENUM_SPORT_WALKING:
                mSportTypeImage.setImageResource(R.drawable.sport_type_walking);
                mSportTypeText.setText(getString(R.string.title_activity_text_title_walking));
                break;
            case ENUM_SPORT_SKIRUN:
                mSportTypeImage.setImageResource(R.drawable.sport_type_skirun);
                mSportTypeText.setText(getString(R.string.title_activity_text_title_skirun));
                break;
            default:
                mSportTypeImage.setImageResource(R.drawable.com_facebook_top_button);
                mSportTypeText.setText("ERROR");
                break;
        }



        mPointsText = (TextView) rootView.findViewById(R.id.newscard_text_points);
        mPointsText.setText(mPoints);
        mDescriptionText = (TextView) rootView.findViewById(R.id.newscard_text_description);
        mDescriptionText.setText(mDescription);
        mTimeValueText = (TextView) rootView.findViewById(R.id.newscard_text_time_value);
        mTimeValueText.setText(mTimeValue);
        mDistanceValueText = (TextView) rootView.findViewById(R.id.newscard_text_distance_value);
        mDistanceValueText.setText(""+mDisctanceValue);

        return rootView;
    }

    public void setContent(NewsObject news) {
        mNews = news;


        mSportType      = news.getSport_type();
        mPoints         = "+10 pts";
        mDescription    = news.getDescription();
        mTimeValue      = news.getDuration();
        mDisctanceValue = news.getDistance();

    }

}
