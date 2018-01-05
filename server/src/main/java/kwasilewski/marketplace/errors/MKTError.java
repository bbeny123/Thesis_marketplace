package kwasilewski.marketplace.errors;

public enum MKTError {

    USER_INVALID_LOGIN_OR_PASSWORD(1, "Invalid login or password"),
    USER_ALREADY_EXISTS(2, "User already exists"),
    USER_NOT_EXISTS(3, "User does not exists"),
    WRONG_TOKEN(4, "Wrong token"),
    NOT_AUTHORIZED(5, "Not authorized");

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
