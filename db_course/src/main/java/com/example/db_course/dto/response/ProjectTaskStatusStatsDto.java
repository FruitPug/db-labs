package com.example.db_course.dto.response;

import com.example.db_course.entity.enums.TaskStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectTaskStatusStatsDto {
    Long projectId;
    TaskStatus status;
    Long taskCount;
}
