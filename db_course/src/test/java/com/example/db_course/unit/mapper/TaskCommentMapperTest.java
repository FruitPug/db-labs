package com.example.db_course.unit.mapper;

import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.entity.TaskCommentEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.mapper.TaskCommentMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskCommentMapperTest {

    @Test
    void createTaskCommentEntity_mapsFieldsAndSetsDefaults() {
        TaskEntity task = TaskEntity.builder()
                .id(1L).build();

        UserEntity author = UserEntity.builder()
                .id(2L).build();

        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(1L);
        dto.setAuthorUserId(2L);
        dto.setBody("Looks good to me");

        TaskCommentEntity comment = TaskCommentMapper.createTaskCommentEntity(task, author, dto);

        assertThat(comment.getTask()).isSameAs(task);
        assertThat(comment.getAuthor()).isSameAs(author);
        assertThat(comment.getBody()).isEqualTo("Looks good to me");
        assertThat(comment.isDeleted()).isFalse();
        assertThat(comment.getCreatedAt()).isNotNull();
        assertThat(comment.getUpdatedAt()).isNotNull();
    }
}
