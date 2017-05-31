package twoAK.runboyrun.fragments.news;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;

public class SportTypeNewsPanelFragment extends Fragment {

    static final String ENUM_SPORT_RUNNING = "RUNNING";
    static final String ENUM_SPORT_CYCLING = "CYCLING";
    static final String ENUM_SPORT_WALKING = "WALKING";
    static final String ENUM_SPORT_SKIRUN = "SKIRUN";

    private View rootView;

    private SquareImageView mSportTypeImage;
    private TextView mSportTypeText;
    private TextView mPointsText;

    private String mSportType;
    private int mPoints;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_news_sporttype_panel, container, false);

        mSportTypeImage = (SquareImageView) rootView.findViewById(R.id.sporttype_panel_fragment_squareimage_sporttype);
        mSportTypeText = (TextView) rootView.findViewById(R.id.sporttype_panel_fragment_text_sport_value);
        mPointsText = (TextView) rootView.findViewById(R.id.sporttype_panel_fragment_text_points);


        return rootView;

    }

    public void setSportType(String sport_type) {
        mSportType = sport_type;

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
    }

    public void setPoints(int points) {
        mPoints = points;

        mPointsText.setText(""+mPoints);
    }
}