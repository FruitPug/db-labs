package com.example.db_course.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectCreateDto {

    @NotBlank
    private String name;

    private String description;
}
