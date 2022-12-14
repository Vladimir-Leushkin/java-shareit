package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        List<User> users = repository.findAll();
        log.info("Найдены пользователи {}, ", users);
        return users;
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = null;
        try {
            user = UserMapper.toUser(userDto);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Указан неверный формат электронной почты");
        }
        if (user.getEmail() != null) {
            try {
                User saveUser = repository.save(user);
                log.info("Добавлен новый пользователь : {}", user);
                return UserMapper.toUserDto(saveUser);
            } catch (DataIntegrityViolationException e) {
                log.info("Пользователь с такой электронной почтой уже существует {}", user.getEmail());
                throw new ConflictException("Пользователь с такой электронной почтой уже существует");
            }
        } else {
            log.info("Адрес электронной почты не может быть пустым");
            throw new ValidationException("Адрес электронной почты не может быть пустым");
        }
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserDto newUserDto) {
        User user = UserMapper.toUser(newUserDto);
        User oldUser = findUserById(userId);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        try {
            User saveUser = repository.save(oldUser);
            log.info("Обновлен пользователь : {}", saveUser);
            return saveUser;
        } catch (DataIntegrityViolationException e) {
            log.info("Пользователь с таким email уже существует {}", user.getEmail());
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public User findUserById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        log.info("Найден пользователь : {}", user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User user = findUserById(userId);
        log.info("Удален пользователь : {}", user);
        repository.deleteById(userId);
    }

}
