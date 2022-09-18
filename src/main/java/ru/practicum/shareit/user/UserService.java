package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserDto userDto);

    UserDto findUserById(Long id);

    void deleteUserById(Long id);

    void checkUserById(Long id);
}
