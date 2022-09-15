package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User findUserById(Long id);

    User updateUser(User user);

    void deleteUserById(Long id);
}
