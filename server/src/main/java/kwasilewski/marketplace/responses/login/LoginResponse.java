package kwasilewski.marketplace.responses.login;

import kwasilewski.marketplace.dto.UserData;

import java.util.Date;

public class LoginResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Date avatarDate;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(UserData userData, String token) {
        if (userData != null) {
            this.id = userData.getId();
            this.email = userData.getEmail();
            this.firstName = userData.getFirstName();
            this.lastName = userData.getLastName();
            this.avatarDate = userData.getAvatarDate();
            this.token = token;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Date getAvatarDate() {
        return avatarDate;
    }

    public void setAvatarDate(Date avatarDate) {
        this.avatarDate = avatarDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
