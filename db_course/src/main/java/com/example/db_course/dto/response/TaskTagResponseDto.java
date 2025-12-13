package com.example.db_course.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskTagResponseDto {
    Long taskId;
    Long tagId;
}
