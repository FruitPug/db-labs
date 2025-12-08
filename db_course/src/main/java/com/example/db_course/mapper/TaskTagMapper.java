package com.example.db_course.mapper;

import com.example.db_course.entity.TagEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.TaskTagEntity;

public class TaskTagMapper {

    public static TaskTagEntity createTaskTagEntity(
            TaskEntity task,
            TagEntity tag
    ) {
        return TaskTagEntity.builder()
                .task(task)
                .tag(tag)
                .build();
    }
}
