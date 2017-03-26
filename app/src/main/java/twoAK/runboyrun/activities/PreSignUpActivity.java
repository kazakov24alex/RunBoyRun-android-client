package twoAK.runboyrun.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import twoAK.runboyrun.R;
import twoAK.runboyrun.auth.Auth;


public class PreSignUpActivity extends AppCompatActivity {

    private Auth            mAuth;      // authorization module
    private CheckEmailTask mCheckIdentificatorTask; // task to check free identificator

    private EditText    mEmailView;     // email input field
    private EditText    mPasswordView;  // password input field
    private Button      mSignUpButton;  // button to check EMAIL
    private Button      mVKSignUpButton;// button to registration by VK oAuth

    private View        mProgressView;  // view of a progress spinner
    private View        mSignUpFormView;// view of SIGNUP form


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_pre_signup);

        // initializing class members
        mAuth           = new Auth();
        mSignUpFormView = findViewById(R.id.pre_signup_form);
        mEmailView      = (EditText) findViewById(R.id.pre_signup_editText_email);
        mPasswordView   = (EditText) findViewById(R.id.pre_signup_editText_password);
        mSignUpButton   = (Button) findViewById(R.id.pre_signup_button_sign_up);
        mVKSignUpButton = (Button) findViewById(R.id.pre_signup_vk_button);
        mProgressView   = findViewById(R.id.pre_signup_progress);

        // setting click listener on SIGN UP button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIdentificator();
            }
        });
        mVKSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oAuthVK();
            }
        });
    }

    /** This method сhecks if this identifier has already been registered.
     *
     */
    private void checkIdentificator() {
        boolean cancelFlag = false; // attempt cancellation flag
        View focusView = null;      // focus on field with error

        // сheck if the task is already running
        if (mCheckIdentificatorTask != null) {
            return;
        }

        // store values at the time of the identificator attempt
        String identificator    = mEmailView.getText().toString().trim();
        String password         = mPasswordView.getText().toString().trim();

        mEmailView.setError(null);
        mPasswordView.setError(null);

        // check for a valid password, if the user entered one
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancelFlag = true;
        }

        // check for a valid email
        if (TextUtils.isEmpty(identificator)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancelFlag = true;
        }

        if (cancelFlag) {
            // there was an error; don't attempt check identificator and focus the first form field with an error
            focusView.requestFocus();
        } else {
            // show a progress spinner and kick off a background task to perform the user login attempt
            showProgress(true);
            mCheckIdentificatorTask = new PreSignUpActivity.CheckEmailTask(identificator, password);
            mCheckIdentificatorTask.execute((Void) null);
        }

    }


    private void oAuthVK() {
        //startActivity(new Intent(PreSignUpActivity.this, VKSignUpActivity.class));
        String[] scope = new String[] {VKScope.OFFLINE };
        VKSdk.login(this, scope);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(getApplicationContext(), res.accessToken, Toast.LENGTH_LONG).show();

                checkSocialAccount(res.userId, res.accessToken);
            }

            @Override
            public void onError(VKError err) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
            }

        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void checkSocialAccount(final String userId, final String accessToken) {
        VKParameters parameters = VKParameters.from(VKApiConst.ACCESS_TOKEN, accessToken);
        VKRequest request = new VKRequest("account.getProfileInfo", parameters);
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                mCheckIdentificatorTask = new PreSignUpActivity.CheckEmailTask(userId, accessToken);

                String OAuth = "vk";
                String name = null;
                String surname = null;
                String country = null;
                String city = null;
                String birthday = null;
                int sex = 0;


                try {
                    JSONObject jsonObject = response.json.getJSONObject("response");
                    name = jsonObject.getString("first_name");
                    surname = jsonObject.getString("last_name");
                    country = jsonObject.getJSONObject("country").getString("title");
                    city = jsonObject.getJSONObject("city").getString("title");
                    birthday = jsonObject.getString("bdate");
                    sex = jsonObject.getInt("sex");
                } catch (JSONException e) { }

                // TODO: country cast
                if(country.equals("Russia"))
                    country = "Russian Federation";

                // sex format cast from VK form to APP form
                if(sex == 0)
                    sex = -1;
                else if (sex == 2)
                    sex = 0;

                //TODO: DEBUG
                System.out.println("TOKEN = "+accessToken);

                // show a progress spinner and kick off a background task to perform the user login attempt
                showProgress(true);
                mCheckIdentificatorTask.setProfile(OAuth, name, surname, country, city, birthday, sex);
                mCheckIdentificatorTask.execute((Void) null);

            }
        });
    }

    /** This method сhecks the entered password for validity.
     * @param password - verifiable password
     * @return correct or not
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class CheckEmailTask extends AsyncTask<Void, Void, Boolean> {

        private String mOAuth;

        private final String mIdentificator; // entered EMAIL
        private final String mPassword;      // entered PASSWORD

        private String mName;
        private String mSurname;
        private String mCountry;
        private String mCity;
        private String mBirthday;
        private int    mSex;

        CheckEmailTask(String email, String password) {
            mOAuth = "own";

            mIdentificator = email;
            mPassword = password;

            mName = null;
            mSurname = null;
            mCountry = null;
            mCity = null;
            mBirthday = null;
            mSex = 0;
        }


        public void setProfile(String OAuth, String name, String surname, String country, String city, String birthday, int sex) {
            mOAuth = OAuth;
            mName = name;
            mSurname = surname;
            mCountry = country;
            mCity = city;
            mBirthday = birthday;
            mSex = sex;
        }

        /** The specified task - "Send request for check identificator".
         * @return success of the task
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("PreSignUpActivity", "Check identificator: "+mIdentificator+".");
            return mAuth.check(mIdentificator);
        }

        /** Actions after task execution
         * @param success - success of the task
         *  */
        @Override
        protected void onPostExecute(final Boolean success) {
            // reset the task and hide a progress spinner
            mCheckIdentificatorTask = null;
            showProgress(false);

            if (success) {
                // show the error and focus on the wrong field
                mEmailView.setError(getString(R.string.error_identificator_busy));
                mEmailView.requestFocus();

            } else {
                // go to the SIGN UP activity and send identificator and password there
                Intent intent = new Intent(PreSignUpActivity.this, SignUpActivity.class);
                intent.putExtra("oauth", mOAuth);
                intent.putExtra("identificator", mIdentificator);
                intent.putExtra("password", mPassword);
                intent.putExtra("name", mName);
                intent.putExtra("surname", mSurname);
                intent.putExtra("country", mCountry);
                intent.putExtra("city", mCity);
                intent.putExtra("birthday", mBirthday);
                intent.putExtra("sex", mSex);
                startActivity(intent);
            }
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            mCheckIdentificatorTask = null;
            showProgress(false);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
