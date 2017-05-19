package twoAK.runboyrun.fragments.activity_page;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;


public class StatisticsPanelFragment extends Fragment {

    private SquareImageView mTimeImage;
    private SquareImageView mDistanceImage;
    private SquareImageView mAvrSpeedImage;
    private SquareImageView mTempoImage;

    private TextView mTimeValue;
    private TextView mDistanceValue;
    private TextView mAvrSpeedValue;
    private TextView mTempoValue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_statistics_panel, container, false);

        mTimeImage = (SquareImageView) rootView.findViewById(R.id.statistics_panel_image_time);
        mTimeImage.setImageResource(R.drawable.result_activity_panel_time);
        mDistanceImage = (SquareImageView) rootView.findViewById(R.id.statistics_panel_image_distance);
        mDistanceImage.setImageResource(R.drawable.result_activity_panel_distance);
        mAvrSpeedImage = (SquareImageView) rootView.findViewById(R.id.statistics_panel_image_avrspeed);
        mAvrSpeedImage.setImageResource(R.drawable.result_activity_panel_speed);
        mTempoImage = (SquareImageView) rootView.findViewById(R.id.statistics_panel_image_tempo);
        mTempoImage.setImageResource(R.drawable.result_activity_panel_tempo);

        mTimeValue = (TextView) rootView.findViewById(R.id.statistics_panel_text_time_value);
        mDistanceValue = (TextView) rootView.findViewById(R.id.statistics_panel_text_distance_value);
        mAvrSpeedValue = (TextView) rootView.findViewById(R.id.statistics_panel_text_avrspeed_value);
        mTempoValue = (TextView) rootView.findViewById(R.id.statistics_panel_text_tempo_value);


        return rootView;
    }

      public void setTimeValue(String time) {
        mTimeValue.setText(time);
    }

    public void setDistanceValue(double distance) {
        mDistanceValue.setText(String.format(Locale.US, "%.2f", distance));
    }

    public void setAvrSpeedValue(double speed) {
        mAvrSpeedValue.setText(String.format(Locale.US, "%.2f", speed));
    }

    public void setTempoValue(double tempo) {
        mTempoValue.setText(String.format(Locale.US, "%.2f", tempo));
    }

}
