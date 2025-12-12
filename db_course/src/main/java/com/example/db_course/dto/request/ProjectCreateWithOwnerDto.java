package com.example.db_course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectCreateWithOwnerDto {

    @NotBlank

    private String name;

    @NotNull
    private Long ownerId;

    private String description;
}
