package twoAK.runboyrun.request.body;

/**
 * Created by alex on 22.05.17.
 */

public class CommentBody {
    int activity_id;
    String text;

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
