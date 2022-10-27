package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserServiceImpl userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserDto userDto;
    private UserDto newUserDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository, userMapper);

        userDto = new UserDto(1L, "John", "john.doe@mail.com");
        newUserDto = new UserDto(1L, "John", "john.doe@mail.com");
        user = new User(1L, "John", "john.doe@mail.com");

    }

    @Test
    void saveUser() {
        when(userRepository.save(UserMapper.toUser(userDto)))
                .thenReturn(user);
        final UserDto newUser = userService.saveUser(userDto);
        assertThat(userDto.getId(), is(1L));
        assertEquals(userDto, newUser);
        verify(userRepository, times(1))
                .save(UserMapper.toUser(userDto));
    }

    @Test
    void saveUserWithEmptyEmail() {
        userDto.setEmail(null);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.saveUser(userDto));
        assertEquals("Адрес электронной почты не может быть пустым", exception.getMessage());

    }

    @Test
    void saveUserWithWrongEmail() {
        when(userRepository.save(user))
                .thenThrow(new ConflictException("Пользователь с такой электронной почтой уже существует"));
        final ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userService.saveUser(userDto));
        assertEquals("Пользователь с такой электронной почтой уже существует", exception.getMessage());
        verify(userRepository, times(1))
                .save(user);

    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        final List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userRepository, times(1))
                .findAll();
    }

    @Test
    void updateUser() {
        when(userRepository.save(user))
                .thenReturn(user);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        final User newUser = userService.updateUser(user.getId(), userDto);
        assertEquals(user, newUser);
        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void findUserById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        final User newUser = userService.findUserById(user.getId());
        assertEquals(user, newUser);
        verify(userRepository, times(1))
                .findById(user.getId());
    }

    @Test
    void findUserByWrongId() {
        when(userRepository.findById(user.getId()))
                .thenThrow(new NotFoundException("Не найден пользователь с id = " + user.getId()));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.findUserById(user.getId()));
        assertEquals("Не найден пользователь с id = " + user.getId(), exception.getMessage());

        verify(userRepository, times(1))
                .findById(user.getId());
    }

    @Test
    void deleteUser() {
        findUserById();
        userService.deleteUserById(1L);
        verify(userRepository, times(1))
                .deleteById(1L);
    }
}
