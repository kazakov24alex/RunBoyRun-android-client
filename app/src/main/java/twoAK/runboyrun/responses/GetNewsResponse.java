package twoAK.runboyrun.responses;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import twoAK.runboyrun.responses.objects.CommentObject;

public class GetNewsResponse extends BaseResponse {
    public ArrayList<CommentObject> getNews() {
        return news;
    }

    public void setNews(ArrayList<CommentObject> news) {
        this.news = news;
    }

    @SerializedName("news")
    ArrayList<CommentObject> news;
}
