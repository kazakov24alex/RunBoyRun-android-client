package twoAK.runboyrun.request.body;


import java.util.ArrayList;
import java.util.List;

import twoAK.runboyrun.objects.route.RoutePoint;

public class ActivityBody {

    boolean track;
    String sport_type;
    String datetime_start;
    int temperature;
    String weather;
    String relief;
    String condition;
    String duration;
    double distance;
    double average_speed;
    double tempo;
    String description;

    List<List<Double>> route;
    List<List<Double>> timeline;



    public ActivityBody() {
        route = null;
        timeline = null;
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
        this.average_speed = round(average_speed, 2);;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = round(tempo, 2);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public void setRoute(List<RoutePoint> routeList) {
        route = new ArrayList<List<Double>>();

        for(int i = 0; i < routeList.size(); i++) {
            List<Double> point = new ArrayList<Double>();
            point.add(routeList.get(i).lat);
            point.add(routeList.get(i).lng);
            route.add(point);
        }
    }

    public void setTimeline(List<RoutePoint> timelineList) {
        timeline = new ArrayList<List<Double>>();

        for(int i = 0; i < timelineList.size(); i++) {
            List<Double> timepoint = new ArrayList<Double>();
            timepoint.add(timelineList.get(i).lat);
            timepoint.add(timelineList.get(i).lng);
            timeline.add(timepoint);
        }

        System.out.println("RUN-BOY-RUN  SIZE="+timelineList.size());
        System.out.println("RUN-BOY-RUN  SIZE="+timelineList.get(0).lat);

    }



    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}
