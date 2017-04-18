package twoAK.runboyrun.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Response;
import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareView;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.GetProfileInfoFailedException;
import twoAK.runboyrun.exceptions.api.LoginFailedException;
import twoAK.runboyrun.responses.GetProfileInfoResponse;

import static twoAK.runboyrun.activities.SocialNetworksAuthActivity.showProgress;

public class Activity2 extends BaseActivity {
    private View rootView;
    private SquareView avka;

    private TextView name;
    private TextView surname;

    private GetProfileInfoTask profileInfoTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        rootView = findViewById(R.id.activity2_personal_page);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Activity2", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //View initialization
        name = (TextView) findViewById(R.id.activity2_textView_name);
        surname = (TextView) findViewById(R.id.activity2_textView_surname);

        //Set nav drawer selected to second item in list
        mNavigationView.getMenu().getItem(1).setChecked(true);

        avka = (SquareView) findViewById(R.id.activity2_imageView_avatar);
        avka.onMeasure(avka.getWidth(),avka.getWidth());

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");
        profileInfoTask = new GetProfileInfoTask();
        profileInfoTask.execute((Void) null);
    }

    public class GetProfileInfoTask extends AsyncTask<Void, Void, GetProfileInfoResponse> {
        private String errMes;  // error message possible

        GetProfileInfoTask() {
            errMes = null;
        }

        @Override
        protected GetProfileInfoResponse doInBackground(Void... params) {
            Log.i("GetProfileInfoActivity", "Trying to get profile info.");
            try {
                return ApiClient.instance().getProfileInfo();
            } catch(GetProfileInfoFailedException e) {
                errMes = getString(R.string.getprofileinfo_error);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetProfileInfoResponse profileInfo) {

            if (profileInfo!=null) {
                Log.i("GetProfileInfoActivity", "Getting profile info was success.");
                name.setText(profileInfo.getName());
                surname.setText(profileInfo.getSurname());
            } else {
                Log.i("SignInActivity", "Login is unsuccessful.");
                // show the error and focus on the wrong field
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_LONG).show();
            }
        }
    }



    /** HIDE TOOLBAR **/
//    @Override
//    protected boolean useToolbar() {
//        return false;
//    }



    /** HIDE hamburger menu **/
//    @Override
//    protected boolean useDrawerToggle() {
//        return false;
//    }

}
