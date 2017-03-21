package twoAK.runboyrun.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.responses.objects.CountryObject;


public class CountriesSpinnerAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private List<CountryObject> countries;

    public CountriesSpinnerAdapter(Context context, List<CountryObject> countries) {
        ctx = context;
        this.countries = countries;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return countries.size();
    }

    // элемент по позиции
    @Override
    public CountryObject getItem(int position) {
        return countries.get(position);
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
            view = lInflater.inflate(R.layout.countries_list_item, parent, false);
        }

        CountryObject country = getItem(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.CountriesListItem)).setText(country.getName());

        return view;
    }

}