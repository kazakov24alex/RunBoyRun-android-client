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


public class LikePanelFragment extends Fragment {

    private boolean mLikeState;
    private boolean mDislikeState;

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private TextView mLikeIconText;
    private TextView mListIconText;
    private TextView mDislikeIconText;

    private TextView mLikeValueText;
    private TextView mDislikeValueText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_like_panel, container, false);

        // initialization text of button panel and setting custom font
        Typeface likeFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontello.ttf");
        mLikeIconText = (TextView) rootView.findViewById(R.id.like_panel_text_like_icon);
        mLikeIconText.setTypeface(likeFont);
        mLikeIconText.setText("\uf164");
        mListIconText = (TextView) rootView.findViewById(R.id.like_panel_text_list_icon);
        mListIconText.setTypeface(likeFont);
        mListIconText.setText("\ue800");
        mDislikeIconText = (TextView) rootView.findViewById(R.id.like_panel_text_dislike_icon);
        mDislikeIconText.setTypeface(likeFont);
        mDislikeIconText.setText("\uf165");

        mLikeValueText = (TextView) rootView.findViewById(R.id.like_panel_text_like_value);
        mDislikeValueText = (TextView) rootView.findViewById(R.id.like_panel_text_dislike_value);

        return rootView;
    }


    public void setLikeValue(String value) {
        mLikeValueText.setText(value);
    }

    public void setDislikeValue(String value) {
        mDislikeValueText.setText(value);
    }

    public void setLikeSelected(boolean selected) {
        if(selected) {
            mLikeIconText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mLikeValueText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mLikeState = false;
        } else {
            mLikeIconText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mLikeValueText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mLikeState = true;
        }
    }

    public void setDislikeSelected(boolean selected) {
        if(selected) {
            mDislikeIconText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mDislikeValueText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mDislikeState = false;
        } else {
            mDislikeIconText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mDislikeValueText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mDislikeState = true;
        }
    }

    public boolean getLikeState() {
        return mLikeState;
    }

    public boolean getDislikeState() {
        return mDislikeState;
    }

}