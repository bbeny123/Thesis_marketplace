package kwasilewski.marketplace.dto.requests;

public class UserAdsData extends ListData {

    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
