package twoAK.runboyrun.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.responses.objects.CommentObject;

public class CommentRecycleAdapter extends RecyclerView.Adapter<CommentRecycleAdapter.CommentHolder>  {

    private List<CommentObject> mCommentsList;
    private LayoutInflater mLayoutInflater;

    public CommentRecycleAdapter(List<CommentObject> commentsList, Context context) {
        mCommentsList = commentsList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_comment_list, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        CommentObject comment = mCommentsList.get(position);
        holder.setCommentObject(comment);
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        private CommentObject commentObject;
        private View mView;

        public CommentHolder(View view) {
            super(view);

            mView = view;
        }

        public void setCommentObject(CommentObject commentObject) {
            this.commentObject = commentObject;

            fillContent();
        }

        public void fillContent() {
            ((SquareImageView) mView.findViewById(R.id.item_comment_list_image_avatar))
                    .setImageResource(R.drawable.com_facebook_top_button);

            ((TextView) mView.findViewById(R.id.item_comment_list_text_author))
                    .setText(commentObject.getName() + " " + commentObject.getSurname());

            ((TextView) mView.findViewById(R.id.item_comment_list_text_order))
                    .setText(mLayoutInflater.getContext().getString(R.string.comment_activity_order_label) + commentObject.getOrder());

            ((TextView) mView.findViewById(R.id.item_comment_list_text_message))
                    .setText(commentObject.getText());

            try {
                Date curDate = new Date();
                Date comDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(commentObject.getDate_time().replaceAll("Z$", "+0000"));

                String curDateString = (new SimpleDateFormat("dd MMMM yyyy")).format(curDate);
                String comDateString = (new SimpleDateFormat("dd MMMM yyyy")).format(comDate);

                if(curDate.getYear() == comDate.getYear()) {
                    ((TextView) mView.findViewById(R.id.item_comment_list_text_datetime))
                            .setText((new SimpleDateFormat("dd MMM yyyy  kk:mm")).format(comDate));
                } else {
                    if (curDateString.equals(comDateString)) {
                        ((TextView) mView.findViewById(R.id.item_comment_list_text_datetime))
                                .setText((new SimpleDateFormat("kk:mm")).format(comDate));
                    } else {
                        ((TextView) mView.findViewById(R.id.item_comment_list_text_datetime))
                                .setText((new SimpleDateFormat("dd MMMM kk:mm")).format(comDate));
                    }
                }
            } catch (ParseException e) {
                //TODO: handle
            }

        }

    }


}