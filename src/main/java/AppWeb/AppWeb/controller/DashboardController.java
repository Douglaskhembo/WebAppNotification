package AppWeb.AppWeb.controller;

import AppWeb.AppWeb.model.*;
import AppWeb.AppWeb.repository.SentEmailRepository;
import AppWeb.AppWeb.service.SubscriptionService;
import AppWeb.AppWeb.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SentEmailRepository sentEmailRepository; // Add SentEmailRepository dependency

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user != null) {
            List<Subscription> subscriptions = subscriptionService.findByUser(user);
            List<Update> updates = fetchUpdates(subscriptions);
            model.addAttribute("updates", updates);
            try {
                sendEmailNotification(user, updates);
            } catch (Exception e) {
                e.printStackTrace(); // Print the stack trace for debugging
            }
            return "dashboard";
        } else {
            return "redirect:/login"; // Redirect to login page if user is not authenticated
        }
    }

    // Method to fetch updates based on user subscriptions
    private List<Update> fetchUpdates(List<Subscription> subscriptions) {
        List<Update> updates = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            // Assuming Subscription entity has a relationship with SubscriptionUpdate entities
            List<SubscriptionUpdate> subscriptionUpdates = subscription.getSubscriptionUpdates();
            for (SubscriptionUpdate subscriptionUpdate : subscriptionUpdates) {
                updates.add(subscriptionUpdate.getUpdate());
            }
        }
        return updates;
    }



    public void sendEmailNotification(User user, List<Update> updates) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(String.valueOf(new InternetAddress("appmanager@gmail.com")));
        helper.setTo(user.getEmail());
        helper.setSubject("Successful login");

        // Process the email template
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("updates", updates);
        String emailContent = templateEngine.process("update_email", context);

        helper.setText(emailContent, true);
        javaMailSender.send(message);
    }

    @GetMapping("/update_email")
    public String updateEmail() {
        return "update_email";
    }

    @GetMapping("/delete_user")
    public String deleteAccount(Model model) {
        // You can add any necessary model attributes here
        return "delete_user";
    }

    @PostMapping("/delete_user")
    public String deleteUser(String email, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(email);
        if (user != null) {
            userService.deleteUserByEmail(email);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
            return "redirect:/login"; // Redirect to logout if user is deleted successfully
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User with email '" + email + "' not found");
        }
        return "redirect:/delete_user";
    }
    @GetMapping("/sentEmail.html")
    public String getSentEmailsPage(Model model, HttpSession session){
        // Retrieve the current authenticated user directly, assuming it's already authenticated during login
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // Handle case where user is not authenticated
            System.out.println("User not authenticated"); // Add this line for logging
            return "sentEmail"; // Redirect to login page
        }

        // Retrieve sent emails for the current user
        List<SentEmail> sentEmails = sentEmailRepository.findByRecipient(loggedInUser);
        model.addAttribute("sentEmails", sentEmails);
        // User is authenticated
        model.addAttribute("loggedInUserId", loggedInUser.getId());
        return "sentEmail";
    }



}
