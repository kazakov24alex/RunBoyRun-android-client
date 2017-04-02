package twoAK.runboyrun.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.auth.Auth;


public class PreSignUpActivity extends AppCompatActivity {

    private Auth mAuth;      // authorization module
    private ProgressDialog mProgressDialog;

    private CheckIdentificatorTask mCheckIdentificatorTask; // task to check free identificator

    private EditText    mEmailView;     // email input field
    private EditText    mPasswordView;  // password input field

    private Button  mSignUpButton;      // button to check EMAIL
    private Button  mSocialNetsButton;  // button to registration by VK oAuth


    private View        mProgressView;  // view of a progress spinner
    private View        mSignUpFormView;// view of SIGNUP form


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_pre_signup);

        // initializing class members
        mAuth = new twoAK.runboyrun.auth.Auth();
        mSignUpFormView = findViewById(R.id.pre_signup_form);
        mEmailView = (EditText) findViewById(R.id.pre_signup_editText_email);
        mPasswordView = (EditText) findViewById(R.id.pre_signup_editText_password);
        mSignUpButton = (Button) findViewById(R.id.pre_signup_button_sign_up);
        mSocialNetsButton = (Button) findViewById(R.id.pre_signup_socialnets_button);
        mProgressView = findViewById(R.id.pre_signup_progress);

        // setting click listener on SIGN UP button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIdentificator();
            }
        });
        mSocialNetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(PreSignUpActivity.this, SocialNetworksAuthActivity.class), 111);
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
            // kick off a background task to perform the user login attempt
            mCheckIdentificatorTask = new CheckIdentificatorTask(identificator, password);
            mCheckIdentificatorTask.execute((Void) null);
        }

    }


    /** This method сhecks the entered password for validity.
     * @param password - verifiable password
     * @return correct or not
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == SignInActivity.RESULT_OK) {
            mCheckIdentificatorTask = new PreSignUpActivity.CheckIdentificatorTask(
                    data.getExtras().getString("id"),
                    data.getExtras().getString("access_token")
            );
            mCheckIdentificatorTask.setProfile(
                    data.getExtras().getString("oauth"),
                    data.getExtras().getString("name"),
                    data.getExtras().getString("surname")
            );
            mCheckIdentificatorTask.execute((Void) null);

        } else {
            Toast.makeText(getApplicationContext(), "Social network data not received" , Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class CheckIdentificatorTask extends AsyncTask<Void, Void, Boolean> {

        private String mOAuth;

        private final String mIdentificator; // entered EMAIL
        private final String mPassword;      // entered PASSWORD

        private String mName;
        private String mSurname;

        CheckIdentificatorTask(String email, String password) {
            showProgress(getString(R.string.presignup_dialog_check_id));
            mOAuth = "own";

            mIdentificator = email;
            mPassword = password;

            mName = null;
            mSurname = null;
        }


        public void setProfile(String OAuth, String name, String surname) {
            mOAuth = OAuth;
            mName = name;
            mSurname = surname;
        }

        /** The specified task - "Send request for check identificator".
         * @return success of the task
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            System.out.println("OAUTH="+mOAuth+" ID="+mIdentificator);

            Log.i("PreSignUpActivity", "Check identificator: "+mIdentificator+".");
            return mAuth.check(mOAuth, mIdentificator);
        }

        /** Actions after task execution
         * @param success - success of the task
         *  */
        @Override
        protected void onPostExecute(final Boolean success) {
            // reset the task and hide a progress spinner
            mCheckIdentificatorTask = null;
            hideProgressDialog();

            if (success) {
                // show the error and focus on the wrong field
                mEmailView.setError(getString(R.string.presignup_error_user_exist));
                mEmailView.requestFocus();
            } else {
                // go to the SIGN UP activity and send identificator and password there
                Intent intent = new Intent(PreSignUpActivity.this, SignUpActivity.class);
                intent.putExtra("oauth", mOAuth);
                intent.putExtra("identificator", mIdentificator);
                intent.putExtra("password", mPassword);
                intent.putExtra("name", mName);
                intent.putExtra("surname", mSurname);
                startActivity(intent);
            }
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            mCheckIdentificatorTask = null;
            hideProgressDialog();
        }
    }


    /**
     * Shows the progress UI.
     */
    protected void showProgress(String message) {
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

}
