package twoAK.runboyrun.fragments.value_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.adapters.LikeListAdapter;
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

        LikeListAdapter adapter = new LikeListAdapter(getActivity(), mValuesList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                Toast.makeText(getActivity(), mValuesList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return mRootView;
    }


    public void setValues(ArrayList<ValueObject> valuesList) {
        mValuesList = valuesList;
    }

}




