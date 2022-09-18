package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
