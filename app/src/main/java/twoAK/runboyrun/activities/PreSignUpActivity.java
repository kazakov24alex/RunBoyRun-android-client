package twoAK.runboyrun.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.auth.Auth;
import twoAK.runboyrun.exceptions.api.CheckFailedException;


/**
 * Activity directs to registration via own registration system and via social networks.
 */
public class PreSignUpActivity extends AppCompatActivity {
    public int PRESIGNUP_ACTIVITY_REQUEST_CODE = 100;

    private Auth mAuth;      // authorization module
    private ProgressDialog mProgressDialog;

    private CheckIdentificatorTask mCheckIdentificatorTask; // task to check free identificator

    private EditText    mEmailView;     // email input field
    private EditText    mPasswordView;  // password input field

    private Button  mSignUpButton;      // button to check EMAIL
    private Button  mSocialNetsButton;  // button to registration by VK oAuth


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_pre_signup);
        mAuth = new Auth();

        // initializing class members
        mEmailView = (EditText) findViewById(R.id.presignup_edittext_email);
        mPasswordView = (EditText) findViewById(R.id.presignup_edittext_password);
        mSignUpButton = (Button) findViewById(R.id.presignup_button_sign_up);
        mSocialNetsButton = (Button) findViewById(R.id.presignup_button_socialnets_signup);

        // setting click listeners
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIdentificator()) {
                    signUpAttempt();
                }
            }
        });
        mSocialNetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(PreSignUpActivity.this, SocialNetworksAuthActivity.class),
                        PRESIGNUP_ACTIVITY_REQUEST_CODE
                );
            }
        });
    }

    /** This method сhecks if this identifier has already been registered.
     * @return (boolean) success - success of checking
     */
    private boolean checkIdentificator() {
        View focusView = null;      // focus on field with error

        // no сheck if the task is already running
        if (mCheckIdentificatorTask != null) {
            return false;
        }

        // check for a valid email
        String identificator = mEmailView.getText().toString().trim();
        mEmailView.setError(null);
        if (!isEmailValid(identificator)) {
            mEmailView.setError(getString(R.string.presignup_error_incorrect_email));
            focusView = mEmailView;
            focusView.requestFocus();
            return false;
        }

        // check for a valid password, if the user entered one
        String password = mPasswordView.getText().toString().trim();
        mPasswordView.setError(null);
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.presignup_error_incorrect_password));
            focusView = mPasswordView;
            focusView.requestFocus();
            return false;
        }

        return true;
    }


    /** This method сhecks the entered email for validity.
     * @param email - verifiable email
     * @return (boolean) correct or not
     */
    private boolean isEmailValid(String email) {
        return email.matches("^[-\\w.]+@([A-z0-9][-A-z0-9]+\\.)+[A-z]{2,4}$");
    }


    /** This method сhecks the entered password for validity.
     * Lowercase and uppercase Latin letters, numbers, special characters. Minimum 6 characters.
     * @param password - verifiable password
     * @return (boolean) correct or not
     */
    private boolean isPasswordValid(String password) {
        return password.matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
    }


    /** This method tries to initialize registation with the data from EMAIL and PASSWORD fields. */
    private void signUpAttempt() {
        String identificator    = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        // kick off a background task to perform the user login attempt
        mCheckIdentificatorTask = new CheckIdentificatorTask(identificator, password);
        mCheckIdentificatorTask.execute((Void) null);
    }


    /**
     * This method gets data from SocialNetworksAuthActivity from social networks accounts of users.
     * And after that launch CheckIdentificatorTask
     * @param requestCode   - code=100 of request from this activity
     * @param resultCode    - RESULT_OK, if it was success
     * @param data          - contains the following extras:
     *                          "oauth" - authorizing social network
     *                          "id" - user id from the social network
     *                          "access_token" - access token of the user
     *                          "name" - name of user from social network
     *                          "surname" - surname of user from social network
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == PRESIGNUP_ACTIVITY_REQUEST_CODE) && (resultCode == SignInActivity.RESULT_OK)) {
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
            Toast.makeText(getApplicationContext(),
                    getString(R.string.presignup_toast_socialnets_no_data), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Represents an asynchronous task of checking uniqueness of the user identificator.
     */
    private class CheckIdentificatorTask extends AsyncTask<Void, Void, Boolean> {
        private String errMes;

        private String mOAuth;  // type of authorization

        private final String mIdentificator; // entered EMAIL
        private final String mPassword;      // entered PASSWORD

        private String mName;
        private String mSurname;

        CheckIdentificatorTask(String identificator, String password) {
            showProgress(getString(R.string.presignup_dialog_check_id));
            errMes = getString(R.string.presignup_toast_id_not_unique);
            mOAuth = "own";

            mIdentificator = identificator;
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
            Log.i("PreSignUpActivity", "Try to check identificator");
            try {
                return mAuth.check(mOAuth, mIdentificator);
            } catch(CheckFailedException e) {
                return false;
            }
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
                Log.i("PreSignUpActivity", "Identificator is not unique");
                // show the error and focus on the wrong field
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_LONG).show();
            } else {
                Log.i("PreSignUpActivity", "Identificator is unique");
                // go to the SIGN UP activity and send user data thereto
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


    /** Show the progress dialog.*/
    protected void showProgress(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    /** Hide the progress dialog.*/
    protected void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

}
