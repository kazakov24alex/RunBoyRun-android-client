package twoAK.runboyrun.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import twoAK.runboyrun.R;
import twoAK.runboyrun.auth.Auth;



public class LoginActivity extends AppCompatActivity {

    private SignInTask signInTask;

    private EditText loginInput;
    private EditText passwordInput;
    private Button mSignUpButton;

    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = new Auth();

        loginInput = (EditText) findViewById(R.id.editText_email);
        passwordInput = (EditText) findViewById(R.id.editText_password);

        mSignUpButton = (Button) findViewById(R.id.button_sendSignIn);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });
    }



    private void attemptSignIn() {
        if(signInTask != null) {
            return;
        }

        loginInput.setError(null);
        passwordInput.setError(null);

        // Store values at the time of the login attempt.
        String login = loginInput.getText().toString();
        String password = passwordInput.getText().toString();
        login.trim();
        password.trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordInput.setError("ERROR: INVALID PASSWORD");  //getString(R.string.error_invalid_password));
            focusView = passwordInput;
            cancel = true;
        }

        // Check for a valid login.
        if (TextUtils.isEmpty(login)) {
            loginInput.setError("ERROR: EMAIL FIELD IS EMPTY"); //getString(R.string.error_field_required));
            focusView = loginInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            signInTask = new LoginActivity.SignInTask(login, password);
            signInTask.execute((Void) null);
        }

    }


    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    private class SignInTask extends AsyncTask<Void, Void, Boolean> {

        private final String identificator;
        private final String password;

        public SignInTask(String login, String password) {
            this.identificator = login;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.i("SignupActivity", "Trying to sign up...");
            return auth.signin(this.identificator, this.password);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            signInTask = null;
            //showProgress(false);

            if(success) {
                System.out.println("YEEEEEAAHHHHH! = " + Auth.getToken());
                auth.setToken(Auth.getToken());
                startActivity(new Intent(LoginActivity.this, Activity1.class));
            }
            else {
                System.out.println("FUUUUUCK!");
                loginInput.setError("Failed to sign up");
                loginInput.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            signInTask = null;
            //showProgress(false);
        }
    }


}

