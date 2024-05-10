package AppWeb.AppWeb.controller;

//import AppWeb.AppWeb.model.Application;
import AppWeb.AppWeb.model.Subscription;
import AppWeb.AppWeb.model.User;
import AppWeb.AppWeb.service.ApplicationService;
import AppWeb.AppWeb.service.SubscriptionService;
import AppWeb.AppWeb.service.UserService;
import AppWeb.AppWeb.service.WindowsApplicationManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SubscriptionController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @GetMapping("/subscription.html")
    public String showSubscriptionForm(Model model) {
        // Fetch the list of installed applications
        List<String> installedApplications = WindowsApplicationManager.getInstalledApplications();
        model.addAttribute("installedApplications", installedApplications);
        return "subscription";
    }

    @PostMapping("/subscription")
    public String subscribeToUpdates(@RequestParam("application") String applicationName,
                                     RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // Get the current user from the session or any other mechanism
        User user = userService.getCurrentUser(request);

        // Subscribe the user to updates for the selected application
        boolean subscribed = subscriptionService.subscribeToUpdates(user, applicationName);

        if (subscribed) {
            // Add success message
            redirectAttributes.addFlashAttribute("successMessage", "Subscribed successfully to updates for " + applicationName);
        } else {
            // Add error message
            redirectAttributes.addFlashAttribute("errorMessage", "You are already subscribed to updates for " + applicationName);
        }

        return "redirect:/dashboard";
    }


    @GetMapping("/unsubscribe")
    public String showUnsubscribeForm(Model model, HttpServletRequest request) {
        // Get the current user from the session or any other mechanism
        User user = userService.getCurrentUser(request);

        if (user == null) {
            // Handle unauthenticated access
            // For example, redirect to the login page
            return "redirect:/logout";
        } else {
            // Fetch the list of subscribed applications for the logged-in user
            List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(user);
            model.addAttribute("subscriptions", subscriptions);
            return "unsubscribe";
        }
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(@RequestParam("subscriptionId") Long subscriptionId, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // Get the current user from the session or any other mechanism
        User user = userService.getCurrentUser(request);

        if (user == null) {
            // Handle unauthenticated access
            // For example, redirect to the login page
            return "redirect:/logout";
        } else {
            // Unsubscribe from updates
            subscriptionService.unsubscribeFromUpdates(user, subscriptionId);

            // Add success message
            redirectAttributes.addFlashAttribute("successMessage", "Unsubscribed successfully");

            return "redirect:/dashboard"; // Redirect to subscription page or any other page as needed
        }
    }


}
