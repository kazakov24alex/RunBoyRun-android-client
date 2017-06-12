package twoAK.runboyrun.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.auth.Auth;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;


/**
 * Starting activity of the application.
 * Directs user to 'News feed' if token is available (not expired).
 * If token expired, invites user to login or register.
 * */
public class WelcomeActivity extends AppCompatActivity {

    private View    mProgressView;  // progress circle progress bar
    private View    mFormView;      // view of UI form (SignIn and SignUp buttons)
    private FrameLayout mLogoView;
    private SquareImageView mLogo;

    Button mSignInButton;   // SignIn: directs to SignIn activity
    Button mSignUpButton;   // SignUp: directs to PreSignUp activity

    CheckTokenTask mCheckTokenTask; // task of checking whether the token has expired


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // View initialization
        mFormView  = findViewById(R.id.welcome_form);
        mLogoView = (FrameLayout) findViewById(R.id.welcome_logo);

        mLogo = (SquareImageView) findViewById(R.id.welcome_logo_image);
        mLogo.setImageResource(R.drawable.logo);


        mSignInButton = (Button) findViewById(R.id.welcome_button_signin);
        mSignUpButton = (Button) findViewById(R.id.welcome_button_signup);

        // On button click listeners
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, PreSignUpActivity.class));
            }
        });

        // Getting token from application storage and launching CheckTokenTask, if it is available
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");
        if(token.equals("") == false) {
            mCheckTokenTask = new WelcomeActivity.CheckTokenTask(token);
            mCheckTokenTask.execute((Void) null);;
        }

    }


    /**
     * Represents an asynchronous correctness token checking task.
     */
    public class CheckTokenTask extends AsyncTask<Void, Void, Boolean> {

        private String token;
        private String errMes;

        CheckTokenTask(String token) {
            this.token = token;
            this.errMes = null;
            mFormView.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("WelcomeActivity", "Trying to check token correctness on server.");
            Auth mAuth = new Auth();
            try{
                return mAuth.checkToken(token);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.error_toast_no_internet_connection);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.welcome_error_toast_token_expired);
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}


            mCheckTokenTask = null;
            if(success) {
                Log.i("WelcomeActivity", "Token checked: correct");
                Auth.setToken(token);
                startActivity(new Intent(WelcomeActivity.this, StartNewActivityActivity.class));
            } else {
                Log.i("WelcomeActivity", "Token checked: incorrect");
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_LONG).show();

                SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("token", "");
                editor.commit();
            }


            mFormView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {
            // reset the task and hide a progress spinner
            mCheckTokenTask = null;

        }

    }


}
