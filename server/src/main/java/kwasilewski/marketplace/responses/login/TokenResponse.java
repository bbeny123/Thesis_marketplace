package kwasilewski.marketplace.responses.login;

import java.util.Date;

public class TokenResponse {

    private Date avatarDate;

    public TokenResponse() {
    }

    public TokenResponse(Date avatarDate) {
        this.avatarDate = avatarDate;
    }

    public Date getAvatarDate() {
        return avatarDate;
    }

    public void setAvatarDate(Date avatarDate) {
        this.avatarDate = avatarDate;
    }

}
