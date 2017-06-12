package twoAK.runboyrun.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private ProgressDialog mProgressDialog;
    private Auth mAuth;

    // Views
    private TextView mTitle;
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
        mAuth = new Auth();

        // obtaining values of Identificator and Password from the previous activity
        mIdentificator  = getIntent().getExtras().getString("identificator");
        mPassword       = getIntent().getExtras().getString("password");
        mOAuth          = getIntent().getExtras().getString("oauth");

        // initialization of views
        mTitle          = (TextView)findViewById(R.id.signup_text_title);
        mNameEdit       = (EditText)findViewById(R.id.signup_editText_name);
        mSurnameEdit    = (EditText)findViewById(R.id.signup_editText_surname);
        mCountrySpinner = (Spinner)findViewById(R.id.signup_spinner_country);
        mCitySpinner    = (Spinner)findViewById(R.id.signup_spinner_city);
        mBithdayButton  = (Button)findViewById(R.id.signup_button_birthday);
        mSexSpinner     = (Spinner)findViewById(R.id.signup_spinner_sex);
        mRegisterButton = (Button)findViewById(R.id.signup_button_save);


        // set current date on TImePicker
        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = calendar.get(Calendar.MONTH);
        mYear = calendar.get(Calendar.YEAR);

        // function-handlers
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation() == true) {
                    onRegisterClick();
                }
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

        Log.i("RUN-BOY-RUN", "OAUTH  = "+mOAuth);
        // standart - own. Registration form autocomplete, if register via social network.
        if(!mOAuth.equals("own")) { formAutocomplete(); }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // starting countries loading task
        mCountryLoadingTask = new CountryLoadingTask();
        mCountryLoadingTask.execute((Void) null);

    }

    /** Validation for fields: name, surname, birthday */
    public boolean validation(){
        View focusView = null;

        String name = mNameEdit.getText().toString().trim();
        String surname = mSurnameEdit.getText().toString().trim();

        if (TextUtils.isEmpty(name) || !isNameValid(name)) {
            mNameEdit.setError(getString(R.string.error_invalid_name));
            focusView = mNameEdit;
            focusView.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(surname) || !isNameValid(surname)) {
            mSurnameEdit.setError(getString(R.string.error_invalid_surname));
            focusView = mSurnameEdit;
            focusView.requestFocus();
            return false;
        }

        Calendar date = Calendar.getInstance();
        date.set(mYear,mMonth,mDay);
        Calendar curDate = Calendar.getInstance();
        if (date.before(curDate) == false){
            Toast toast = Toast.makeText(getApplicationContext(),
                   getString(R.string.error_invalid_birthday), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        return true;
    }


    private boolean isNameValid(String name) {
        return name.matches("^[a-zA-Z][a-zA-Z-]{1,20}$");
    }


    /** The method autocomplete registration form if registration via social network. */
    private void formAutocomplete() {

        if(mOAuth.equals("vk")) {
            mTitle.setText(mTitle.getText().toString()+" VIA VK");
            mTitle.setBackgroundResource(R.color.vk_color);
            mTitle.setTextColor(getResources().getColor(R.color.vk_white));
        } else if(mOAuth.equals("facebook")) {
            mTitle.setText(mTitle.getText().toString()+" VIA FACEBOOK");
            mTitle.setBackgroundResource(R.color.facebook);
            mTitle.setTextColor(getResources().getColor(R.color.vk_white));
        } else if(mOAuth.equals("google")) {
            mTitle.setText(mTitle.getText().toString()+" VIA GOOGLE PLUS");
            mTitle.setBackgroundResource(R.color.googleplus);
            mTitle.setTextColor(getResources().getColor(R.color.vk_white));
        }

        // obtaining values from the previous activity
        mName       = getIntent().getExtras().getString("name");
        mSurname    = getIntent().getExtras().getString("surname");

        // set values to NameField and SurnameField
        if(mName != null)
            mNameEdit.setText(mName);
        if(mSurname != null)
            mSurnameEdit.setText(mSurname);

    }

    /** BIRTHDAY button mHandler */
    public void onBithdayButtonClick(View view)
    {
        // date picker dialog initialization
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


    /** REGISTER button mHandler */
    public void onRegisterClick()
    {
        // collection of all data specified in the registration form
        CountryObject countryObject = mCountriesList.getByPosition(mCountrySpinner.getSelectedItemPosition());
        CityObject cityObject = mCitiesList.getByPosition(mCitySpinner.getSelectedItemPosition());
        String name     = mNameEdit.getText().toString();
        String surname  = mSurnameEdit.getText().toString();
        String country  = countryObject.getName();
        String city     = cityObject.getName();
        String sex      = mSexSpinner.getSelectedItem().toString();
        mMonth++;
        String birthday = mYear+"-"+mMonth+"-"+mDay;

        // initialization and starting task for registration
        mSignUpTask = new SignUpActivity.SignupTask(mOAuth, mIdentificator, mPassword, name, surname, country, city, birthday, sex);
        mSignUpTask.execute((Void) null);
    }


    protected void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        mProgressDialog.dismiss();
    }


    /**
     * Represents an asynchronous country list loading task used to fill COUNTRY SPINNER.
     */
    public class CountryLoadingTask extends AsyncTask<Void, Void, Boolean> {

        CountryLoadingTask() {
            // show loading dailog
            showProgressDialog(getString(R.string.signup_dialog_country_loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load countries.");

            // loading country list
            mCountriesList = new CountriesList(ApiClient.instance());
            return mCountriesList.load();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            hideProgressDialog();

            // check success of country loading
            if(mCountriesList == null) {
                mCountryLoadingTask = new CountryLoadingTask();
                mCountryLoadingTask.execute((Void) null);
            };

            // create an adapter and assign the adapter to the list
            CountriesSpinnerAdapter countryAdapter = new CountriesSpinnerAdapter(self, mCountriesList.getAll());
            mCountrySpinner.setAdapter(countryAdapter);


            // mHandler of country spinner select
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

    /**
     * Represents an asynchronous city list loading task used to fill CITY SPINNER.
     */
    public class CitiesLoadTask extends AsyncTask<Void, Void, Boolean> {

        private String countryCode; // base country


        CitiesLoadTask(String countryCode) {
            this.countryCode = countryCode;

            showProgressDialog(getString(R.string.signup_dialog_city_loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to load cities.");

            // loading city list
            mCitiesList = new CitiesList(ApiClient.instance());
            return mCitiesList.load(countryCode);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            hideProgressDialog();

            // check success of country loading
            if(mCitiesList == null) {
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
        }

    }


    /**
     * Represents an asynchronous sign up task used to user registration.
     */
    public class SignupTask extends AsyncTask<Void, Void, Boolean> {

        private String mToken;

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
            this.mToken         = null;

            this.OAuth          = OAuth;
            this.identificator  = identificator;
            this.password       = password;
            this.name           = name;
            this.surname        = surname;
            this.country        = country;
            this.city           = city;
            this.bithday        = bithday;
            this.sex            = sex;

            showProgressDialog(getString(R.string.signup_dialog_registration_waiting));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignUpActivity", "Trying to registrate user.");

            // sending request to registration
            mToken = mAuth.signup(OAuth, identificator, password, name, surname, country, city, bithday, sex);
            if(mToken == null) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignUpTask = null;
            hideProgressDialog();

            // check success of task
            if (success){
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("token", mToken);
                editor.commit();
                Auth.setToken(mToken);
                startActivity(new Intent(SignUpActivity.this, StartNewActivityActivity.class));
            } else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Registration failed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}
