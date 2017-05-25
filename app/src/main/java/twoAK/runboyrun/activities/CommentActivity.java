package twoAK.runboyrun.activities;


import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.TimeAdapter;
import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.CustomDividerItemDecoration;
import twoAK.runboyrun.adapters.DataAdapter;
import twoAK.runboyrun.api.ApiClient;
import twoAK.runboyrun.exceptions.api.InsuccessfulResponseException;
import twoAK.runboyrun.exceptions.api.RequestFailedException;
import twoAK.runboyrun.interfaces.OnLoadMoreListener;
import twoAK.runboyrun.responses.GetCommentsResponse;
import twoAK.runboyrun.responses.objects.CommentObject;

public class CommentActivity extends BaseActivity {

    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "[" + ConditionActivity.class.getName() + "]: ";

    static final int COMMENTS_PER_PAGE = 8;

    private int mAllCommentsNum;
    private int mLastLoadedPage;

    private int mActivityID;
    private boolean isPageScroll = false;
    private int lastPage = 10;

    private GetCommentsPageTask mGetCommentsPageTask;

    private AbstractWheel mPagesWheel;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DataAdapter mCommentRecycleAdapter;

    private List<CommentObject> mCommentsList;
    protected Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_template);

        // Set nav drawer selected to first item in list
        mNavigationView.getMenu().getItem(1).setChecked(true);

        // Content initialization
        ViewStub stub = (ViewStub) findViewById(R.id.content_stub);
        stub.setLayoutResource(R.layout.content_comment_activity);
        View inflated = stub.inflate();

        try {
            mActivityID = getIntent().getExtras().getInt("ACTIVITY_ID", -1);
        } catch (NullPointerException e) {
            Log.i(APP_TAG, ACTIVITY_TAG + "NOT GIVEN ATHLETE_ID");
            finish();
        }

        mAllCommentsNum = -1;
        mLastLoadedPage = 0;

        handler = new Handler();


        // Meters horizontal wheel
        mPagesWheel = (AbstractWheel) findViewById(R.id.comment_activity_wheel_meters);
        TimeAdapter metersAdapter = new TimeAdapter(this, 1, 1, "%d");
        metersAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        metersAdapter.setItemTextResource(R.id.text);
        mPagesWheel.setViewAdapter(metersAdapter);
        mPagesWheel.setCurrentItem(2);

        /*OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                Log.i(APP_TAG, ACTIVITY_TAG + "PAGE = "+newValue);

                int totalVisibleItems = mLinearLayoutManager.findLastVisibleItemPosition() - mLinearLayoutManager.findFirstVisibleItemPosition();
                int centeredItemPosition = totalVisibleItems / 2;
                mRecyclerView.smoothScrollToPosition(newValue*COMMENTS_PER_PAGE);



               // mLinearLayoutManager.scrollToPosition(newValue*COMMENTS_PER_PAGE + 2);
            }
        };
        mPagesWheel.addChangingListener(wheelListener);*/


        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                isPageScroll = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                isPageScroll = false;

                int currentPage = mPagesWheel.getCurrentItem();
                int totalVisibleItems = mLinearLayoutManager.findLastVisibleItemPosition() - mLinearLayoutManager.findFirstVisibleItemPosition() - 1;

                if( (lastPage > currentPage) || ( (currentPage*COMMENTS_PER_PAGE + totalVisibleItems) > mAllCommentsNum) )  {
                    mRecyclerView.scrollToPosition(mPagesWheel.getCurrentItem()*COMMENTS_PER_PAGE);
                } else {
                    mRecyclerView.scrollToPosition(mPagesWheel.getCurrentItem()*COMMENTS_PER_PAGE + totalVisibleItems);
                }

                lastPage = mPagesWheel.getCurrentItem();
              }
        };
        mPagesWheel.addScrollingListener(scrollListener);



        mRecyclerView = (RecyclerView) findViewById(R.id.comment_activity_recycler_view);

        mLinearLayoutManager = new MyCustomLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new CustomDividerItemDecoration(this));


        mCommentsList = new ArrayList<CommentObject>();
        mCommentRecycleAdapter = new DataAdapter(mCommentsList, mRecyclerView);
        mCommentRecycleAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(mCommentsList.size() < mAllCommentsNum) {
                    //add null , so the adapter will check view_type and show progress bar at bottom
                    mCommentsList.add(null);
                    mCommentRecycleAdapter.notifyItemInserted(mCommentsList.size() - 1);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //   remove progress item
                            mCommentsList.remove(mCommentsList.size() - 1);
                            mCommentRecycleAdapter.notifyItemRemoved(mCommentsList.size());
                            //add items one by one
                            mGetCommentsPageTask = new GetCommentsPageTask(mActivityID, COMMENTS_PER_PAGE, ++mLastLoadedPage);
                            mGetCommentsPageTask.execute((Void) null);

                            //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                        }
                    }, 3000);
                }
            }
        });
        mRecyclerView.setAdapter(mCommentRecycleAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!isPageScroll) {
                    int currentPage = (mLinearLayoutManager.findLastVisibleItemPosition() + 1) / COMMENTS_PER_PAGE;
                    mPagesWheel.setCurrentItem(currentPage);
                }
            }
        });


        mGetCommentsPageTask = new GetCommentsPageTask(mActivityID, COMMENTS_PER_PAGE, ++mLastLoadedPage);
        mGetCommentsPageTask.execute((Void) null);

    }


    public class MyCustomLayoutManager extends LinearLayoutManager {
        private static final float MILLISECONDS_PER_INCH = 50f;
        private Context mContext;

        public MyCustomLayoutManager(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView,
                                           RecyclerView.State state, final int position) {

            LinearSmoothScroller smoothScroller =
                    new LinearSmoothScroller(mContext) {

                        //This controls the direction in which smoothScroll looks
                        //for your view
                        @Override
                        public PointF computeScrollVectorForPosition
                        (int targetPosition) {
                            return MyCustomLayoutManager.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        //This returns the milliseconds it takes to
                        //scroll one pixel.
                        @Override
                        protected float calculateSpeedPerPixel
                        (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH/displayMetrics.densityDpi;
                        }
                    };

            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }

    private class GetCommentsPageTask extends AsyncTask<Void, Void, GetCommentsResponse> {
        private String errMes;  // error message possible
        private int activity_id;
        private int comments_num;
        private int page_num;

        GetCommentsPageTask(int activity_id, int comments_num, int page_num) {
            errMes = null;
            this.activity_id = activity_id;
            this.comments_num = comments_num;
            this.page_num = page_num;

            //mLastCommentsPanelFragment.showProgressCircle(true);
        }

        @Override
        protected GetCommentsResponse doInBackground(Void... params) {
            Log.i(APP_TAG, ACTIVITY_TAG + "Trying to get comments page");
            try {
                return ApiClient.instance().getCommentsPage(activity_id, comments_num, page_num);
            } catch (RequestFailedException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_request_failed);
            } catch (InsuccessfulResponseException e) {
                errMes = getString(R.string.comment_activity_error_loading_activity_insuccessful);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final GetCommentsResponse commentsResponse) {
            if (commentsResponse == null) {
                Log.i(APP_TAG, ACTIVITY_TAG + "GETTING COMMENTS ERROR: " + errMes);
                Toast.makeText(getApplicationContext(), errMes, Toast.LENGTH_SHORT).show();
                mCommentRecycleAdapter.setLoaded();
                return;

            } else {
                if (mAllCommentsNum == -1) {
                    mAllCommentsNum = commentsResponse.getComments().get(0).getOrder();

                    int pagesNum = mAllCommentsNum / COMMENTS_PER_PAGE + 1;
                    TimeAdapter metersAdapter = new TimeAdapter(getApplicationContext(), 1, pagesNum, "%d");
                    metersAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
                    metersAdapter.setItemTextResource(R.id.text);
                    mPagesWheel.setViewAdapter(metersAdapter);
                }

                for (int i = 0; i < commentsResponse.getComments().size(); i++) {
                    mCommentsList.add(commentsResponse.getComments().get(i));
                }
                mCommentRecycleAdapter.notifyItemInserted(mCommentsList.size());
                mCommentRecycleAdapter.setLoaded();


            }

        }

    }
}


    