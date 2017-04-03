package twoAK.runboyrun.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import twoAK.runboyrun.R;
import twoAK.runboyrun.auth.Auth;


public class WelcomeActivity extends AppCompatActivity {

    private View        mProgressView;  // view of a progress spinner
    private View        mFormView; // view of login form

    Button mSignInButton;
    Button mSignUpButton;

    CheckTokenTask mCheckTokenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mFormView  = findViewById(R.id.welcome_form);
        mProgressView   = findViewById(R.id.welcome_progressbar_check_token);

        mSignInButton = (Button) findViewById(R.id.welcome_button_signin);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
            }
        });

        mSignUpButton = (Button) findViewById(R.id.welcome_button_signup);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, PreSignUpActivity.class));
            }
        });

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");

        if(token.equals("") == false) {
            mCheckTokenTask = new WelcomeActivity.CheckTokenTask(token);
            mCheckTokenTask.execute((Void) null);;
        }

    }


    public class CheckTokenTask extends AsyncTask<Void, Void, Boolean> {

        private String token;

        CheckTokenTask(String token) {
            // show loading dailog
            showProgress(true);

            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("WelcomeActivity", "Trying to check token on server.");
            Auth mAuth = new Auth();
            return mAuth.checkToken(token);

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success) {
                startActivity(new Intent(WelcomeActivity.this, Activity1.class));
            } else {
                showProgress(false);
            }

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

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
