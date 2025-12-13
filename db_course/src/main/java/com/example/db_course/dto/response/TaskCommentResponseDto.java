package com.example.db_course.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskCommentResponseDto {
    Long taskId;
    Long authorId;
    String body;
}
