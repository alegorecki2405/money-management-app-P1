package aleksander.gorecki.moneymanagementapp.repository;

import aleksander.gorecki.moneymanagementapp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
