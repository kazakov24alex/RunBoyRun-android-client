package twoAK.runboyrun.responses;


public class ValueResponse extends BaseResponse {


    int like_num;
    int dislike_num;
    Boolean my_value;


    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public int getDislike_num() {
        return dislike_num;
    }

    public void setDislike_num(int dislike_num) {
        this.dislike_num = dislike_num;
    }

    public Boolean getMy_value() {
        return my_value;
    }

    public void setMy_value(Boolean my_value) {
        this.my_value = my_value;
    }


}
