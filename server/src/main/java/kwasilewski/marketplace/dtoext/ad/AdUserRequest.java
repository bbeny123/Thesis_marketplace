package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dtoext.ListRequest;

public class AdUserRequest extends ListRequest {

    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
