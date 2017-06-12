package twoAK.runboyrun.fragments.activity_page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import twoAK.runboyrun.R;
import twoAK.runboyrun.adapters.SquareImageView;


public class CommentPreviewFragment extends Fragment {

    private String mAuthor;
    private String mMessage;

    private SquareImageView mAvatarImage;
    private TextView mAuthorText;
    private TextView mMessageText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comment_preview, container, false);

        mAvatarImage = (SquareImageView) rootView.findViewById(R.id.comment_preview_image_avatar);
        mAvatarImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        mAuthorText = (TextView) rootView.findViewById(R.id.comment_preview_text_author);
        mAuthorText.setText(mAuthor);

        mMessageText = (TextView) rootView.findViewById(R.id.comment_preview_text_message);
        mMessageText.setText(mMessage);

        return rootView;
    }

    public void setContent(String author, String message) {
        mAuthor = author;
        mMessage = message;
    }

}
