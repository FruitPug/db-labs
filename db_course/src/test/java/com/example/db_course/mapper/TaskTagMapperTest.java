package com.example.db_course.mapper;

import com.example.db_course.entity.TagEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.TaskTagEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTagMapperTest {

    @Test
    void createTaskTagEntity_setsTaskAndTag() {
        TaskEntity task = TaskEntity.builder()
                .id(1L).build();

        TagEntity tag = TagEntity.builder()
                .id(2L).build();

        TaskTagEntity taskTag = TaskTagMapper.createTaskTagEntity(task, tag);

        assertThat(taskTag.getTask()).isSameAs(task);
        assertThat(taskTag.getTag()).isSameAs(tag);
    }
}
