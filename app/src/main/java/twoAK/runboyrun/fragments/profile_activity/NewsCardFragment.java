package twoAK.runboyrun.fragments.profile_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.fragments.activity_page.LikePanelFragment;
import twoAK.runboyrun.responses.objects.NewsObject;


public class NewsCardFragment extends Fragment {
    private View rootView;

    private SquareImageView mAvatarImage;
    private TextView mNameText;
    private TextView mDateTimeText;

    private SquareImageView mSportTypeImage;
    private TextView mSportTypeText;
    private TextView mPointsText;

    private TextView mDescriptionText;

    private SquareImageView mTimeImage;
    private TextView mTimeValueText;
    private SquareImageView mDistanceImage;
    private TextView mDistanceValueText;

    private LikePanelFragment mLikePanelFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_news_card, container, false);

        /*mAvatarImage = (SquareImageView) rootView.findViewById(R.id.newscard_squareimage_avatar);
        mAvatarImage.setImageResource(R.drawable.com_facebook_top_button);*/
        /*mSportTypeImage = (SquareImageView) rootView.findViewById(R.id.newscard_squareimage_sporttype);
        mSportTypeImage.setImageResource(R.drawable.com_facebook_top_button);
        mTimeImage = (SquareImageView) rootView.findViewById(R.id.newscard_squareimage_time);
        mTimeImage.setImageResource(R.drawable.com_facebook_top_button);
        mDistanceImage = (SquareImageView) rootView.findViewById(R.id.newscard_squareimage_distance);
        mDistanceImage.setImageResource(R.drawable.com_facebook_top_button);

        mNameText = (TextView) rootView.findViewById(R.id.newscard_text_name_surname);
        mDateTimeText = (TextView) rootView.findViewById(R.id.newscard_text_datetime_start);
        mSportTypeText = (TextView) rootView.findViewById(R.id.newscard_text_sport_value);
        mPointsText = (TextView) rootView.findViewById(R.id.newscard_text_points);
        mDescriptionText = (TextView) rootView.findViewById(R.id.newscard_text_description);
        mTimeValueText = (TextView) rootView.findViewById(R.id.newscard_text_time_value);
        mDistanceValueText = (TextView) rootView.findViewById(R.id.newscard_text_distance_value);*/

        return rootView;
    }

    public void setContent(NewsObject news) {
        /*mNameText.setText(news.getName()+" "+news.getSurname());
        mDateTimeText.setText(news.getDatetime_start());
        mSportTypeText.setText(news.getSport_type());
        mPointsText.setText("+10 pts");
        mDescriptionText.setText(news.getDescription());
        mTimeValueText.setText(news.getDuration());
        mDistanceValueText.setText(""+news.getDistance());
*/

    }

}
