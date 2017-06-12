package twoAK.runboyrun.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.auth.Auth;
import twoAK.runboyrun.exceptions.api.LoginFailedException;


/**
 * Activity offers login via own login system and via social networks.
*/
public class SignInActivity extends AppCompatActivity {
    public int SIGNIN_ACTIVITY_REQUEST_CODE = 111;

    private Auth        mAuth;              // authorization module
    private SignInTask  mSignInTask;        // task to attempt sign in (own)

    private EditText    mIdentificatorView; // email input field
    private EditText    mPasswordView;      // password input field

    private Button      mSignInButton;      // button to attempt sign in
    private Button      mSNSignInButton;    // button to attempt sign in via VK

    private ProgressDialog mProgressDialog; // view of a progress spinner


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = new Auth();

        // View initialization
        final FrameLayout mLogo = (FrameLayout) findViewById(R.id.signin_logo);

        mIdentificatorView  = (EditText) findViewById(R.id.signin_editText_identificator);
        mPasswordView       = (EditText) findViewById(R.id.signin_editText_password);
        mSignInButton       = (Button) findViewById(R.id.signin_button_sendSignIn);
        mSNSignInButton     = (Button) findViewById(R.id.signin_button_socialnets_sign_in);

        SquareImageView mLogoImage = (SquareImageView) findViewById(R.id.welcome_logo_image);
        mLogoImage.setImageResource(R.drawable.logo);
        // Setting click listeners
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation()) {
                    signInAttempt();
                }
            }
        });
        mSNSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SignInActivity.this, SocialNetworksAuthActivity.class),
                        SIGNIN_ACTIVITY_REQUEST_CODE);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    /** This method checks syntax correctness of identificator and password field.
     * @return (boolean) success - success of checking
     */
    private boolean validation() {
        View focusView = null; // focus on field with error

        // сheck if the task is already running
        if(mSignInTask != null) {
            return false;
        }

        // check for a valid email
        String email = mIdentificatorView.getText().toString().trim();
        mIdentificatorView.setError(null);
        if (!isEmailValid(email)) {
            mIdentificatorView.setError(getString(R.string.signin_error_incorrect_email));
            focusView = mIdentificatorView;
            focusView.requestFocus();
            return false;
        }

        // check for a valid password, if the user entered one
        String password = mPasswordView.getText().toString().trim();
        mPasswordView.setError(null);
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.signin_error_incorrect_password));
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


    /** This method tries to login with the data available in the EMAIL and PASSWORD fields. */
    private void signInAttempt() {
        String identificator = mIdentificatorView.getText().toString().trim();
        String password      = mPasswordView.getText().toString().trim();

        mSignInTask = new SignInActivity.SignInTask("own", identificator, password);
        mSignInTask.execute((Void) null);
    }


    /**
     * This method gets data from SocialNetworksAuthActivity from social networks accounts of users.
     * @param requestCode   - code=111 of request from this activity
     * @param resultCode    - RESULT_OK, if it was success
     * @param data          - contains the following extras:
     *                          "oauth" - authorizing social network
     *                          "id" - user id from the social network
     *                          "access_token" - access token of the user
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == SIGNIN_ACTIVITY_REQUEST_CODE) && (resultCode == SignInActivity.RESULT_OK)) {
            mSignInTask = new SignInActivity.SignInTask(
                    data.getExtras().getString("oauth"),
                    data.getExtras().getString("id"),
                    data.getExtras().getString("access_token")
            );
            mSignInTask.execute((Void) null);

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.signin_toast_socialnets_no_data) , Toast.LENGTH_LONG).show();
        }
    }



    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class SignInTask extends AsyncTask<Void, Void, Boolean> {
        private String mToken;  // received token
        private String errMes;  // error message possible

        private final String mOAuth;            // type of authorization
        private final String mIdentificator;    // entered EMAIL
        private final String mPassword;         // entered PASSWORD

        SignInTask(String oauth, String email, String password) {
            showProgress(getString(R.string.signin_dialog_login));
            mToken = null;
            errMes = null;

            mOAuth = oauth;
            mIdentificator = email;
            mPassword = password;
        }


        /** The specified task - "Send request for login".
         * @return success of the task
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("SignInActivity", "Trying to login.");
            try {
                mToken = mAuth.signin(mOAuth, mIdentificator, mPassword);
                return true;
            } catch(LoginFailedException e) {
                errMes = getString(R.string.signin_toast_incorrent_login_data);
            }
            return false;
        }


        /** Actions after task execution
         * @param success - success of the task
         *  */
        @Override
        protected void onPostExecute(final Boolean success) {
            // reset the task and hide a progressbar
            mSignInTask = null;
            hideProgressDialog();

            if (success) {
                Log.i("SignInActivity", "Login was success.");

                // set the received token and go to the "NewsFeed" activity
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("token", mToken).commit();
                editor.commit();
                Auth.setToken(mToken);
                startActivity(new Intent(SignInActivity.this, StartNewActivityActivity.class));
            } else {
                Log.i("SignInActivity", "Login is unsuccessful.");
                // show the error and focus on the wrong field
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_LONG).show();
            }
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            mSignInTask = null;
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
