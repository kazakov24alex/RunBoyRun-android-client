package twoAK.runboyrun.fragments.profile_activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.TimeAdapter;
import twoAK.runboyrun.R;
import twoAK.runboyrun.activities.ConditionActivity;
import twoAK.runboyrun.adapters.DataAdapter;
import twoAK.runboyrun.adapters.SquareImageView;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.fragments.comment_activity.CommentRecyclerFragment;
import twoAK.runboyrun.responses.GetCommentsResponse;
import twoAK.runboyrun.responses.GetNewsResponse;
import twoAK.runboyrun.responses.objects.CommentObject;
import twoAK.runboyrun.responses.objects.NewsObject;


public class NewsFeedFragment extends Fragment {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "[" + ConditionActivity.class.getName() + "]: ";

    static final int NEWS_PER_PAGE = 8;

    private GetNewsPageTask mGetNewsPageTask;

    private int mAllNewsNum;
    private int mLastLoadedPage;

    private int mAthleteID;
    private boolean isPageScroll = false;
    private int lastPage = 10;


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DataAdapter mNewsRecycleAdapter;

    private List<NewsObject> mNewsList;
    protected Handler handler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);

        return rootView;
    }

    public void setAthleteId(int athleteId){
        mAthleteID = athleteId;
        mGetNewsPageTask = new GetNewsPageTask(mAthleteID, NEWS_PER_PAGE, 1);
        mGetNewsPageTask.execute((Void)null);
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
            Log.i(APP_TAG, athlete_id+" "+ news_num+ " "+ page_num);
            //mLastCommentsPanelFragment.showProgressCircle(true);
        }

        @Override
        protected GetNewsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get comments page");
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
                Log.i(APP_TAG, ACTIVITY_TAG + "GETTING NEWS ERROR: " + errMes);
                Toast.makeText(getActivity(), errMes, Toast.LENGTH_SHORT).show();
                //mCommentRecycleAdapter.setLoaded();
            } else{
                Log.i(APP_TAG, ACTIVITY_TAG + "GETTING NEWS: " + newsResponse.getNews().size());
                Toast.makeText(getActivity(), ""+newsResponse.getNews().size(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
