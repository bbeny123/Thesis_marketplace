package kwasilewski.marketplace.configuration.context;


import kwasilewski.marketplace.dto.CurrentUserData;

import java.io.Serializable;

public class UserContext implements Serializable {

    private static final long serialVersionUID = -7616775661499486842L;
    private Long userId;
    private boolean admin;

    public UserContext() {
    }

    public UserContext(CurrentUserData currentUser) {
        this.userId = currentUser.getId();
        this.admin = currentUser.isAdmin();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void changeUser(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
