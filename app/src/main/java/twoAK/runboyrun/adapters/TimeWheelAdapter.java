package twoAK.runboyrun.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;
import twoAK.runboyrun.R;

public class TimeWheelAdapter extends AbstractWheelTextAdapter {
    // Countries names
    private List<String> minutesTexts;

    /**
     * Constructor
     */
    public TimeWheelAdapter(Context context, int sequence, String label) {
        super(context, R.layout.item_minutes, NO_RESOURCE);

        setItemTextResource(R.id.item_minutes_text);

        minutesTexts = new ArrayList<String>();
        for(int i=0; i<=sequence; i++) {
            minutesTexts.add(i+" "+label);
        }
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent, int currentItemIdx) {
        View view = super.getItem(index, cachedView, parent, currentItemIdx);
        return view;
    }

    @Override
    public int getItemsCount() {
        return minutesTexts.size();
    }

    @Override
    protected CharSequence getItemText(int index) {
        return minutesTexts.get(index);
    }
}
