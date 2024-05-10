package AppWeb.AppWeb.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

public class SubscribeRequest {

    private Long userId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Application application;

    // Constructors
    public SubscribeRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

//    public Application getApplication() {
//        return application;
//    }
//
//    public void setApplication(Application application) {
//        this.application = application;
//    }
}
