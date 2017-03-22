package twoAK.runboyrun.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CitiesSpinnerAdapter;
import twoAK.runboyrun.adapters.CountriesSpinnerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.objects.cities.CitiesList;
import twoAK.runboyrun.objects.countries.CountriesList;
import twoAK.runboyrun.responses.objects.CountryObject;


public class SignUpActivity extends AppCompatActivity {

    SignUpActivity self = this;

    CountriesList mCountriesList;
    CitiesList mCitiesList;

    CountriesLoadTask mCountriesLoadTask;
    CitiesLoadTask mCitiesLoadTask;

    Spinner mCountrySpinner;
    Spinner mCitySpinner;

    CountryObject   mSelectedCountry;
    CountryObject   mPreviousCountry;
    int mPosSelectedCountry;
    int mPosPreviousCountry;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);

        mCountrySpinner = (Spinner)findViewById(R.id.signup_spinner_country);
        mCitySpinner = (Spinner)findViewById(R.id.signup_spinner_city);

        mCountriesLoadTask = new SignUpActivity.CountriesLoadTask();
        mCountriesLoadTask.execute((Void) null);


    }



    /////////////////////////////////////////////////////////////////////////
    public class CountriesLoadTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog dialog;

        CountriesLoadTask() {
            dialog = ProgressDialog.show(self,null,null);
            dialog.setContentView(R.layout.loader);
            TextView text = (TextView)dialog.findViewById(R.id.signup_dialog_text);
            text.setText("Loading a list of countries");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load countries.");

            mCountriesList = new CountriesList(ApiClient.instance());

            return mCountriesList.load();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            dialog.dismiss();


            if(mCountriesList.getAll() == null) {
                mCountriesLoadTask = new SignUpActivity.CountriesLoadTask();
                mCountriesLoadTask.execute((Void) null);
            };

            // create an adapter and assign the adapter to the list
            CountriesSpinnerAdapter countryAdapter = new CountriesSpinnerAdapter(self, mCountriesList.getAll());
            mCountrySpinner.setAdapter(countryAdapter);

            mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition, long selectedId) {


                    mPosPreviousCountry = mPosSelectedCountry;
                    mPosSelectedCountry = selectedItemPosition;
                    mSelectedCountry = mCountriesList.getByPosition(selectedItemPosition);

                    mCitiesLoadTask = new SignUpActivity.CitiesLoadTask(mSelectedCountry.getCode());
                    mCitiesLoadTask.execute((Void) null);
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


        }

    }

    /////////////////////////////////////////////////////////////////////////
    public class CitiesLoadTask extends AsyncTask<Void, Void, Boolean> {

        private String countryCode;
        private ProgressDialog dialog;

        CitiesLoadTask(String countryCode) {
            this.countryCode = countryCode;

            dialog = ProgressDialog.show(self,null,null);
            dialog.setContentView(R.layout.loader);
            TextView text = (TextView)dialog.findViewById(R.id.signup_dialog_text);
            text.setText("Loading a list of cities");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load cities.");
            mCitiesList = new CitiesList(ApiClient.instance());

            return mCitiesList.load(countryCode);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            dialog.dismiss();

            if(mCitiesList.getAll() == null) {
                mCountrySpinner.setSelection(mPosPreviousCountry);
                mPosSelectedCountry = mPosPreviousCountry;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сities were not loaded. try it again", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            mCitySpinner.setEnabled(true);
            // create an adapter and assign the adapter to the list
            CitiesSpinnerAdapter cityAdapter = new CitiesSpinnerAdapter(self, mCitiesList.getAll());
            mCitySpinner.setAdapter(cityAdapter);



        }

    }
}
