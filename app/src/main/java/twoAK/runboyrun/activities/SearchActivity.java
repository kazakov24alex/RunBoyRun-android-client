package twoAK.runboyrun.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.AthletePreviewListAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.responses.GetSearchResponse;
import twoAK.runboyrun.responses.objects.AthletePreviewObject;

public class SearchActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+SearchActivity.class.getName()+"]: ";

    private GetSearchTask mGetSearchTask;
    private GetSearchResponse mGetSearchResponse;

    private String lastSearch;

    private EditText mSearchField;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(4).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_search_activity);
        View inflated = stub.inflate();

        lastSearch = "_";

        mSearchField = (EditText) findViewById(R.id.search_activity_edittext_search_field);
        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });


        mListView = (ListView) findViewById(R.id.search_activity_list_view);

        search();

    }

    private void search() {
        String stringSearch = mSearchField.getText().toString().trim();
        if(!stringSearch.equals("")) {
            stringSearch = stringSearch.replace(' ', '+');
        } else {
            stringSearch = "_";
        }

        mGetSearchTask = new GetSearchTask(stringSearch);
        mGetSearchTask.execute((Void) null);

    }



    public class GetSearchTask extends AsyncTask<Void, Void, GetSearchResponse> {
        private String errMes;  // error message possible
        private String searchString;

        GetSearchTask(String searchString) {
            errMes = null;
            this.searchString = searchString;

        }

        @Override
        protected GetSearchResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to search");
            try {
                return ApiClient.instance().getSearch(searchString);
            } catch(RequestFailedException e) {
                errMes = getString(R.string.value_activity_error_loading_values_request_failed);
            } catch(InsuccessfulResponseException e) {
                errMes = getString(R.string.value_activity_error_loading_values_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetSearchResponse response) {
            if(response == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "SEARING ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                return;
            } else {

                if(!lastSearch.equals(searchString)) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "search was performed");
                    mGetSearchResponse = response;
                    lastSearch = searchString;

                    if (response.getAthletes() == null) {
                        response.setAthletes(new ArrayList<AthletePreviewObject>());
                    }

                    // set adapten on ListView
                    AthletePreviewListAdapter adapter = new AthletePreviewListAdapter(getApplicationContext(), response.getAthletes());
                    mListView.setAdapter(adapter);

                    // on item click listener
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(SearchActivity.this, NewsFeedProfileActivity.class);
                            intent.putExtra("ATHLETE_ID", mGetSearchResponse.getAthletes().get(position).getId());
                            startActivity(intent);
                        }
                    });
                }

                mGetSearchTask = null;
                search();

            }

        }

        /** The task was canceled. */
        @Override
        protected void onCancelled() { }
    }
}


