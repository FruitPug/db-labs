package com.example.db_course.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskReassignDto {

    @NotNull
    private Long taskId;

    @NotNull
    private Long newAssigneeUserId;
}
