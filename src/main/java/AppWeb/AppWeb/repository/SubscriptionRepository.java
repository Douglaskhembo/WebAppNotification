package AppWeb.AppWeb.repository;

import AppWeb.AppWeb.model.Subscription;
import AppWeb.AppWeb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);

//    List<Subscription> findByUserAndSubscriptionType(User user, SubscriptionType subscriptionType);

//    @Query("SELECT s FROM Subscription s WHERE s.user = :user AND s.subscriptionType = :subscriptionType")
//    List<Subscription> findByUserAndSubscriptionType(User user, SubscriptionType subscriptionType);

}
