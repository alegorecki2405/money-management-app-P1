package aleksander.gorecki.moneymanagementapp.repository;

import aleksander.gorecki.moneymanagementapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}