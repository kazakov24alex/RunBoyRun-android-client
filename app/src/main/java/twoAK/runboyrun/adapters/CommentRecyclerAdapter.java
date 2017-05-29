package twoAK.runboyrun.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import twoAK.runboyrun.R;
import twoAK.runboyrun.interfaces.OnLoadMoreListener;
import twoAK.runboyrun.responses.objects.CommentObject;

public class CommentRecyclerAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<CommentObject> commentList;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;



    public CommentRecyclerAdapter(List<CommentObject> comments, RecyclerView recyclerView) {
        commentList = comments;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return commentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_comment_list, parent, false);

            vh = new CommentHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentHolder) {
            CommentObject comment = (CommentObject) commentList.get(position);
            ((CommentHolder) holder).setCommentObject(comment);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
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
                    .setText("#" + commentObject.getOrder());

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


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.item_progress_bar);
        }
    }
}