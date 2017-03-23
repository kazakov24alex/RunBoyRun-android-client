package twoAK.runboyrun.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import java.util.Calendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CitiesSpinnerAdapter;
import twoAK.runboyrun.adapters.CountriesSpinnerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.auth.Auth;
import twoAK.runboyrun.objects.cities.CitiesList;
import twoAK.runboyrun.objects.countries.CountriesList;
import twoAK.runboyrun.responses.SignUpResponse;
import twoAK.runboyrun.responses.objects.CityObject;
import twoAK.runboyrun.responses.objects.CountryObject;


public class SignUpActivity extends AppCompatActivity {

    SignUpActivity self = this;
    Auth mAuth;

    EditText mNameEdit;
    EditText mSurnameEdit;

    CountriesList mCountriesList;
    CitiesList mCitiesList;

    CountriesLoadTask mCountriesLoadTask;
    CitiesLoadTask mCitiesLoadTask;
    SignupTask mSignUpTask;

    Spinner mCountrySpinner;
    Spinner mCitySpinner;
    Spinner mSexSpinner;

    Button mBithdayButton;
    private int mYear, mMonth, mDay;

    Button mRegisterButton;

    CountryObject   mSelectedCountry;
    CountryObject   mPreviousCountry;
    int mPosSelectedCountry;
    int mPosPreviousCountry;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = new Auth();

        mCountrySpinner = (Spinner)findViewById(R.id.signup_spinner_country);
        mCitySpinner = (Spinner)findViewById(R.id.signup_spinner_city);
        mSexSpinner = (Spinner)findViewById(R.id.signup_spinner_sex);
        mBithdayButton = (Button)findViewById(R.id.signup_button_birthday);
        mRegisterButton = (Button)findViewById(R.id.signup_button_save);

        mNameEdit = (EditText)findViewById(R.id.signup_editText_name);
        mSurnameEdit = (EditText)findViewById(R.id.signup_editText_surname);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterClick();
            }
        });

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Вызываем адаптер
        mSexSpinner.setAdapter(adapter);

        mCountriesLoadTask = new SignUpActivity.CountriesLoadTask();
        mCountriesLoadTask.execute((Void) null);
    }


    public void onBithdayButtonClick(View view)
    {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        mBithdayButton.setText(editTextDateParam);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void onRegisterClick()
    {
        String identificator = getIntent().getExtras().getString("identificator");
        String password = getIntent().getExtras().getString("password");
        String name = mNameEdit.getText().toString();
        String surname = mSurnameEdit.getText().toString();
        CountryObject countryObject = mCountriesList.getByPosition(mCountrySpinner.getSelectedItemPosition());
        String country = countryObject.getName();
        CityObject cityObject = mCitiesList.getByPosition(mCitySpinner.getSelectedItemPosition());
        String city = cityObject.getName();
        String sex = mSexSpinner.getSelectedItem().toString();
        String birthday = mYear+"-"+mMonth+"-"+mDay;

        System.out.println(identificator + password + name + surname + country + city + birthday + sex);
        mSignUpTask = new SignUpActivity.SignupTask(identificator, password, name, surname, country, city, birthday, sex);
        mSignUpTask.execute((Void) null);
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


    public class SignupTask extends AsyncTask<Void, Void, Boolean> {

        private String identificator;
        private String password;
        private String name;
        private String surname;
        private String country;
        private String city;
        private String bithday;
        private String sex;
        private ProgressDialog dialog;

        SignupTask(String identificator,String password,String name,String surname,String country,
                   String city, String bithday, String sex) {
            this.identificator = identificator;
            this.password = password;
            this.name = name;
            this.surname = surname;
            this.country = country;
            this.city = city;
            this.bithday = bithday;
            this.sex = sex;

            dialog = ProgressDialog.show(self,null,null);
            dialog.setContentView(R.layout.loader);
            TextView text = (TextView)dialog.findViewById(R.id.signup_dialog_text);
            text.setText("Wait for second...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to registrate user.");


            return mAuth.signup(identificator, password, name, surname, country, city, bithday, sex);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignUpTask = null;
            dialog.dismiss();
            if (success){
                //setToken(Auth.getToken());
                startActivity(new Intent(SignUpActivity.this, Activity1.class));
            } else{
                mNameEdit.setError("registration failed");
                mNameEdit.requestFocus();
            }
        }

    }
}
