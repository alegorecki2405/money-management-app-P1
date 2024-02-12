package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    void delete(User user);

    void updateUsersBalance(User user, BigDecimal amount, LocalDate date);
}

