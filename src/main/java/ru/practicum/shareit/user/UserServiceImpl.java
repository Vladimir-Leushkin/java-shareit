package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = repository.findAll();
        usersDto = users.stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
        return usersDto;
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        validateEmail(userDto);
        validateDuplicateEmail(userDto);
        User user = UserMapper.toUser(userDto);
        User saveUser = repository.save(user);
        return UserMapper.toUserDto(saveUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto newUserDto) {
        checkUserById(id);
        User user = repository.findUserById(id);
        if (newUserDto.getName() == null || newUserDto.getName().isEmpty() || newUserDto.getName().isBlank()) {
            newUserDto.setName(user.getName());
        }
        validateDuplicateEmail(newUserDto);
        if (newUserDto.getEmail() == null || newUserDto.getEmail().isEmpty() || newUserDto.getEmail().isBlank()) {
            newUserDto.setEmail(user.getEmail());
        }
        newUserDto.setId(id);
        User newUser = UserMapper.toUser(newUserDto);
        repository.updateUser(newUser);
        return newUserDto;
    }

    @Override
    public UserDto findUserById(Long id) {
        checkUserById(id);
        User user = repository.findUserById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        checkUserById(id);
        repository.deleteUserById(id);
    }

    @Override
    public void checkUserById(Long id) {
        List<User> users = repository.findAll();
        Map<Long, User> usersMap = new HashMap<>();
        for (User user : users) {
            usersMap.put(user.getId(), user);
        }
        if (!usersMap.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private boolean validateEmail(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || userDto.getEmail().isBlank() ||
                !userDto.getEmail().contains("@")) {
            throw new ValidationException("Адрес электронной почты не может быть пустым");
        }
        return true;
    }

    private boolean validateDuplicateEmail(UserDto userDto) {
        List<UserDto> usersDto = getAllUsers();
        for (UserDto newUserDto : usersDto) {
            if (newUserDto.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }
        return true;
    }

}
