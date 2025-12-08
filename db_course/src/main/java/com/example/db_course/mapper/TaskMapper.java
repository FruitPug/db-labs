package com.example.db_course.mapper;

import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;

import java.time.LocalDateTime;

public class TaskMapper {

    public static TaskEntity createTaskEntity(
            ProjectEntity project,
            UserEntity creator,
            UserEntity assignee,
            TaskCreateDto dto
    ) {
        return TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(assignee)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .dueDate(dto.getDueDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }
}
