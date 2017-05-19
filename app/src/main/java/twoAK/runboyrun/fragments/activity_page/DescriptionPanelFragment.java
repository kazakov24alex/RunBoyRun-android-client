package twoAK.runboyrun.fragments.activity_page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;


public class DescriptionPanelFragment extends Fragment {

    private TextView mDescriptionValue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_description_panel, container, false);

        mDescriptionValue = (TextView) rootView.findViewById(R.id.description_panel_text_value);

        return rootView;
    }

    public void setDescriptionValue(String description) {
        mDescriptionValue.setText(description);
    }

}
