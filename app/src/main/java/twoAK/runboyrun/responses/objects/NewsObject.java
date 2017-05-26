package twoAK.runboyrun.responses.objects;


public class NewsObject {
    private int id;
    private String sport_type;
    private String datetime_start;
    private String duration;
    private double distance;
    private String description;
    private int athlete_id;
    private String name;
    private String surname;
    private int order;
    private int like_num;
    private int dislike_num;
    private Boolean my_value;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSport_type() {
        return sport_type;
    }

    public void setSport_type(String sport_type) {
        this.sport_type = sport_type;
    }

    public String getDatetime_start() {
        return datetime_start;
    }

    public void setDatetime_start(String datetime_start) {
        this.datetime_start = datetime_start;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAthlete_id() {
        return athlete_id;
    }

    public void setAthlete_id(int athlete_id) {
        this.athlete_id = athlete_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

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
