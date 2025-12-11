package com.example.db_course.dto.request;

import com.example.db_course.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusUpdateDto {

    @NotNull
    private Long taskId;

    @NotNull
    private TaskStatus status;
}
