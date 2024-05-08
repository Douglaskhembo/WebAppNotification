package AppWeb.AppWeb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @Column(updatable = false)
    private LocalDateTime subscriptionDate = LocalDateTime.now();

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SubscriptionUpdate> subscriptionUpdates;
}

