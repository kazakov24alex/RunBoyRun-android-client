package twoAK.runboyrun.responses;


public class GetActivityDataResponse extends BaseResponse {

    private int id;
    private int athlete_id;
    private boolean track;
    private String sport_type;
    private String datetime_start;
    private int temperature;
    private String weather;
    private String relief;
    private String condition;
    private String duration;
    private double distance;
    private double average_speed;
    private double tempo;
    private String description;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAthlete_id() {
        return athlete_id;
    }

    public void setAthlete_id(int athlete_id) {
        this.athlete_id = athlete_id;
    }

    public boolean isTrack() {
        return track;
    }

    public void setTrack(boolean track) {
        this.track = track;
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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getRelief() {
        return relief;
    }

    public void setRelief(String relief) {
        this.relief = relief;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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

    public double getAverage_speed() {
        return average_speed;
    }

    public void setAverage_speed(double average_speed) {
        this.average_speed = average_speed;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
