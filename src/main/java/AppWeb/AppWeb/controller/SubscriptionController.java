package AppWeb.AppWeb.controller;

import AppWeb.AppWeb.model.Subscription;
import AppWeb.AppWeb.model.SubscriptionType;
import AppWeb.AppWeb.model.User;
import AppWeb.AppWeb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import AppWeb.AppWeb.repository.SubscriptionRepository;
import AppWeb.AppWeb.repository.UserRepository;
import AppWeb.AppWeb.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class SubscriptionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @GetMapping("/subscription.html")
    public String getSubscriptionPage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // Handle case where user is not authenticated
            System.out.println("User not authenticated"); // Add this line for logging
            return "redirect:/login"; // Redirect to login page
        }

        // User is authenticated
        model.addAttribute("loggedInUserId", loggedInUser.getId());
        return "subscription";
    }

    @GetMapping("/types")
    public ResponseEntity<List<SubscriptionType>> getSubscriptionTypes() {
        List<SubscriptionType> subscriptionTypes = Arrays.asList(SubscriptionType.values());
        return ResponseEntity.ok(subscriptionTypes);
    }

    @PostMapping("/subscription")
    public String subscribeUser(@RequestParam("subscriptionType") String subscriptionType, Model model,
                                HttpServletRequest request) {
        // No need to check for authentication here

        // Validate subscription type
        SubscriptionType type = SubscriptionType.valueOf(subscriptionType);
        if (type == null) {
            // Handle invalid subscription type
            model.addAttribute("errorMessage", "Invalid subscription type");
            return "redirect:/dashboard";
        }

        // For demonstration purposes, let's assume the user is retrieved from the session
        User user = userService.getCurrentUser(request);
        if (user == null) {
            // Handle case where user is not found in session
            model.addAttribute("errorMessage", "User not found in session");
            return "redirect:/dashboard";
        }

        // Use the SubscriptionService to handle subscription
        subscriptionService.subscribeUser(user, type);

        // Return a success response
        model.addAttribute("successMessage", "Subscribed successfully");
        return "redirect:/dashboard";
    }


    @GetMapping("/unsubscribe")
    public String getUnsubscribePage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            System.out.println("User not authenticated");
            return "redirect:/login";
        }

        // Retrieve subscriptions for the logged-in user
        List<Subscription> subscriptions = subscriptionService.findByUser(loggedInUser);
        model.addAttribute("subscriptions", subscriptions);
        return "unsubscribe";
    }


    // Add this new endpoint for unsubscribing
    @PostMapping("/unsubscribe")
    public String unsubscribeUser(@RequestParam("subscriptionId") Long subscriptionId, Model model) {
        Optional<Subscription> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        if (optionalSubscription.isPresent()) {
            Subscription subscription = optionalSubscription.get();
            User user = subscription.getUser();
            subscriptionRepository.delete(subscription); // Delete the subscription

            // Send email notification
            subscriptionService.sendUnsubscribeEmailNotification(user);

            // Redirect back to the subscription page with success message
            model.addAttribute("successMessage", "Unsubscribed successfully");
            return "redirect:/dashboard";
        } else {
            // Subscription not found, handle this case
            model.addAttribute("errorMessage", "Subscription not found");
            return "redirect:/unsubscribe";
        }
    }
}
