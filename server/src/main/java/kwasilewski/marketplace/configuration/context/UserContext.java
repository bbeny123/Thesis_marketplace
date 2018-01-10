package kwasilewski.marketplace.configuration.context;


import kwasilewski.marketplace.dto.CurrentUserData;
import kwasilewski.marketplace.dto.UserData;

import java.io.Serializable;

public class UserContext implements Serializable {

    private static final long serialVersionUID = -7616775661499486842L;
    private UserData user;
    private Long userId;
    private boolean admin;

    public UserContext() {
    }

    public UserContext(CurrentUserData currentUser) {
        this.user = currentUser.getUser();
        this.userId = currentUser.getId();
        this.admin = currentUser.isAdmin();
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public void changeUser(UserData user) {
        this.user = user;
        this.admin = user.isAdmin();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isUser() {
        return !admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
