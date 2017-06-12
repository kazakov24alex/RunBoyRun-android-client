package twoAK.runboyrun.fragments.activity_page;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.activities.ValueActivity;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.ValueResponse;


public class LikePanelFragment extends Fragment {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private SendValueTask mSendValueTask;

    private int mActivityID;
    private int mLikeNum;
    private int mDislikeNum;

    private boolean mLikeState;
    private boolean mDislikeState;
    private Boolean mMyValue;

    private TextView mLikeIconText;
    private TextView mListIconText;
    private TextView mDislikeIconText;

    private TextView mLikeValueText;
    private TextView mDislikeValueText;

    private LinearLayout mLikeLayout;
    private LinearLayout mDislikeLayout;
    private LinearLayout mListLayout;



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

        mLikeLayout = (LinearLayout) rootView.findViewById(R.id.like_panel_like_button);
        mListLayout = (LinearLayout) rootView.findViewById(R.id.like_panel_list_button);
        mDislikeLayout = (LinearLayout) rootView.findViewById(R.id.like_panel_dislike_button);

        setListeners();

        setLikeSelected(false);
        setDislikeSelected(false);

        return rootView;
    }

    public void setListeners() {
        mLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendValueTask == null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "onLikeClick");
                    mSendValueTask = new SendValueTask(mActivityID, true);
                    mSendValueTask.setStartValue(mLikeNum, mDislikeNum, mMyValue);
                    mSendValueTask.execute((Void) null);

                    onLikeClick();
                }
            }
        });

        mListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendValueTask == null) {
                    Intent intent = new Intent(getContext(), ValueActivity.class);
                    intent.putExtra("ACTIVITY_ID", mActivityID);
                    startActivity(intent);
                }
            }
        });

        mDislikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendValueTask == null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "onDislikeClick");
                    mSendValueTask = new SendValueTask(mActivityID, false);
                    mSendValueTask.setStartValue(mLikeNum, mDislikeNum, mMyValue);
                    mSendValueTask.execute((Void) null);

                    onDislikeClick();
                }
            }
        });

    }

    public void setActivityID(int activity_id) {
        mActivityID = activity_id;
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

    private void setLikeSelected(boolean selected) {
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

    private void setDislikeSelected(boolean selected) {
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



    private class SendValueTask extends AsyncTask<Void, Void, ValueResponse> {
        private String errMes;  // error message possible
        private ValueBody valueBody;

        private int start_like_num;
        private int start_dislike_num;
        private Boolean start_my_value;

        SendValueTask(int activity_id, boolean value) {
            errMes = null;
            valueBody = new ValueBody();
            valueBody.setActivity_id(activity_id);
            valueBody.setValue(value);
        }

        public void setStartValue(int like_num, int dislike_num, Boolean my_value) {
            start_like_num      = like_num;
            start_dislike_num   = dislike_num;
            start_my_value      = my_value;
        }

        @Override
        protected ValueResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to send value");
            try {
                return ApiClient.instance().sendValue(valueBody);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ValueResponse result) {
            if(result == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: " + errMes);
                Toast.makeText(getContext(), errMes, Toast.LENGTH_SHORT).show();

                setLikeNum(start_like_num);
                setDislikeNum(start_dislike_num);
                setMyValue(start_my_value);

                return;
            } else {
                Log.i(APP_TAG, ACTIVITY_TAG + "value was sent");
            }
            mSendValueTask = null;
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }

}