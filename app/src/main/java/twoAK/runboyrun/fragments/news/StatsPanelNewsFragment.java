package twoAK.runboyrun.fragments.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;


public class StatsPanelNewsFragment extends Fragment {

    private View rootView;

    private SquareImageView mTimeImage;
    private TextView mDurationValueText;
    private SquareImageView mDistanceImage;
    private TextView mDistanceValueText;
    private String mDurationValue;
    private double mDistanceValue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_news_stats_panel, container, false);

        mTimeImage = (SquareImageView) rootView.findViewById(R.id.news_stats_panel_squareimage_time);
        mTimeImage.setImageResource(R.drawable.result_activity_panel_time);
        mDistanceImage = (SquareImageView) rootView.findViewById(R.id.news_stats_panel_squareimage_distance);
        mDistanceImage.setImageResource(R.drawable.result_activity_panel_distance);

        mDurationValueText = (TextView) rootView.findViewById(R.id.news_stats_panel_text_time_value);
        mDistanceValueText = (TextView) rootView.findViewById(R.id.news_stats_panel_text_distance_value);

        return rootView;
    }


    public void setDuration(String time) {
        mDurationValue = time;
        mDurationValueText.setText(mDurationValue);
    }

    public void setDistance(double distance) {
        mDistanceValue = distance;
        mDistanceValueText.setText(""+ mDistanceValue);
    }
}
