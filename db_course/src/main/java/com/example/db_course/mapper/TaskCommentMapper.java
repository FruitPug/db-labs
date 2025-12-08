package com.example.db_course.mapper;

import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.entity.TaskCommentEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;

import java.time.LocalDateTime;

public class TaskCommentMapper {

    public static TaskCommentEntity createTaskCommentEntity(
            TaskEntity task,
            UserEntity author,
            TaskCommentCreateDto dto
    ) {
        return TaskCommentEntity.builder()
                .task(task)
                .author(author)
                .body(dto.getBody())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }
}
