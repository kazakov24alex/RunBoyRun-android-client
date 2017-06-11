package twoAK.runboyrun.fragments.value_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.activities.NewsFeedProfileActivity;
import twoAK.runboyrun.adapters.ValueListAdapter;
import twoAK.runboyrun.responses.objects.ValueObject;


public class LikeListFragment extends Fragment{

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";


    private ListView mListView;
    private ArrayList<ValueObject> mValuesList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.fragment_like_list, container, false);

        mListView = (ListView) mRootView.findViewById(R.id.like_list_listview);

        // set adapten on ListView
        ValueListAdapter adapter = new ValueListAdapter(getActivity(), mValuesList);
        mListView.setAdapter(adapter);

        // on item click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(getActivity(), NewsFeedProfileActivity.class);
                intent.putExtra("ATHLETE_ID", mValuesList.get(position).getAthlete_id());
                startActivity(intent);
            }
        });

        return mRootView;
    }


    public void setValues(ArrayList<ValueObject> valuesList) {
        mValuesList = valuesList;
    }

}




