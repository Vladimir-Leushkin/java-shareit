package ru.practicum.shareit.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User saveUser(UserDto userDto);

    Page<User> getAllUsers(PageRequest pageRequest);

    User updateUser(Long id, UserDto userDto);

    User findUserById(Long id);

    void deleteUserById(Long id);

}
