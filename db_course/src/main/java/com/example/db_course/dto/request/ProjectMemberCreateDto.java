package com.example.db_course.dto.request;

import com.example.db_course.entity.enums.ProjectMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectMemberCreateDto {

    @NotNull
    private Long projectId;

    @NotNull
    private Long userId;

    @NotNull
    private ProjectMemberRole role;
}
