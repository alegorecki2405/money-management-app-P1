package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.ExpenseType;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.ExpenseTypeRepository;
import aleksander.gorecki.moneymanagementapp.repository.RoleRepository;
import aleksander.gorecki.moneymanagementapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExpenseTypeRepository expenseTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void saveUser(UserDto userDto) {
        defaultExpneseTypes(createUserFromDto(userDto));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    private User createUserFromDto(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setBalance(BigDecimal.ZERO);
        user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public void defaultExpneseTypes(User user) {
        ExpenseType food = new ExpenseType();
        food.setName("FOOD");
        food.setUser(user);
        ExpenseType entertainment = new ExpenseType();
        entertainment.setName("ENTERTAINMENT");
        entertainment.setUser(user);
        ExpenseType bills = new ExpenseType();
        bills.setName("BILLS");
        bills.setUser(user);
        ExpenseType subscriptions = new ExpenseType();
        subscriptions.setName("SUBSCRIPTIONS");
        subscriptions.setUser(user);

        expenseTypeRepository.save(food);
        expenseTypeRepository.save(entertainment);
        expenseTypeRepository.save(bills);
        expenseTypeRepository.save(subscriptions);
    }
}

