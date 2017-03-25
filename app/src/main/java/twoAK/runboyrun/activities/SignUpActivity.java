package twoAK.runboyrun.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CitiesSpinnerAdapter;
import twoAK.runboyrun.adapters.CountriesSpinnerAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.auth.Auth;
import twoAK.runboyrun.objects.cities.CitiesList;
import twoAK.runboyrun.objects.countries.CountriesList;
import twoAK.runboyrun.responses.objects.CityObject;
import twoAK.runboyrun.responses.objects.CountryObject;


public class SignUpActivity extends AppCompatActivity {

    SignUpActivity self = this;
    private ProgressDialog mDialog;
    private TextView mDialogText;
    private Auth mAuth;

    // Views
    private EditText mNameEdit;
    private EditText mSurnameEdit;
    private Spinner mCountrySpinner;
    private Spinner mCitySpinner;
    private Spinner mSexSpinner;
    private Button mBithdayButton;
    private Button mRegisterButton;

    // Values
    private String mOAuth;
    private String mIdentificator;
    private String mPassword;
    private String mName;
    private String mSurname;
    private String mCountry;
    private String mCity;
    private String mBirthday;
    private int    mSex;

    // Containers
    private CountriesList   mCountriesList;
    private CitiesList      mCitiesList;
    private CountryObject   mSelectedCountry;
    private CountryObject   mPreviousCountry;
    private int mPosSelectedCountry;
    private int mPosPreviousCountry;

    private int mYear, mMonth, mDay;

