package twoAK.runboyrun.fragments.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.activities.NewsFeedProfileActivity;


public class TitleNewsFragment extends Fragment {

    private View rootView;

    private int mAthleteID;

    private LinearLayout mForm;
    private SquareImageView mAvatarImage;
    private TextView mNameText;
    private TextView mTimeText;
    private TextView mDateText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_news_title, container, false);

        mAvatarImage = (SquareImageView) rootView.findViewById(R.id.news_title_panel_image_avatar);
        mAvatarImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        mNameText = (TextView) rootView.findViewById(R.id.news_title_panel_text_name_surname);
        mTimeText = (TextView) rootView.findViewById(R.id.news_title_panel_text_time_start);
        mDateText = (TextView) rootView.findViewById(R.id.news_title_panel_text_date_start);

        mForm = (LinearLayout) rootView.findViewById(R.id.news_title_panel_form);
        mForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), NewsFeedProfileActivity.class);
                intent.putExtra("ATHLETE_ID", mAthleteID);
                startActivity(intent);
            }
        });
        return rootView;
    }



    public void setAthleteID(int athlete_id) {
        mAthleteID = athlete_id;
    }

    public void setName(String name, String surname) {
        if( (name.length()+surname.length()+1) > 15 ) {
            mNameText.setText(name.charAt(0)+". "+surname);
        } else {
            mNameText.setText(name+" "+surname);
        }
    }

    public void setDateTimeStart(String dateTimeStart) {
        try {
            Date curDate = new Date();
            Date comDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(dateTimeStart.replaceAll("Z$", "+0000"));

            String curDateString = (new SimpleDateFormat("dd MMMM yyyy")).format(curDate);
            String comDateString = (new SimpleDateFormat("dd MMMM yyyy")).format(comDate);

            mTimeText.setText((new SimpleDateFormat("kk:mm")).format(comDate));

            if(curDate.getYear() == comDate.getYear()) {
                mDateText.setText((new SimpleDateFormat("dd MMM yyyy")).format(comDate));
            } else {
                mDateText.setText((new SimpleDateFormat("dd MMMM kk:mm")).format(comDate));
            }
        } catch (ParseException e) {
            //TODO: handle
        }
    }



}