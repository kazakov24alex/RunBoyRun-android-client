package twoAK.runboyrun.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import twoAK.runboyrun.R;

public class StartNewActivityActivity extends BaseActivity {


    private View rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_activity);
        rootView = findViewById(R.id.activity1_container);

        /* use rootView to get UI content from main container */
        //BT bt = (BT) rootView.findViewById(R.id.fab) ...;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "StartNewActivityActivity", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.start_activity_button_enter_stat:
                startActivity(new Intent(StartNewActivityActivity.this, EnterCompletedActivity.class));
                break;
            case R.id.start_activity_button_track_activity:
                startActivity(new Intent(StartNewActivityActivity.this, TrackActivityActivity.class));
                break;
            default:
                break;
        }
    }


}
