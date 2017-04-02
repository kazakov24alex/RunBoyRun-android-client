package twoAK.runboyrun.request.body;


/** Body of CHECK request */
public class CheckBody {
    private String oauth;
    private String identificator;

    public CheckBody(String oauth, String identificator) {
        this.oauth = oauth;
        this.identificator = identificator;
    }
}