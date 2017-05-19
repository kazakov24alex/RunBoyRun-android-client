package twoAK.runboyrun.fragments.activity_page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import twoAK.runboyrun.R;


public class LastCommentsPanelFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_last_comments, container, false);

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


}
