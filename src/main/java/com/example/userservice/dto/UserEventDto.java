package com.example.userservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEventDto {
    private String email;
    private String eventType;
    private String timestamp;
}
