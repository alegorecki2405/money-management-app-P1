package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.Role;
import aleksander.gorecki.moneymanagementapp.entity.User;
import aleksander.gorecki.moneymanagementapp.repository.RoleRepository;
import aleksander.gorecki.moneymanagementapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUser(UserDto userDto) {
        User user = createUserFromDto(userDto);
        userRepository.save(user);
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
        user.setRoles(Collections.singletonList(getRole()));
        return user;
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private Role getRole() {
        if (roleRepository.findByName("ROLE_USER") == null) {
            Role role = new Role();
            role.setName("ROLE_USER");
            return roleRepository.save(role);
        } else {
            return roleRepository.findByName("ROLE_USER");
        }
    }
}

