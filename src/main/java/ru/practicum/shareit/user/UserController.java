package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> userDto = new ArrayList<>();
        List<User> users = userService.getAllUsers();
        userDto = users.stream().map(user -> UserMapper.toUserDto(user)).collect(Collectors.toList());
        return userDto;
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.saveUser(userDto));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.updateUser(id, userDto));
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {

        return UserMapper.toUserDto(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
