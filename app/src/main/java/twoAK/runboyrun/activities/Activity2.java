package twoAK.runboyrun.activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.GetProfileInfoFailedException;
import twoAK.runboyrun.responses.GetProfileInfoResponse;

public class Activity2 extends BaseActivity {
    private SquareImageView avka;
    private ImageButton friendsButton;
    private ImageButton statsButton;
    private ImageButton recordsButton;
    private ImageButton victoriesButton;

    private TextView name;
    private TextView surname;
    private TextView country_city;
    private TextView age;

    private int mYear, mMonth, mDay;

    private GetProfileInfoTask profileInfoTask;

    private ProgressDialog mProgressDialog; // view of a progress spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

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
        country_city = (TextView) findViewById(R.id.activity2_textView_country_city);
        age = (TextView) findViewById(R.id.activity2_textView_age);

        //Set nav drawer selected to second item in list
        mNavigationView.getMenu().getItem(1).setChecked(true);

        avka = (SquareImageView) findViewById(R.id.activity2_imageView_avatar);
        avka.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);


        // Initialization images of button panel
        SquareImageView cell1 = (SquareImageView) findViewById(R.id.picture1);
        cell1.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        SquareImageView cell2 = (SquareImageView) findViewById(R.id.picture2);
        cell2.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        SquareImageView cell3 = (SquareImageView) findViewById(R.id.picture3);
        cell3.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        SquareImageView cell4 = (SquareImageView) findViewById(R.id.picture4);
        cell4.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        // setting listeners on button panel
        View.OnTouchListener panelTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundResource(R.color.vk_color);
                }
                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundResource(0);
                    onClick(view);
                }
                return true;
            }
        };
        cell1.setOnTouchListener(panelTouchListener);
        cell2.setOnTouchListener(panelTouchListener);
        cell3.setOnTouchListener(panelTouchListener);
        cell4.setOnTouchListener(panelTouchListener);

        // initialization text of button panel and setting custom font
        Typeface squareFont = Typeface.createFromAsset(getAssets(), "fonts/square.ttf");
        TextView text1 = (TextView) findViewById(R.id.text1);
        text1.setTypeface(squareFont);
        TextView text2 = (TextView) findViewById(R.id.text2);
        text2.setTypeface(squareFont);
        TextView text3 = (TextView) findViewById(R.id.text3);
        text3.setTypeface(squareFont);
        TextView text4 = (TextView) findViewById(R.id.text4);
        text4.setTypeface(squareFont);


        profileInfoTask = new GetProfileInfoTask();
        profileInfoTask.execute((Void) null);
    }

    public class GetProfileInfoTask extends AsyncTask<Void, Void, GetProfileInfoResponse> {
        private String errMes;  // error message possible

        GetProfileInfoTask() {
            errMes = null;
            showProgress(getString(R.string.profile_loading_progress_dialog));
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
            hideProgressDialog();
            if (profileInfo!=null) {
                Log.i("GetProfileInfoActivity", "Getting profile info was success.");
                name.setText(profileInfo.getName());
                surname.setText(profileInfo.getSurname());
                country_city.setText(profileInfo.getCountry()+", "+profileInfo.getCity());

                Calendar calendar = Calendar.getInstance();
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = calendar.get(Calendar.MONTH);
                mYear = calendar.get(Calendar.YEAR);

                int day = Integer.parseInt(profileInfo.getBirthday().substring(8,10));
                int month = Integer.parseInt(profileInfo.getBirthday().substring(5,7));
                int year = Integer.parseInt(profileInfo.getBirthday().substring(0,4));

                if(mMonth>month){
                    age.setText("Age:"+(mYear-year));
                }else
                    if(mMonth<month){
                        age.setText("Age:"+(mYear-year-1));
                    }else
                        if(mMonth<month){
                            if(mDay>=day){
                                age.setText("Age:"+(mYear-year));
                            }
                            else{
                                age.setText("Age:"+(mYear-year-1));
                            }
                        }

                System.out.println("!!!!!"+day+"-"+month+"-"+year);
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
            hideProgressDialog();
        }
    }

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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture1:
                System.out.println("FRIENDS WAS PRESSED");
                break;
            case R.id.picture2:
                System.out.println("STATS WAS PRESSED");
                break;
            case R.id.picture3:
                System.out.println("RECORDS WAS PRESSED");
                break;
            case R.id.picture4:
                System.out.println("VICTORIES WAS PRESSED");
                break;
            default:
                break;
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
