package kwasilewski.marketplace.dto.user;

import kwasilewski.marketplace.util.MRKUtil;

public class PasswordData {

    private String oldPassword;
    private String newPassword;

    public PasswordData(String email, String oldPassword, String newPassword) {
        this.oldPassword = MRKUtil.encodePassword(email, oldPassword);
        this.newPassword = MRKUtil.encodePassword(email, newPassword);
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
