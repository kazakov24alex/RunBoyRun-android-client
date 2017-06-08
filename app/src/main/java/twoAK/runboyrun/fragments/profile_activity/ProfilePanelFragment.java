package twoAK.runboyrun.fragments.profile_activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.request.body.SubscribeBody;
import twoAK.runboyrun.responses.GetProfileResponse;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class ProfilePanelFragment extends Fragment{

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private SendSubscribeTask mSendSubscribeTask;

    private GetProfileResponse mProfile;

    private SquareImageView mAvatarImage;
    private FloatingActionButton mSubscribeFAB;
    private TextView mNameText;
    private TextView mSurnameText;
    private TextView mCountryCityText;
    private TextView mAgeText;

    private int ind = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_panel, container, false);

        mAvatarImage = (SquareImageView) rootView.findViewById(R.id.profile_panel_imageview_avatar);
        mAvatarImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        mSubscribeFAB = (FloatingActionButton) rootView.findViewById(R.id.profile_panel_fab_subscribe);
        mSubscribeFAB.setImageBitmap(textAsBitmap("\uf234", 40, Color.WHITE));
        mSubscribeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendSubscribeTask == null) {
                    mSendSubscribeTask = new SendSubscribeTask(mProfile.getId());
                    mSendSubscribeTask.execute((Void) null);
                }
            }
        });

        //View initialization
        mNameText = (TextView) rootView.findViewById(R.id.profile_panel_textView_name);
        mSurnameText = (TextView) rootView.findViewById(R.id.profile_panel_textView_surname);
        mCountryCityText = (TextView) rootView.findViewById(R.id.profile_panel_textView_country_city);
        mAgeText = (TextView) rootView.findViewById(R.id.profile_panel_textView_age);

        return rootView;
    }


    public void setProfileData(GetProfileResponse profile) {
        mProfile = profile;

        mNameText.setText(profile.getName());
        mSurnameText.setText(profile.getSurname());
        mCountryCityText.setText(profile.getCountry()+", "+profile.getCity());

        Date birthdayDate = new Date();
        try{
            birthdayDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(profile.getBirthday().replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            //TODO: handle
            Log.i(APP_TAG, ACTIVITY_TAG + e);
        }

        mAgeText.setText("Age: "+getDiffYears(birthdayDate, new Date()));


        if(profile.getSubscription() == null) {
            mSubscribeFAB.setVisibility(View.GONE);
            mSubscribeFAB.setClickable(false);
        } else {
            setFABstate(profile.getSubscription());
        }
    }




    private class SendSubscribeTask extends AsyncTask<Void, Void, Boolean> {
        private String errMes;  // error message possible
        private SubscribeBody subscribeBody;

        SendSubscribeTask(int athlete_id) {
            errMes = null;
            subscribeBody = new SubscribeBody();
            subscribeBody.setAthlete_id(athlete_id);

            mSubscribeFAB.setClickable(false);
            setFABstate(!mProfile.getSubscription());
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to send subscription");
            try {
                return ApiClient.instance().sendSubscribe(subscribeBody);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.activity_page_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success == false) {
                Log.i(APP_TAG, ACTIVITY_TAG + "sending subscription ERROR: " + errMes);
                Toast.makeText(getContext(), errMes, Toast.LENGTH_SHORT).show();

                mSubscribeFAB.setClickable(true);
                //setFABstate(mProfile.getSubscription());
                mSendSubscribeTask = null;
                return;
            }

            Log.i(APP_TAG, ACTIVITY_TAG + "subscription was sent");
            Toast.makeText(getContext(), getString(R.string.profile_panel_toast_subscribed), Toast.LENGTH_SHORT).show();

            mSubscribeFAB.setClickable(true);
            mProfile.setSubscription(!mProfile.getSubscription());
            setFABstate(mProfile.getSubscription());
            mSendSubscribeTask = null;
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }

    public void setFABstate(boolean state) {
        //TODO:
        if (state == true) {
            mSubscribeFAB.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TEXT_GREY)));
        } else {
            mSubscribeFAB.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TEXT_GREY)));
        }
    }



    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }


    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint();
        Typeface fontello = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontello.ttf");
        paint.setTypeface(fontello);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }



}
