package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDto = users.stream().map(user -> UserMapper.toUserDto(user)).collect(Collectors.toList());
        return userDto;
    }

    @PostMapping
    public UserDto saveNewUser(@RequestBody UserDto userDto) {
        UserDto saveUserDto = userService.saveUser(userDto);
        return saveUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.updateUser(id, userDto));
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        UserDto saveUserDto = UserMapper.toUserDto(user);
        return saveUserDto;
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
