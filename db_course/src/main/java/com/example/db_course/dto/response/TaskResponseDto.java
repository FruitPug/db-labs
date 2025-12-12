package com.example.db_course.dto.response;

import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskResponseDto {
    String title;
    String description;

    TaskStatus status;
    TaskPriority priority;

    Long projectId;
    Long creatorUserId;
    Long assigneeUserId;

}
