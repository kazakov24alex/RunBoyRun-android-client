package twoAK.runboyrun.fragments.activity_page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import twoAK.runboyrun.R;


public class LastCommentsPanelFragment extends Fragment {

    private View mFormView;
    private View mProgressView;

    private Button mWriteCommentButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_last_comments, container, false);

        mProgressView = rootView.findViewById(R.id.last_comments_progress_circle);
        mFormView     = rootView.findViewById(R.id.last_comments_form);

        mWriteCommentButton = (Button) rootView.findViewById(R.id.last_comments_button_write_comment);

        return rootView;
    }

    public void addCommentReview(String author, String message) {
        // получаем экземпляр FragmentTransaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // добавляем фрагмент
        CommentPreviewFragment mCommentPreviewFragment = new CommentPreviewFragment();
        mCommentPreviewFragment.setContent(author, message);

        fragmentTransaction.add(R.id.last_comments_container_fragments, mCommentPreviewFragment);
        fragmentTransaction.commit();
    }

    public void showWriteCommentButton(boolean value) {
        if(value == true) {
            mWriteCommentButton.setVisibility(View.VISIBLE);
        } else {
            mWriteCommentButton.setVisibility(View.GONE);
        }
    }


    /**
     * Shows the progress UI and hides the UI form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgressCircle(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
