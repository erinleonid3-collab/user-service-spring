package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Getter
@Setter
@Relation(value = "user", collectionRelation = "users")
public class UserDtoWithLinks extends RepresentationModel<UserDtoWithLinks> {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;
}
