package twoAK.runboyrun.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

            ((SquareImageView) mView.findViewById(R.id.item_comment_list_image_avatar))
                    .setImageResource(R.drawable.com_facebook_top_button);

            ((TextView) mView.findViewById(R.id.item_comment_list_text_author))
                    .setText(commentObject.getName() + " " + commentObject.getSurname());

            ((TextView) mView.findViewById(R.id.item_comment_list_text_message))
                    .setText(commentObject.getText());

            ((TextView) mView.findViewById(R.id.item_comment_list_text_datetime))
                    .setText(commentObject.getDate_time());

            ((TextView) mView.findViewById(R.id.item_comment_list_text_order))
                    .setText("#" + commentObject.getOrder());
        }

    }


/*
    static final String APP_TAG = "RUN-BOY-RUN";
    static final String ACTIVITY_TAG = "["+ConditionActivity.class.getName()+"]: ";

    private Context ctx;
    private LayoutInflater lInflater;
    private List<CommentObject> commentsList;

    public CommentRecycleAdapter(Context context, List<CommentObject> commentsList) {
        this.commentsList = commentsList;
        ctx = context;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public CommentObject getItem(int position) {
        return commentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_comment_list, parent, false);
        }

        CommentObject comment = getItem(position);

        // initialization of views
        ((SquareImageView) view.findViewById(R.id.item_comment_list_image_avatar))
                .setImageResource(R.drawable.com_facebook_top_button);

        ((TextView) view.findViewById(R.id.item_comment_list_text_author))
                .setText(comment.getName()+" "+comment.getSurname());

        ((TextView) view.findViewById(R.id.item_comment_list_text_message))
                .setText(comment.getText());

        ((TextView) view.findViewById(R.id.item_comment_list_text_datetime))
                .setText(comment.getDate_time());

        ((TextView) view.findViewById(R.id.item_comment_list_text_order))
                .setText("#"+comment.getOrder());

        return view;
    }
*/

}