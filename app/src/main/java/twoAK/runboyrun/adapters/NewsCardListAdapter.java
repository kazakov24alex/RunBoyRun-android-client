package twoAK.runboyrun.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ActivityPageActivity;
import twoAK.runboyrun.activities.NewsFeedProfileActivity;
import twoAK.runboyrun.activities.ValueActivity;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.request.body.ValueBody;
import twoAK.runboyrun.responses.ValueResponse;
import twoAK.runboyrun.responses.objects.NewsObject;


public class NewsCardListAdapter extends BaseAdapter {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+NewsCardListAdapter.class.getName()+"]: ";

    private SendValueTask mSendValueTask = null;

    private Context ctx;
    private LayoutInflater lInflater;
    private List<NewsObject> newsList;


    public NewsCardListAdapter(Context context, FragmentManager fm, List<NewsObject> newsList) {
        this.newsList = newsList;
        ctx = context;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public NewsObject getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_news, parent, false);
        }

        NewsObject news = getItem(position);

        initTitlePart(view, news);
        initTypeSportPart(view, news);
        initDescriptionPart(view, news);
        initStatsPart(view, news);
        initValuePart(view, news);

        initListeners(view, news);

        return view;
    }

    private void initListeners(View view, final NewsObject news) {
        LinearLayout mTitleForm = (LinearLayout) view.findViewById(R.id.news_item_form);
        mTitleForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, NewsFeedProfileActivity.class);
                intent.putExtra("ATHLETE_ID", news.getAthlete_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        LinearLayout mActitvityForm = (LinearLayout) view.findViewById(R.id.news_item_layout_activity);
        mActitvityForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ActivityPageActivity.class);
                intent.putExtra("ACTIVITY_ID", news.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        LinearLayout mListLayout = (LinearLayout) view.findViewById(R.id.news_item_list_button);
        mListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(ctx, ValueActivity.class);
                    intent.putExtra("ACTIVITY_ID", news.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
            }
        });

    }

    private void initTitlePart(View view, final NewsObject news) {
        SquareImageView mAvatarImage = (SquareImageView) view.findViewById(R.id.news_item_image_avatar);
        mAvatarImage.setImageResource(R.drawable.com_facebook_top_button);

        TextView mNameText = (TextView) view.findViewById(R.id.news_item_text_name_surname);
        if( (news.getName().length()+news.getSurname().length()+1) > 15 ) {
            mNameText.setText(news.getName().charAt(0)+". "+news.getSurname());
        } else {
            mNameText.setText(news.getName()+" "+news.getSurname());
        }

        TextView mTimeText = (TextView) view.findViewById(R.id.news_item_text_time_start);
        TextView mDateText = (TextView) view.findViewById(R.id.news_item_text_date_start);

        try {
            Date curDate = new Date();
            Date comDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(news.getDatetime_start().replaceAll("Z$", "+0000"));

            String curDateString = (new SimpleDateFormat("dd MMMM yyyy")).format(curDate);
            String comDateString = (new SimpleDateFormat("dd MMMM yyyy")).format(comDate);

            mTimeText.setText((new SimpleDateFormat("kk:mm")).format(comDate));

            if(curDate.getYear() == comDate.getYear()) {
                mDateText.setText((new SimpleDateFormat("dd MMM yyyy")).format(comDate));
            } else {
                mDateText.setText((new SimpleDateFormat("dd MMMM kk:mm")).format(comDate));
            }
        } catch (ParseException e) { }

    }

    private void initTypeSportPart(View view, final NewsObject news) {
        SquareImageView mSportTypeImage = (SquareImageView) view.findViewById(R.id.news_item_squareimage_sporttype);
        TextView mSportTypeText = (TextView) view.findViewById(R.id.news_item_text_sport_value);
        TextView mPointsText = (TextView) view.findViewById(R.id.news_item_text_points);

        final String ENUM_SPORT_RUNNING = "RUNNING";
        final String ENUM_SPORT_CYCLING = "CYCLING";
        final String ENUM_SPORT_WALKING = "WALKING";
        final String ENUM_SPORT_SKIRUN = "SKIRUN";

        switch(news.getSport_type()) {
            case ENUM_SPORT_RUNNING:
                mSportTypeImage.setImageResource(R.drawable.sport_type_running);
                mSportTypeText.setText(ctx.getString(R.string.title_activity_text_title_running));
                break;
            case ENUM_SPORT_CYCLING:
                mSportTypeImage.setImageResource(R.drawable.sport_type_cycling);
                mSportTypeText.setText(ctx.getString(R.string.title_activity_text_title_cycling));
                break;
            case ENUM_SPORT_WALKING:
                mSportTypeImage.setImageResource(R.drawable.sport_type_walking);
                mSportTypeText.setText(ctx.getString(R.string.title_activity_text_title_walking));
                break;
            case ENUM_SPORT_SKIRUN:
                mSportTypeImage.setImageResource(R.drawable.sport_type_skirun);
                mSportTypeText.setText(ctx.getString(R.string.title_activity_text_title_skirun));
                break;
            default:
                mSportTypeImage.setImageResource(R.drawable.com_facebook_top_button);
                mSportTypeText.setText("ERROR");
                break;
        }


        mPointsText.setText("HEY"); // TODO: points
    }

    private void initDescriptionPart(View view, final NewsObject news) {
        TextView mDescription = (TextView) view.findViewById(R.id.news_item_text_description);
        if(news.getDescription() != null) {
            mDescription.setText(news.getDescription());
        } else {
            mDescription.setVisibility(View.GONE);
        }
    }

    private void initStatsPart(View view, final NewsObject news) {
        SquareImageView mTimeImage = (SquareImageView) view.findViewById(R.id.news_item_squareimage_time);
        mTimeImage.setImageResource(R.drawable.result_activity_panel_time);
        SquareImageView mDistanceImage = (SquareImageView) view.findViewById(R.id.news_item_squareimage_distance);
        mDistanceImage.setImageResource(R.drawable.result_activity_panel_distance);

        TextView mDurationValueText = (TextView) view.findViewById(R.id.news_item_text_time_value);
        mDurationValueText.setText(news.getDuration());
        TextView mDistanceValueText = (TextView) view.findViewById(R.id.news_item_text_distance_value);
        mDistanceValueText.setText(""+news.getDistance());
    }

    private void initValuePart(View view, final NewsObject news) {
        // initialization text of button panel and setting custom font
        Typeface likeFont = Typeface.createFromAsset(ctx.getAssets(), "fonts/fontello.ttf");
        final TextView mLikeIconText = (TextView) view.findViewById(R.id.news_item_text_like_icon);
        mLikeIconText.setTypeface(likeFont);
        mLikeIconText.setText("\uf164");
        TextView mListIconText = (TextView) view.findViewById(R.id.news_item_text_list_icon);
        mListIconText.setTypeface(likeFont);
        mListIconText.setText("\ue800");
        final TextView mDislikeIconText = (TextView) view.findViewById(R.id.news_item_text_dislike_icon);
        mDislikeIconText.setTypeface(likeFont);
        mDislikeIconText.setText("\uf165");

        final TextView mLikeValueText = (TextView) view.findViewById(R.id.news_item_text_like_value);
        final TextView mDislikeValueText = (TextView) view.findViewById(R.id.news_item_text_dislike_value);


        if(news.getLike_num() != 0) {
            mLikeValueText.setText("" + news.getLike_num());
        } else {
            mLikeValueText.setText("");
        }
        if(news.getDislike_num() != 0) {
            mDislikeValueText.setText("" + news.getDislike_num());
        } else {
            mDislikeValueText.setText("");
        }

        if(news.getMy_value() != null) {
            if(news.getMy_value() == false) {
                mDislikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
                mDislikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
            } else {
                mLikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
                mLikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
            }
        } else {
            mLikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
            mLikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
            mDislikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
            mDislikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
        }

        // listeners
        LinearLayout mLikeLayout = (LinearLayout) view.findViewById(R.id.news_item_like_button);
        mLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendValueTask == null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "onLikeClick");
                    SendValueTask mSendValueTask = new SendValueTask(ctx, news.getId(), true);
                    mSendValueTask.setViews(mLikeValueText, mDislikeValueText, mLikeIconText, mDislikeIconText);
                    mSendValueTask.execute((Void) null);
                }

            }
        });

        LinearLayout mDislikeLayout = (LinearLayout) view.findViewById(R.id.news_item_dislike_button);
        mDislikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendValueTask == null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "onDislikeClick");
                    mSendValueTask = new SendValueTask(ctx, news.getId(), false);
                    mSendValueTask.setViews(mLikeValueText, mDislikeValueText, mLikeIconText, mDislikeIconText);
                    mSendValueTask.execute((Void) null);

                }
            }
        });


    }



    private class SendValueTask extends AsyncTask<Void, Void, ValueResponse> {
        private String errMes;  // error message possible
        private ValueBody valueBody;
        private Context ctx;

        private TextView mLikeIconText;
        private TextView mDislikeIconText;
        private TextView mLikeValueText;
        private TextView mDislikeValueText;


        SendValueTask(Context context, int activity_id, boolean value) {
            errMes = null;
            ctx = context;
            valueBody = new ValueBody();
            valueBody.setActivity_id(activity_id);
            valueBody.setValue(value);
        }

        public void setViews(TextView likeText, TextView dislikeText, TextView likeIcon, TextView dislikeIcon) {
            mLikeIconText = likeIcon;
            mDislikeIconText = dislikeIcon;
            mLikeValueText = likeText;
            mDislikeValueText = dislikeText;
        }


        @Override
        protected ValueResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to send value");
            try {
                return ApiClient.instance().sendValue(valueBody);
            } catch(RequestFailedException e) {
                errMes = ctx.getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = ctx.getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ValueResponse valueResponse) {
            if(valueResponse == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "ERROR: " + errMes);
                Toast.makeText(ctx, errMes, Toast.LENGTH_SHORT).show();

                return;
            } else {
                Log.i(APP_TAG, ACTIVITY_TAG + "value was sent");

                if(valueResponse.getLike_num() != 0) {

                    mLikeValueText.setText("" + valueResponse.getLike_num());
                } else {
                    mLikeValueText.setText("");
                }
                if(valueResponse.getDislike_num() != 0) {
                    mDislikeValueText.setText("" + valueResponse.getDislike_num());
                } else {
                    mDislikeValueText.setText("");
                }

                if(valueResponse.getMy_value() != null) {
                    if(valueResponse.getMy_value() == false) {
                        mDislikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
                        mDislikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
                        mLikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                        mLikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                    } else {
                        mLikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
                        mLikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_SELECTED));
                        mDislikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                        mDislikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                    }
                } else {
                    mLikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                    mLikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                    mDislikeIconText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                    mDislikeValueText.setTextColor(ctx.getResources().getColor(R.color.TEXT_GREY));
                }

            }

            mSendValueTask = null;
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }

    }

}
