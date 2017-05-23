package twoAK.runboyrun.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.responses.objects.CityObject;


public class CitiesSpinnerAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private List<CityObject> cities;

    public CitiesSpinnerAdapter(Context context, List<CityObject> cities) {
        ctx = context;
        this.cities = cities;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return cities.size();
    }

    // элемент по позиции
    @Override
    public CityObject getItem(int position) {
        return cities.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_countries_list, parent, false);
        }

        CityObject city = getItem(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.CountriesListItem)).setText(city.getName());

        return view;
    }

}