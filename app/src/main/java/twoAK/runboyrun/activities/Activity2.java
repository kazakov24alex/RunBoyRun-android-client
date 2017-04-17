package twoAK.runboyrun.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareView;

public class Activity2 extends BaseActivity {
    private View rootView;
    private SquareView avka;

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


        //Set nav drawer selected to second item in list
        mNavigationView.getMenu().getItem(1).setChecked(true);

        avka = (SquareView) findViewById(R.id.activity2_imageView_avatar);
        avka.onMeasure(avka.getWidth(),avka.getWidth());
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
