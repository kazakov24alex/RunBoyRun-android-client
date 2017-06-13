package twoAK.runboyrun.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.responses.objects.AthletePreviewObject;

public class AthletePreviewListAdapter extends BaseAdapter {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private Context ctx;
    private LayoutInflater lInflater;
    private List<AthletePreviewObject> subscribersList;

    public AthletePreviewListAdapter(Context context, List<AthletePreviewObject> subscribersList) {
        this.subscribersList = subscribersList;
        ctx = context;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public AthletePreviewListAdapter(LayoutInflater lInflater, List<AthletePreviewObject> subscribersList) {
        this.subscribersList = subscribersList;
        this.lInflater = lInflater;
    }

    @Override
    public int getCount() {
        return subscribersList.size();
    }

    @Override
    public AthletePreviewObject getItem(int position) {
        return subscribersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_athlete_preview_list, parent, false);
        }

        AthletePreviewObject athlete = getItem(position);

        // initialization of views
        ((SquareImageView) view.findViewById(R.id.item_athlete_preview_squareimage_avatar))
                .setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        ((TextView) view.findViewById(R.id.item_athlete_preview_list_text_name_surname))
                .setText(athlete.getName()+" "+athlete.getSurname());

        return view;
    }

}
