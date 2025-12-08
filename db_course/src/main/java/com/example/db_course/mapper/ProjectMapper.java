package com.example.db_course.mapper;

import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.enums.ProjectStatus;

import java.time.LocalDateTime;

public class ProjectMapper {

    public static ProjectEntity fromCreateDto(ProjectCreateDto projectCreateDto){
        return ProjectEntity.builder()
                .name(projectCreateDto.getName())
                .description(projectCreateDto.getDescription())
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }
}
