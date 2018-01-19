package kwasilewski.marketplace.errors;

public enum MKTError {

    USER_INVALID_CREDENTIALS(1, "Invalid login or password"),
    NOT_AUTHORIZED(2, "Not authorized"),
    WRONG_TOKEN(3, "Wrong token"),
    PASSWORDS_NOT_MATCH(4, "Password does not match"),
    USER_ALREADY_EXISTS(5, "User already exists"),
    USER_NOT_EXISTS(6, "User does not exists"),
    AD_ALREADY_EXISTS(7, "Ad already exists"),
    AD_NOT_EXISTS(8, "Ad does not exists"),
    FAVOURITE_ALREADY_EXISTS(9, "Favourite already exists"),
    FAVOURITE_NOT_EXISTS(10, "Favourite does not exists"),
    FAVOURITE_OWN_AD(11, "Favourite own ad");

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
