package twoAK.runboyrun.responses;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import twoAK.runboyrun.responses.objects.CommentObject;

public class GetCommentsResponse extends BaseResponse {

    @SerializedName("comments")
    ArrayList<CommentObject> comments;


    public ArrayList<CommentObject> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentObject> comments) {
        this.comments = comments;
    }

}
