package com.example.db_course.dto.response;

import com.example.db_course.entity.enums.ProjectMemberRole;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectMemberResponseDto {
    Long projectId;
    Long userId;
    ProjectMemberRole role;
}
