package twoAK.runboyrun.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import twoAK.runboyrun.R;


public class WelcomeActivity extends AppCompatActivity {

    Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        mSignInButton = (Button) findViewById(R.id.button_signin);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });
    }


}
