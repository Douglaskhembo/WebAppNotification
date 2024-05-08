package AppWeb.AppWeb.service;

import AppWeb.AppWeb.model.*;
import AppWeb.AppWeb.repository.SentEmailRepository;
import AppWeb.AppWeb.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SentEmailRepository sentEmailRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public List<Subscription> findByUser(User user) {
        return subscriptionRepository.findByUser(user);
    }

    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public void sendEmailNotification(User user, List<Update> updates) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("appmanager@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("New Updates Available");
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(user.getUsername()).append(",\n\n");
        sb.append("New updates are available:\n");
        for (Update update : updates) {
            sb.append("- ").append(update.getDescription()).append("\n");
        }
        message.setText(sb.toString());
        javaMailSender.send(message);

        // Save sent email to database
        saveSentEmail(user, "New Updates Available", sb.toString());
    }

    public void subscribeUser(User user, SubscriptionType subscriptionType) {
        // Check if the user is already subscribed to the given subscription type
        if (userAlreadySubscribed(user, subscriptionType)) {
            // If already subscribed, you can handle this scenario accordingly
            // For example, you can send a notification or simply return
            System.out.println("User is already subscribed to " + subscriptionType);
            return;
        }

        // Create a new subscription
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionType);

        // Save the subscription in the database
        subscriptionRepository.save(subscription);

        // Send confirmation email
        sendSubscriptionEmailNotification(user, subscriptionType);
    }

    private boolean userAlreadySubscribed(User user, SubscriptionType subscriptionType) {
        List<Subscription> userSubscriptions = findByUser(user);
        for (Subscription subscription : userSubscriptions) {
            if (subscription.getSubscriptionType() == subscriptionType) {
                return true;
            }
        }
        return false;
    }

    private void sendSubscriptionEmailNotification(User user, SubscriptionType subscriptionType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("appmanager@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Subscription Confirmation");
        String text = "Dear " + user.getUsername() + ",\n\n"
                + "You have successfully subscribed to " + subscriptionType.toString() + ".\n\n"
                + "Thank you!";
        message.setText(text);
        javaMailSender.send(message);

        // Save sent email to database
        saveSentEmail(user, "Subscription Confirmation", text);
    }

    public void sendUnsubscribeEmailNotification(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("appmanager@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Unsubscription Confirmation");
        String text = "Dear " + user.getUsername() + ",\n\n"
                + "You have successfully unsubscribed from a subscription.\n\n"
                + "If this was in error, please contact support.\n\n"
                + "Thank you!";
        message.setText(text);
        javaMailSender.send(message);

        // Save sent email to database
        saveSentEmail(user, "Unsubscription Confirmation", text);
    }

    private void saveSentEmail(User user, String subject, String body) {
        SentEmail sentEmail = new SentEmail();
        sentEmail.setRecipient(user);
        sentEmail.setSubject(subject);
        sentEmail.setBody(body);
        sentEmail.setSentAt(LocalDateTime.now());
        sentEmailRepository.save(sentEmail);
    }
}
