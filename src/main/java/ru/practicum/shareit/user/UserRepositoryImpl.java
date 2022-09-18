package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static long userId = 0;

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>(users.values());
        log.info("Найдены пользователи: {} ", userList);
        return userList;
    }

    @Override
    public User save(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {} ", user);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        User user = users.get(id);
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Обновлена информация о пользователе: {}", user);
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        users.remove(id);
        log.info("Удален пользователь: {}", id);
    }

    private long getId() {
        userId += 1;
        return userId;
    }
}
