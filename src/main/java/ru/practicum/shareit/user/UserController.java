package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAllUsers(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size, Sort.unsorted());
        List<UserDto> userDto = new ArrayList<>();
        Page<User> users = userService.getAllUsers(pageRequest);
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
