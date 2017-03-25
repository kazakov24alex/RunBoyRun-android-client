package twoAK.runboyrun.request.body;


public class SignUpBody {
    private String oauth;
    private String identificator;
    private String password;
    private String name;
    private String surname;
    private String country;
    private String city;
    private String birthday;
    private String sex;

    public SignUpBody(String oauth, String identificator,String password,String name,String surname,String country,
                     String city, String birthday, String sex) {

        this.oauth          = oauth;
        this.identificator  = identificator;
        this.password       = password;
        this.name           = name;
        this.surname        = surname;
        this.country        = country;
        this.city           = city;
        this.birthday       = birthday;
        this.sex            = sex;
    }


}
