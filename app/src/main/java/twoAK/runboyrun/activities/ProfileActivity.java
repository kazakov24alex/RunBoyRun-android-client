package twoAK.runboyrun.activities;

import android.os.Bundle;

public class ProfileActivity extends BaseActivity {



/*
    private GetNewsPageTask mGetNewsPageTask;*/







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);









    }


   /*



    public void onFriendsClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onFriendsClick");
    }


    public void onStatsClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onStatsClick");
    }

    public void onRecordsClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onRecordsClick");
    }

    public void onVictoriesClick(View view) {
        Log.i(APP_TAG, ACTIVITY_TAG + "onVictioriesClick");
    }








    private class GetNewsPageTask extends AsyncTask<Void, Void, GetNewsResponse> {
        private String errMes;  // error message possible
        private int athlete_id;
        private int news_num;
        private int page_num;

        GetNewsPageTask(int athlete_id, int news_num, int page_num) {
            errMes = null;
            this.athlete_id = athlete_id;
            this.news_num = news_num;
            this.page_num = page_num;

            loadingInProgress = true;
        }

        @Override
        protected GetNewsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get news page");
            try {
                return ApiClient.instance().getNewsPage(athlete_id, news_num, page_num);
            } catch (RequestFailedException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_request_failed);
            } catch (InsuccessfulResponseException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetNewsResponse newsResponse) {
            if (newsResponse == null)   {
                Log.i(APP_TAG, ACTIVITY_TAG + "getting news error: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();

            } else {
                if (newsResponse.getNews() != null) {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news page was loaded");
                    Toast.makeText(getApplicationContext(), "" + newsResponse.getNews().size(), Toast.LENGTH_SHORT).show();

                    if (mAllNewsNum == -1) {
                        mAllNewsNum = newsResponse.getNews().get(0).getOrder();
                        int pagesNum = mAllNewsNum / NEWS_PER_PAGE + 1;
                    }

                    for (int i = 0; i < newsResponse.getNews().size(); i++) {
                        mNewsList.add(newsResponse.getNews().get(i));
                    }



                } else {
                    Log.i(APP_TAG, ACTIVITY_TAG + "news are absent");
                    Toast.makeText(getApplicationContext(), "news are absent", Toast.LENGTH_SHORT).show();
                }
            }

            showProgressCircle(false);
            loadingInProgress = false;
        }
    }



    */

}