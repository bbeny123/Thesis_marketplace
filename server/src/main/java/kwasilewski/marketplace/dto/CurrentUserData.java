package kwasilewski.marketplace.dto;

import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUserData extends org.springframework.security.core.userdetails.User {

    private final UserData user;
    private final boolean admin;

    public CurrentUserData(UserData user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole()));
        this.user = user;
        this.admin = user.isAdmin();
    }

    public UserData getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getRole() {
        return user.getRole();
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + user +
                "} " + super.toString();
    }

}
