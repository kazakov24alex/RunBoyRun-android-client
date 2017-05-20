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
    private Boolean mMyValue;
    private int mLikeNum;
    private int mDislikeNum;

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

        setLikeSelected(false);
        setDislikeSelected(false);

        return rootView;
    }


    public void setLikeNum(int num) {
        mLikeNum = num;

        if(num != 0) {
            mLikeValueText.setText(Integer.toString(num));
        } else {
            mLikeValueText.setText("");
        }
    }

    public void setDislikeNum(int num) {
        mDislikeNum = num;

        if (num != 0) {
            mDislikeValueText.setText(Integer.toString(num));
        } else {
            mDislikeValueText.setText("");
        }
    }

    public void setMyValue(Boolean value) {
        mMyValue = value;
        setLikeSelected(false);
        setDislikeSelected(false);
        if(value != null) {
            if(value == true) {
                setLikeSelected(true);
            } else {
                setDislikeSelected(true);
            }
        }
    }

    public void setLikeSelected(boolean selected) {
        if(selected) {
            mLikeIconText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mLikeValueText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mLikeState = true;
        } else {
            mLikeIconText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mLikeValueText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mLikeState = false;
        }
    }

    public void setDislikeSelected(boolean selected) {
        if(selected) {
            mDislikeIconText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mDislikeValueText.setTextColor(getResources().getColor(R.color.TEXT_SELECTED));
            mDislikeState = true;
        } else {
            mDislikeIconText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mDislikeValueText.setTextColor(getResources().getColor(R.color.TEXT_GREY));
            mDislikeState = false;
        }
    }





    public Boolean getmMyValue() {
        return mMyValue;
    }

    public int getmLikeNum() {
        return mLikeNum;
    }

    public int getmDislikeNum() {
        return mDislikeNum;
    }


    public void onLikeClick() {
        if(mMyValue != null) {
            if(mMyValue == false) {
                mDislikeNum--;
                setDislikeNum(mDislikeNum);

                mLikeNum++;
                setLikeNum(mLikeNum);

                setMyValue(true);
            } else {
                mLikeNum--;
                setLikeNum(mLikeNum);

                setMyValue(null);
            }
        } else {
            mLikeNum++;
            setLikeNum(mLikeNum);

            setMyValue(true);
        }
    }

    public void onDislikeClick() {
        if(mMyValue != null) {
            if(mMyValue == true) {
                mLikeNum--;
                setLikeNum(mLikeNum);

                mDislikeNum++;
                setDislikeNum(mDislikeNum);

                setMyValue(false);
            } else {
                mDislikeNum--;
                setDislikeNum(mDislikeNum);

                setMyValue(null);
            }
        } else {
            mDislikeNum++;
            setDislikeNum(mDislikeNum);

            setMyValue(false);
        }
    }

}