package kwasilewski.marketplace.dtoext.user;

public class PasswordDataExt {

    private String oldPassword;
    private String newPassword;

    public PasswordDataExt() {
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
