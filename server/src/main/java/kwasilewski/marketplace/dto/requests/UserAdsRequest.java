package kwasilewski.marketplace.dto.requests;

public class UserAdsRequest extends ListRequest {

    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
