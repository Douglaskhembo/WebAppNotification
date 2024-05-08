package AppWeb.AppWeb.repository;

import AppWeb.AppWeb.model.SentEmail;
import AppWeb.AppWeb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentEmailRepository extends JpaRepository<SentEmail, Long> {
    List<SentEmail> findByRecipient(User user);
}

