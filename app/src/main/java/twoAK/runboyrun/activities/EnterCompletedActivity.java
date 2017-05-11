package twoAK.runboyrun.activities;


import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import twoAK.runboyrun.R;

public class EnterCompletedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_enter_completed);
        View inflated = stub.inflate();

    }


}
