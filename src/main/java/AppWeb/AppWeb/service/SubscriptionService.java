package AppWeb.AppWeb.service;

import AppWeb.AppWeb.exceptionHandler.UsernameNotFoundException;
import AppWeb.AppWeb.model.*;
//import AppWeb.AppWeb.repository.ApplicationRepository;
import AppWeb.AppWeb.repository.SentEmailRepository;
import AppWeb.AppWeb.repository.SubscriptionRepository;
import AppWeb.AppWeb.repository.UserRepository;
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

//    @Autowired
//    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<Subscription> findByUser(User user) {
        return subscriptionRepository.findByUser(user);
    }

    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public List<Subscription> getUserSubscriptions(User user) {
        // Implement this method to fetch subscriptions for the current user
        return subscriptionRepository.findByUser(user);
    }

    public boolean subscribeToUpdates(User user, String applicationName) {
        // Check if the user is already subscribed to the specified application
        if (userAlreadySubscribed(user, applicationName)) {
            System.out.println("User is already subscribed to " + applicationName);
            return false; // Subscription failed
        }

        // Create a new Subscription entity
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setApplicationName(applicationName);

        // Save the subscription entity to the database
        subscriptionRepository.save(subscription);

        // Send subscription confirmation email notification
        sendSubscriptionEmailNotification(user, applicationName);

        return true; // Subscription successful
    }

    public void unsubscribeFromUpdates(User user, Long subscriptionId) {
        // Retrieve the subscription by ID
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOptional.isPresent()) {
            Subscription subscription = subscriptionOptional.get();
            if (subscription.getUser().getId().equals(user.getId())) { // Check if the subscription belongs to the current user
                // If the subscription belongs to the current user, delete it
                subscriptionRepository.delete(subscription);

                // Send unsubscribe email notification
                sendUnsubscribeEmailNotification(user, subscription.getApplicationName()); // Pass application name

            } else {
                // Handle case where the subscription does not belong to the current user
                System.out.println("Subscription with ID '" + subscriptionId + "' does not belong to the current user.");
            }
        } else {
            // Handle case where the subscription with the given ID does not exist
            System.out.println("Subscription with ID '" + subscriptionId + "' not found.");
        }
    }

    private boolean userAlreadySubscribed(User user, String applicationName) {
        // Check if the user is already subscribed to the given application name
        List<Subscription> userSubscriptions = subscriptionRepository.findByUser(user);
        for (Subscription subscription : userSubscriptions) {
            if (subscription.getApplicationName().equals(applicationName)) {
                return true;
            }
        }
        return false;
    }


    private void sendSubscriptionEmailNotification(User user, String applicationName) {
        // Send subscription confirmation email notification to the user
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("appmanager@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Subscription Confirmation");
        String text = "Dear " + user.getUsername() + ",\n\n"
                + "You have successfully subscribed to updates for " + applicationName + ".\n\n"
                + "Thank you!";
        message.setText(text);
        javaMailSender.send(message);

        System.out.println("Subscription email notification sent.");

        // Save sent email to database
        saveSentEmail(user, "Subscription Confirmation", text);
        System.out.println("Subscription email saved to database.");
    }

    private void sendUnsubscribeEmailNotification(User user, String applicationName) {
        // Send unsubscribe confirmation email notification to the user
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("appmanager@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Unsubscription Confirmation");
        String text = "Dear " + user.getUsername() + ",\n\n"
                + "You have successfully unsubscribed from updates for " + applicationName + ".\n\n"
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
