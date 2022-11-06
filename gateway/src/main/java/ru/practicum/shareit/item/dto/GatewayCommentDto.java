package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GatewayCommentDto {
    private Long id;
    @NotBlank
    @Size(min = 2, max = 500)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
