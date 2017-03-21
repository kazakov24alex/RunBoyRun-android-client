package twoAK.runboyrun.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Spinner;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CitiesSpinnerAdapter;
import twoAK.runboyrun.adapters.CountriesSpinnerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.objects.cities.CitiesList;
import twoAK.runboyrun.objects.countries.CountriesList;


public class SignUpActivity extends AppCompatActivity {

    SignUpActivity self = this;

    CountriesList mCountriesList;
    CountriesLoadTask mCountriesLoadTask;
    CitiesList mCitiesList;
    CitiesLoadTask mCitiesLoadTask;

    Spinner mCountrySpinner;
    Spinner mCitySpinner;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);

        mCountrySpinner = (Spinner)findViewById(R.id.signup_spinner_country);
        mCitySpinner = (Spinner)findViewById(R.id.signup_spinner_city);

        mCountriesLoadTask = new SignUpActivity.CountriesLoadTask();
        mCountriesLoadTask.execute((Void) null);

        // TODO: prisobachit' to country selection
        mCitiesLoadTask = new SignUpActivity.CitiesLoadTask("RUS");
        mCitiesLoadTask.execute((Void) null);
    }



    /////////////////////////////////////////////////////////////////////////
    public class CountriesLoadTask extends AsyncTask<Void, Void, Boolean> {

        CountriesLoadTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load countries.");

            mCountriesList = new CountriesList(ApiClient.instance());

            return mCountriesList.load();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            System.out.println(mCountriesList.getAll());
            // create an adapter and assign the adapter to the list
            CountriesSpinnerAdapter countryAdapter = new CountriesSpinnerAdapter(self, mCountriesList.getAll());
            mCountrySpinner.setAdapter(countryAdapter);

        }

        @Override
        protected void onCancelled() {

        }
    }

    /////////////////////////////////////////////////////////////////////////
    public class CitiesLoadTask extends AsyncTask<Void, Void, Boolean> {

        private String countryCode;

        CitiesLoadTask(String countryCode) {
            this.countryCode = countryCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load cities.");

            mCitiesList = new CitiesList(ApiClient.instance());

            return mCitiesList.load(countryCode);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            System.out.println("CITIES =" + mCitiesList.getAll());
            // create an adapter and assign the adapter to the list
            CitiesSpinnerAdapter cityAdapter = new CitiesSpinnerAdapter(self, mCitiesList.getAll());
            mCitySpinner.setAdapter(cityAdapter);


        }

        @Override
        protected void onCancelled() {

        }
    }
}
