package twoAK.runboyrun.fragments.activity_page;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.adapters.SquareImageView;


public class ConditionPanelFragment extends Fragment {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";


    static final String ENUM_WEATHER_SUNNY  = "SUNNY";
    static final String ENUM_WEATHER_CLOUDY = "CLOUDY";
    static final String ENUM_WEATHER_RAINY  = "RAINY";
    static final String ENUM_WEATHER_SNOWY  = "SNOWY";

    static final String ENUM_RELIEF_STADIUM = "STADIUM";
    static final String ENUM_RELIEF_PARK    = "PARK";
    static final String ENUM_RELIEF_CROSS   = "CROSS";
    static final String ENUM_RELIEF_HILLS   = "HILLS";

    static final String ENUM_CONDITIOM_GOOD     = "GOOD";
    static final String ENUM_CONDITIOM_MEDIUM   = "MEDIUM";
    static final String ENUM_CONDITIOM_TIRED    = "TIRED";
    static final String ENUM_CONDITIOM_BEATED   = "BEATED";


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


    public void setWeather(String value){
        switch (value){
            case ENUM_WEATHER_SUNNY:
                mConditionWeatherImage.setImageResource(R.drawable.weather_sunny);
                break;
            case ENUM_WEATHER_CLOUDY:
                mConditionWeatherImage.setImageResource(R.drawable.weather_cloudy);
                break;
            case ENUM_WEATHER_RAINY:
                mConditionWeatherImage.setImageResource(R.drawable.weather_rainy);
                break;
            case ENUM_WEATHER_SNOWY:
                mConditionWeatherImage.setImageResource(R.drawable.weather_snowy);
                break;
            default: break;
        }
    }

    public void setRelief(String value){
        switch (value){
            case ENUM_RELIEF_STADIUM:
                mConditionReliefImage.setImageResource(R.drawable.relief_stadium);
                break;
            case ENUM_RELIEF_CROSS:
                mConditionReliefImage.setImageResource(R.drawable.relief_cross_country);
                break;
            case ENUM_RELIEF_HILLS:
                mConditionReliefImage.setImageResource(R.drawable.relief_hills);
                break;
            case ENUM_RELIEF_PARK:
                mConditionReliefImage.setImageResource(R.drawable.relief_park);
                break;
            default: break;
        }
    }

    public void setCondition(String value){
        switch (value){
            case ENUM_CONDITIOM_GOOD:
                mConditionConditionImage.setImageResource(R.drawable.condition_good);
                break;
            case ENUM_CONDITIOM_MEDIUM:
                mConditionConditionImage.setImageResource(R.drawable.condition_medium);
                break;
            case ENUM_CONDITIOM_TIRED:
                mConditionConditionImage.setImageResource(R.drawable.condition_tired);
                break;
            case ENUM_CONDITIOM_BEATED:
                mConditionConditionImage.setImageResource(R.drawable.condition_beated);
                break;
            default: break;
        }
    }


}
