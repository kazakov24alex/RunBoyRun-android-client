package twoAK.runboyrun.responses.objects;


public class CityObject {
    private String name;
    private int id;

    public CityObject() {}


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getID() { return id; }
    public void setID(int id) { this.id = id; }

    public String toString() { return "name: " + name + " id: " + id; }

}
