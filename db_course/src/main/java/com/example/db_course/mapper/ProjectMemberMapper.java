package com.example.db_course.mapper;

import com.example.db_course.dto.response.ProjectMemberResponseDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;

import java.time.LocalDateTime;

public class ProjectMemberMapper {

    public static ProjectMemberEntity fromCreateDto(
            ProjectEntity project,
            UserEntity user,
            ProjectMemberRole role
    ){
        return ProjectMemberEntity.builder()
                .project(project)
                .user(user)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public static ProjectMemberResponseDto toResponseDto(ProjectMemberEntity projectMember) {
        return ProjectMemberResponseDto.builder()
                .projectId(projectMember.getId())
                .userId(projectMember.getId())
                .role(projectMember.getRole())
                .build();
    }
}
