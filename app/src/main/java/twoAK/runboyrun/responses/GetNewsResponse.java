package twoAK.runboyrun.responses;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import twoAK.runboyrun.responses.objects.NewsObject;

public class GetNewsResponse extends BaseResponse {

    @SerializedName("news")
    ArrayList<NewsObject> news;


    public ArrayList<NewsObject> getNews() {
        return news;
    }

    public void setNews(ArrayList<NewsObject> news) {
        this.news = news;
    }
}
