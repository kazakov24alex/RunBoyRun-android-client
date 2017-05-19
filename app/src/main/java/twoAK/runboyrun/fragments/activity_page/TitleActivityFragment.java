package twoAK.runboyrun.fragments.activity_page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.adapters.SquareImageView;


public class TitleActivityFragment extends Fragment {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    static final String ENUM_SPORT_RUNNING = "RUNNING";
    static final String ENUM_SPORT_CYCLING = "CYCLING";
    static final String ENUM_SPORT_WALKING = "WALKING";
    static final String ENUM_SPORT_SKIRUN  = "SKIRUN";

    private SquareImageView mSportImage;

    private TextView mSportValue;
    private TextView mDateValue;
    private TextView mTimeValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_title_activity, container, false);

        mSportImage = (SquareImageView) rootView.findViewById(R.id.title_activity_squareimage_sport);
        mSportImage.setImageResource(R.drawable.com_facebook_top_button);

        mSportValue = (TextView) rootView.findViewById(R.id.title_activity_text_sport_value);
        mDateValue = (TextView) rootView.findViewById(R.id.title_activity_text_date);
        mTimeValue = (TextView) rootView.findViewById(R.id.title_activity_text_time);

        return rootView;
    }


    public void setSportValue(String sport) {
        switch (sport) {
            case ENUM_SPORT_RUNNING:
                mSportValue.setText(getString(R.string.title_activity_text_title_running));
                break;
            case ENUM_SPORT_CYCLING:
                mSportValue.setText(getString(R.string.title_activity_text_title_cycling));
                break;
            case ENUM_SPORT_WALKING:
                mSportValue.setText(getString(R.string.title_activity_text_title_walking));
                break;
            case ENUM_SPORT_SKIRUN:
                mSportValue.setText(getString(R.string.title_activity_text_title_skirun));
                break;
            default:
                break;
        }
    }

    public void setDateTimeStartValue(String date_string) {

        try {
            Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(date_string.replaceAll("Z$", "+0000"));

            mDateValue.setText((new SimpleDateFormat("dd MMMM")).format(date));
            mTimeValue.setText((new SimpleDateFormat("hh:mm a")).format(date));
        } catch (ParseException e) {
            //TODO: handle
            Log.i(APP_TAG, ACTIVITY_TAG + e);
        }

    }

}
