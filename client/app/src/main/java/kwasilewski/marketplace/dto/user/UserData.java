package kwasilewski.marketplace.dto.user;

public class UserData extends LoginData {

    private String firstName;
    private String lastName;
    private String city;
    private Long prvId;
    private String phone;
    private String token; // only for logging in

    public UserData() {
    }

    public UserData(String email, String password, String firstName, String lastName, String city, Long prvId, String phone) {
        super(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.prvId = prvId;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getPrvId() {
        return prvId;
    }

    public void setPrvId(Long prvId) {
        this.prvId = prvId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
