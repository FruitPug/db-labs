package com.example.db_course.mapper;

import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.enums.ProjectStatus;

import java.time.LocalDateTime;

public class ProjectMapper {

    public static ProjectEntity fromCreateDto(ProjectCreateDto projectCreateDto){
        ProjectEntity project = new ProjectEntity();
        project.setName(projectCreateDto.getName());
        project.setDescription(projectCreateDto.getDescription());
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setDeleted(false);

        return project;
    }
}
