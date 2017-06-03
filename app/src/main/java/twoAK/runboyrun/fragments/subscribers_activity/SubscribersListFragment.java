package twoAK.runboyrun.fragments.subscribers_activity;

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
import twoAK.runboyrun.activities.ProfileActivity;
import twoAK.runboyrun.adapters.AthletePreviewListAdapter;
import twoAK.runboyrun.responses.objects.AthletePreviewObject;


public class SubscribersListFragment extends Fragment {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "[" + ConditionActivity.class.getName() + "]: ";


    private ListView mListView;
    private ArrayList<AthletePreviewObject> mSubscribersList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.fragment_like_list, container, false);

        mListView = (ListView) mRootView.findViewById(R.id.like_list_listview);


        // on item click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("ATHLETE_ID", mSubscribersList.get(position).getId());
                startActivity(intent);
            }
        });

        return mRootView;
    }


    public void setSubscribers(ArrayList<AthletePreviewObject> subscribersList) {
        mSubscribersList = subscribersList;
        // set adapten on ListView
        AthletePreviewListAdapter adapter = new AthletePreviewListAdapter(getActivity(), mSubscribersList);
        mListView.setAdapter(adapter);
    }

}