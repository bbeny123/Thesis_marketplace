package kwasilewski.marketplace.dtoext.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.UserData;

public class UserDataExt extends LoginDataExt {

    private String firstName;
    private String lastName;
    private String city;
    private Long prvId;
    private String province;
    private String phone;
    private String token; // only for logging in

    public UserDataExt() {
    }

    public UserDataExt(UserData userData) {
        this.email = userData.getEmail();
        this.firstName = userData.getFirstName();
        this.lastName = userData.getLastName();
        this.city = userData.getCity();
        this.prvId = userData.getPrvId();
        this.province = userData.getProvince().getName();
        this.phone = userData.getPhone();
        this.token = userData.getToken();
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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

    @JsonIgnore
    public UserData getUserData() {
        UserData user = new UserData();
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setCity(this.city);
        user.setPrvId(this.prvId);
        user.setPhone(this.phone);
        return user;
    }

    public UserData getUserData(UserContext ctx) {
        UserData user = getUserData();
        user.setId(ctx.getUserId());
        user.setEmail(ctx.getUser().getEmail());
        user.setPassword(ctx.getUser().getPassword());
        return user;
    }
}
