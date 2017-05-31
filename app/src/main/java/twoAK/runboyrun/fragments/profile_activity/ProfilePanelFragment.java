package twoAK.runboyrun.fragments.profile_activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.responses.GetProfileResponse;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class ProfilePanelFragment extends Fragment{

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private GetProfileResponse mProfile;

    private SquareImageView mAvatarImage;
    private FloatingActionButton mSubscribeFAB;
    private TextView mNameText;
    private TextView mSurnameText;
    private TextView mCountryCityText;
    private TextView mAgeText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_panel, container, false);

        mAvatarImage = (SquareImageView) rootView.findViewById(R.id.profile_panel_imageview_avatar);
        mAvatarImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        mSubscribeFAB = (FloatingActionButton) rootView.findViewById(R.id.profile_panel_fab_subscribe);

        mSubscribeFAB.setImageBitmap(textAsBitmap("\uf234", 40, Color.WHITE));

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
