package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.entity.Role;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.RoleRepository;
import aleksander.gorecki.moneymanagementapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("ROLE_USER") == null) {
            Role role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }

        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            roleRepository.save(role);
        }

        if (userRepository.findByEmail("admin@gmail.com") == null) {
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setName("admin admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(List.of(roleRepository.findByName("ROLE_ADMIN")));
            admin.setBalance(BigDecimal.ZERO);
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("alegorecki@gmail.com") == null) {
            User user = new User();
            user.setEmail("alegorecki@gmail.com");
            user.setName("Aleksander Górecki");
            user.setPassword(passwordEncoder.encode("Q"));
            user.setRoles(List.of(roleRepository.findByName("ROLE_USER")));
            user.setBalance(BigDecimal.valueOf(1000));
            user.updateBalance(BigDecimal.ZERO, LocalDate.now().minusYears(1), "initial balance");
            User saved = userRepository.save(user);
            userService.defaultExpeneseTypes(saved);
            userService.defaultIncomeTypes(saved);
        }
    }
}