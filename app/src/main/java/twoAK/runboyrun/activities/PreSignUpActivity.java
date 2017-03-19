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

import twoAK.runboyrun.R;
import twoAK.runboyrun.auth.Auth;

import static twoAK.runboyrun.auth.Auth.setToken;


public class PreSignUpActivity extends AppCompatActivity {

    private Auth            mAuth;      // authorization module
    private CheckEmailTask  mCheckEmailTask; // task to check free identificator

    private EditText    mEmailView;     // email input field
    private EditText    mPasswordView;  // password input field
    private Button      mSignUpButton;  // button to check EMAIL

    private View        mProgressView;  // view of a progress spinner
    private View        mSignUpFormView;// view of SIGNUP form


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_pre_signup);

        // initializing class members
        mAuth           = new Auth();
        mEmailView      = (EditText) findViewById(R.id.pre_signup_editText_email);
        mPasswordView   = (EditText) findViewById(R.id.pre_signup_editText_password);
        mSignUpButton   = (Button) findViewById(R.id.pre_signup_button_sign_up);
        mSignUpFormView = findViewById(R.id.pre_signup_form);
        mProgressView   = findViewById(R.id.pre_signup_progress);

        // setting click listener on SIGN UP button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIdentificator();
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
        if (mCheckEmailTask != null) {
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
            mCheckEmailTask = new PreSignUpActivity.CheckEmailTask(identificator);
            mCheckEmailTask.execute((Void) null);
        }

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

        private final String mIdentificator; // entered EMAIL

        CheckEmailTask(String email) {
            mIdentificator = email;
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
            mCheckEmailTask = null;
            showProgress(false);

            if (success) {
                // set the received token and go to the "NewsFeed" activity
                setToken(Auth.getToken());
                startActivity(new Intent(PreSignUpActivity.this, Activity1.class));
            } else {
                // show the error and focus on the wrong field
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            mCheckEmailTask = null;
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
