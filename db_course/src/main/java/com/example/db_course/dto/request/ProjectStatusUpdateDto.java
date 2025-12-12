package com.example.db_course.dto.request;

import com.example.db_course.entity.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectStatusUpdateDto {

    @NotNull
    private Long projectId;

    @NotNull
    private ProjectStatus status;
}
