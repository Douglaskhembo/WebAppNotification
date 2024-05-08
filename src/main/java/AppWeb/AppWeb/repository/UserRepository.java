package AppWeb.AppWeb.repository;

import AppWeb.AppWeb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM _user WHERE username = ?1", nativeQuery = true)
    User findByUsername(String username);

    @Query(value = "SELECT * FROM _user WHERE email = ?1", nativeQuery = true)
    User findByEmail(String email);
}

