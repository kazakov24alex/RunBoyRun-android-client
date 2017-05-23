package twoAK.runboyrun.fragments.profile_activity;

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


public class ButtonPanelFragment extends Fragment {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private SquareImageView mFriendsButton;
    private SquareImageView mStatsButton;
    private SquareImageView mRecordsButton;
    private SquareImageView mVictoriesButton;

    private TextView mTextFriendsButton;
    private TextView mTextStatsButton;
    private TextView mTextRecordsButton;
    private TextView mTextVictoriesButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_buttons, container, false);

        // Initialization images of button panel
        mFriendsButton = (SquareImageView) rootView.findViewById(R.id.profile_buttons_fragment_squareimage_friends);
        mFriendsButton.setImageResource(R.drawable.sport_type_running);
        mStatsButton = (SquareImageView) rootView.findViewById(R.id.profile_buttons_fragment_squareimage_stats);
        mStatsButton.setImageResource(R.drawable.sport_type_running);
        mRecordsButton = (SquareImageView) rootView.findViewById(R.id.profile_buttons_fragment_squareimage_records);
        mRecordsButton.setImageResource(R.drawable.sport_type_running);
        mVictoriesButton = (SquareImageView) rootView.findViewById(R.id.profile_buttons_fragment_squareimage_victories);
        mVictoriesButton.setImageResource(R.drawable.sport_type_running);

        // initialization text of button panel and setting custom font
        Typeface squareFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/square.ttf");
        mTextFriendsButton = (TextView) rootView.findViewById(R.id.profile_buttons_fragment_textview_friends);
        mTextFriendsButton.setTypeface(squareFont);
        mTextStatsButton = (TextView) rootView.findViewById(R.id.profile_buttons_fragment_textview_stats);
        mTextStatsButton.setTypeface(squareFont);
        mTextRecordsButton = (TextView) rootView.findViewById(R.id.profile_buttons_fragment_textview_records);
        mTextRecordsButton.setTypeface(squareFont);
        mTextVictoriesButton = (TextView) rootView.findViewById(R.id.profile_buttons_fragment_textview_victories);
        mTextVictoriesButton.setTypeface(squareFont);

        return rootView;
    }

}
