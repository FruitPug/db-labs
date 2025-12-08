package com.example.db_course.dto.response;

import com.example.db_course.model.enums.ProjectStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ProjectResponseDto {

    Long id;
    String name;
    String description;
    ProjectStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long ownerUserId;
}
