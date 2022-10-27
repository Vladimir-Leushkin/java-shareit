package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    List<User> getAllUsers();

    User updateUser(Long id, UserDto userDto);

    User findUserById(Long id);

    void deleteUserById(Long id);

}
