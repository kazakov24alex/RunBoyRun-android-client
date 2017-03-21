package twoAK.runboyrun.responses.objects;


public class CountryObject {
    private String name;
    private String code;

    public CountryObject() {}


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() { return code; }
    public void setId(String code) { this.code = code; }

    public String toString() { return "name: " + name + " code: " + code; }

}
