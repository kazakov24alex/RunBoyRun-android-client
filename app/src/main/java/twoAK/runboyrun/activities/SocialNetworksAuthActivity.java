package twoAK.runboyrun.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

import twoAK.runboyrun.R;


public class SocialNetworksAuthActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, FragmentManager.OnBackStackChangedListener{
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";

    private final String G_PLUS_SCOPE   = "oauth2:https://www.googleapis.com/auth/plus.me";
    private final String USERINFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private final String EMAIL_SCOPE    = "https://www.googleapis.com/auth/userinfo.email";
    private final String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;


    private static Context context;
    private static ProgressDialog progressDialog;

    private SocialNetworksAuthActivity self = this;
    private GoogleApiClient mGoogleApiClient;
    private GoogleLoginTask mGoogleLoginTask;
    private Button googleplusButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_socialnets);
        context = this;
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        homeAsUpByBackStack();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SocialNetFragment())
                    .commit();
        }

        googleplusButton = (Button) findViewById(R.id.socialnets_gpButton2);
        googleplusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });
    }

    // login GOOGLE
    private void googleLogin() {
        if(mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    requestScopes(new Scope(Scopes.PLUS_LOGIN)).
                    requestEmail().
                    build();
            mGoogleApiClient = new GoogleApiClient.Builder(self).
                    enableAutoManage(self, self).
                    addApi(Auth.GOOGLE_SIGN_IN_API, gso).
                    addApi(Plus.API).
                    build();
        }

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, 123);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("GOOGLE PLUS", "ConnectionFailed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackStackChanged() {
        homeAsUpByBackStack();
    }

    private void homeAsUpByBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected static void showProgress(String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    protected static void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if VK,  Facebook
        if(requestCode != 123) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

        // if GooglePlus
        } else if (resultCode == RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d("GOOGLE_SIGN", "LoginStatus: " + result.isSuccess());
                GoogleSignInAccount acct = result.getSignInAccount();

                // starting Google login task
                mGoogleLoginTask = new SocialNetworksAuthActivity.GoogleLoginTask(
                        acct.getAccount().name,
                        acct.getId(),
                        acct.getGivenName(),
                        acct.getFamilyName()
                );
                mGoogleLoginTask.execute(null, null, null);
            }
        }
    }


    private class GoogleLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String oauth = "google";
        private String accountName;
        private String id;
        private String name;
        private String surname;
        private String token;

        GoogleLoginTask(String accountName, String id, String name, String surname) {
            this.accountName = accountName;
            this.id          = id;
            this.name        = name;
            this.surname     = surname;
            this.token       = null;
        }

        // Getting token
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                token = GoogleAuthUtil.getToken(SocialNetworksAuthActivity.this, accountName, SCOPES);
                return true;
            } catch (UserRecoverableAuthException userAuthEx) {
                startActivityForResult(userAuthEx.getIntent(), 123);
                return false;
            }  catch (IOException ioEx) {
                Log.d("GOOGLE_SIGN", "IOException");
                return false;
            }  catch (GoogleAuthException fatalAuthEx)  {
                Log.d("GOOGLE_SIGN", "Fatal Authorization Exception" + fatalAuthEx.getLocalizedMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success == true) {
                Log.d("GOOGLE_SIGN", "token received");

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).
                        setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
                            @Override
                            public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                                Log.d("GOOGLE_SIGN", "logout occured");
                            }
                        });

                Intent returnIntent = new Intent();
                returnIntent.putExtra("oauth", oauth);
                returnIntent.putExtra("id", id);
                returnIntent.putExtra("access_token", token);
                returnIntent.putExtra("name", name);
                returnIntent.putExtra("surname", surname);
                setResult(SocialNetworksAuthActivity.RESULT_OK, returnIntent);
                finish();
            } else {
                Log.d("GOOGLE_SIGN", "no token recieved");
            }
        }
    };

}