    // AsyncTasks
    private CountryLoadingTask mCountryLoadingTask;
    private CitiesLoadTask mCitiesLoadTask;
    private SignupTask mSignUpTask;



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);
        mDialog = ProgressDialog.show(self,null,null);
        mDialog.setContentView(R.layout.loader);
        mDialogText = (TextView) mDialog.findViewById(R.id.signup_dialog_text);
        mAuth = new Auth();

        // obtaining values of Identificator and Password from the previous activity
        mIdentificator  = getIntent().getExtras().getString("identificator");
        mPassword       = getIntent().getExtras().getString("password");
        mOAuth          = getIntent().getExtras().getString("oauth");

        // initialization of views
        mNameEdit       = (EditText)findViewById(R.id.signup_editText_name);
        mSurnameEdit    = (EditText)findViewById(R.id.signup_editText_surname);
        mCountrySpinner = (Spinner)findViewById(R.id.signup_spinner_country);
        mCitySpinner    = (Spinner)findViewById(R.id.signup_spinner_city);
        mBithdayButton  = (Button)findViewById(R.id.signup_button_birthday);
        mSexSpinner     = (Spinner)findViewById(R.id.signup_spinner_sex);
        mRegisterButton = (Button)findViewById(R.id.signup_button_save);

        // function-handlers
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterClick();
            }
        });
        mBithdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBithdayButtonClick(view);
            }
        });

        // setting listenets on views
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSexSpinner.setAdapter(adapter);

        // set current date on TImePicker
        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = calendar.get(Calendar.MONTH);
        mYear = calendar.get(Calendar.YEAR);

        // registration form autocomplete, if register via socail network
        if(mOAuth != "own") {
            formAutocomplete();
        }

        // starting countries loading task
        mCountryLoadingTask = new CountryLoadingTask();
        mCountryLoadingTask.execute((Void) null);

    }


    private void formAutocomplete() {
        // obtaining values from the previous activity
        mName       = getIntent().getExtras().getString("name");
        mSurname    = getIntent().getExtras().getString("surname");
        mCountry    = getIntent().getExtras().getString("country");
        mCity       = getIntent().getExtras().getString("city");
        mBirthday   = getIntent().getExtras().getString("birthday");
        mSex        = getIntent().getExtras().getInt("sex");


        // set values to NameField and SurnameField
        if(mName != null)
            mNameEdit.setText(mName);
        if(mName != null)
            mSurnameEdit.setText(mSurname);

        // set value to SetSpinner (VK: 1-famale, 2-male, APP: 0-male, 1-female)
        if(mSex != -1)
            mSexSpinner.setSelection(mSex);


        // set value to DatePicker of Birthday button
        if(mBirthday != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date parsingDate = new Date();
            try {
                parsingDate = dateFormat.parse(mBirthday);
            } catch (ParseException e) {
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsingDate);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mMonth = calendar.get(Calendar.MONTH);
            mYear = calendar.get(Calendar.YEAR);
            mBithdayButton.setText(getString(R.string.signup_birthday)+": "+mDay+"."+mMonth+"."+mYear);
        }

    }


    public void onBithdayButtonClick(View view)
    {
        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        mBithdayButton.setText(getString(R.string.signup_birthday)+": "+editTextDateParam);
                        mDay = dayOfMonth;
                        mMonth = monthOfYear;
                        mYear = year;
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void onRegisterClick()
    {
        CountryObject countryObject = mCountriesList.getByPosition(mCountrySpinner.getSelectedItemPosition());
        CityObject cityObject = mCitiesList.getByPosition(mCitySpinner.getSelectedItemPosition());

        String name     = mNameEdit.getText().toString();
        String surname  = mSurnameEdit.getText().toString();
        String country  = countryObject.getName();
        String city     = cityObject.getName();
        String sex      = mSexSpinner.getSelectedItem().toString();
        String birthday = mYear+"-"+mMonth+"-"+mDay;

        // TODO: DEBUG
        System.out.println(mOAuth + mIdentificator + mPassword + name + surname + country + city + birthday + sex);
        mSignUpTask = new SignUpActivity.SignupTask(mOAuth, mIdentificator, mPassword, name, surname, country, city, birthday, sex);
        mSignUpTask.execute((Void) null);
    }


    /////////////////////////////////////////////////////////////////////////
    public class CountryLoadingTask extends AsyncTask<Void, Void, Boolean> {


        CountryLoadingTask() {
            mDialogText.setText("Loading a list of countries");
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load countries.");

            mCountriesList = new CountriesList(ApiClient.instance());

            return mCountriesList.load();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mDialog.dismiss();


            if(mCountriesList.getAll() == null) {
                mCountryLoadingTask = new CountryLoadingTask();
                mCountryLoadingTask.execute((Void) null);
            };

            // create an adapter and assign the adapter to the list
            CountriesSpinnerAdapter countryAdapter = new CountriesSpinnerAdapter(self, mCountriesList.getAll());
            mCountrySpinner.setAdapter(countryAdapter);

            int countryFromSN = mCountriesList.getPositionByTitle(mCountry);
            if(countryFromSN != -1) {
                mCountrySpinner.setSelection(countryFromSN);
            }

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

        CitiesLoadTask(String countryCode) {
            this.countryCode = countryCode;

            mDialogText.setText("Loading a list of cities");
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load cities.");
            mCitiesList = new CitiesList(ApiClient.instance());

            return mCitiesList.load(countryCode);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            mDialog.dismiss();

            if(mCitiesList.getAll() == null) {
                mCountrySpinner.setSelection(mPosPreviousCountry);
                mPosSelectedCountry = mPosPreviousCountry;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сities were not loaded. try it again", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }


            // create an adapter and assign the adapter to the list
            CitiesSpinnerAdapter cityAdapter = new CitiesSpinnerAdapter(self, mCitiesList.getAll());
            mCitySpinner.setAdapter(cityAdapter);

            int cityFromSN = mCitiesList.getPositionByTitle(mCity);
            if(cityFromSN != -1) {
                mCitySpinner.setSelection(cityFromSN);
            }

        }

    }


    public class SignupTask extends AsyncTask<Void, Void, Boolean> {

        private String OAuth;
        private String identificator;
        private String password;
        private String name;
        private String surname;
        private String country;
        private String city;
        private String bithday;
        private String sex;

        SignupTask(String OAuth, String identificator,String password,String name,String surname,String country,
                   String city, String bithday, String sex) {
            this.OAuth          = OAuth;
            this.identificator  = identificator;
            this.password       = password;
            this.name           = name;
            this.surname        = surname;
            this.country        = country;
            this.city           = city;
            this.bithday        = bithday;
            this.sex            = sex;


            mDialogText.setText("Wait for second...");
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to registrate user.");


            return mAuth.signup(OAuth, identificator, password, name, surname, country, city, bithday, sex);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignUpTask = null;
            mDialog.dismiss();
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
