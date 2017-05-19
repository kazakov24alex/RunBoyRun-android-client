package twoAK.runboyrun.fragments.activity_page;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;


public class ConditionPanelFragment extends Fragment {

    private SquareImageView mConditionWeatherImage;
    private SquareImageView mConditionTemperatureImage;
    private SquareImageView mConditionReliefImage;
    private SquareImageView mConditionConditionImage;
    private TextView mConditionWeatherText;
    private TextView mConditionTemperatureText;
    private TextView mConditionReliefText;
    private TextView mConditionConditionText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_condition_panel, container, false);

        mConditionWeatherImage = (SquareImageView) rootView.findViewById(R.id.condition_panel_image_weather);
        mConditionWeatherImage.setImageResource(R.drawable.com_facebook_top_button);
        mConditionTemperatureImage = (SquareImageView) rootView.findViewById(R.id.condition_panel_image_temperature);
        mConditionTemperatureImage.setImageResource(R.drawable.com_facebook_top_button);
        mConditionReliefImage = (SquareImageView) rootView.findViewById(R.id.condition_panel_image_relief);
        mConditionReliefImage.setImageResource(R.drawable.com_facebook_top_button);
        mConditionConditionImage = (SquareImageView) rootView.findViewById(R.id.condition_panel_image_condition);
        mConditionConditionImage.setImageResource(R.drawable.com_facebook_top_button);

        // initialization text of button panel and setting custom font
        Typeface squareFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/square.ttf");
        mConditionWeatherText = (TextView) rootView.findViewById(R.id.condition_panel_text_weather);
        mConditionWeatherText.setTypeface(squareFont);
        mConditionTemperatureText = (TextView) rootView.findViewById(R.id.condition_panel_text_temperature);
        mConditionTemperatureText.setTypeface(squareFont);
        mConditionReliefText = (TextView) rootView.findViewById(R.id.condition_panel_text_relief);
        mConditionReliefText.setTypeface(squareFont);
        mConditionConditionText = (TextView) rootView.findViewById(R.id.condition_panel_text_condition);
        mConditionConditionText.setTypeface(squareFont);

        return rootView;
    }


}
