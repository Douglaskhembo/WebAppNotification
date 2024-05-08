package AppWeb.AppWeb.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class SubscribeRequest {

    private Long userId;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    // Constructors
    public SubscribeRequest() {
    }

    public SubscribeRequest(Long userId, SubscriptionType subscriptionType) {
        this.userId = userId;
        this.subscriptionType = subscriptionType;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}
