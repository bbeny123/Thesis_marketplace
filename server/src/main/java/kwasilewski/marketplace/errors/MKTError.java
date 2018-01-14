package kwasilewski.marketplace.errors;

public enum MKTError {

    USER_INVALID_CREDENTIALS(1, "Invalid login or password"),
    NOT_AUTHORIZED(2, "Not authorized"),
    WRONG_TOKEN(3, "Wrong token"),
    USER_ALREADY_EXISTS(4, "User already exists"),
    USER_NOT_EXISTS(5, "User does not exists"),
    AD_ALREADY_EXISTS(6, "Ad already exists"),
    AD_NOT_EXISTS(7, "Ad does not exists"),
    FAVOURITE_ALREADY_EXISTS(8, "Favourite already exists"),
    FAVOURITE_NOT_EXISTS(9, "Favourite does not exists");

    private final int code;
    private final String message;

    MKTError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + message;
    }

}
