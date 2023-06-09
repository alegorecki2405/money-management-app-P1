package aleksander.gorecki.moneymanagementapp.service;

import aleksander.gorecki.moneymanagementapp.dto.UserDto;
import aleksander.gorecki.moneymanagementapp.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    UserDto findUserByEmailToDto(String email);

    List<UserDto> findAllUsers();
}
