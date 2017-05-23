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
import twoAK.runboyrun.responses.objects.ValueObject;


public class LikeListAdapter extends BaseAdapter {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private Context ctx;
    private LayoutInflater lInflater;
    private List<ValueObject> valuesList;

    public LikeListAdapter(Context context, List<ValueObject> valuesList) {
        this.valuesList = valuesList;
        ctx = context;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return valuesList.size();
    }

    @Override
    public ValueObject getItem(int position) {
        return valuesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_like_list, parent, false);
        }

        ValueObject value = getItem(position);

        // initialization of views
        ((SquareImageView) view.findViewById(R.id.item_like_list_squareimage_avatar))
                .setImageResource(R.drawable.com_facebook_top_button);
        ((TextView) view.findViewById(R.id.item_like_list_text_name_surname))
                .setText(value.getName()+" "+value.getSurname());

        return view;
    }

}
