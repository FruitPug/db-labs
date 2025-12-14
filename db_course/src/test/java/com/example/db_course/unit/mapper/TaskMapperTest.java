package com.example.db_course.unit.mapper;

import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import com.example.db_course.mapper.TaskMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

    @Test
    void createTaskEntity_mapsAllFieldsAndSetsDefaults() {
        ProjectEntity project = ProjectEntity.builder()
                .id(1L).build();

        UserEntity creator = UserEntity.builder()
                .id(2L).build();

        UserEntity assignee = UserEntity.builder()
                .id(3L).build();

        TaskCreateDto dto = new TaskCreateDto();
        dto.setProjectId(1L);
        dto.setCreatorUserId(2L);
        dto.setAssigneeUserId(3L);
        dto.setTitle("Task title");
        dto.setDescription("Do something important");
        dto.setStatus(TaskStatus.IN_PROGRESS);
        dto.setPriority(TaskPriority.HIGH);
        dto.setDueDate(LocalDate.now().plusDays(5));

        TaskEntity task = TaskMapper.createTaskEntity(project, creator, assignee, dto);

        assertThat(task.getProject()).isSameAs(project);
        assertThat(task.getCreator()).isSameAs(creator);
        assertThat(task.getAssignee()).isSameAs(assignee);
        assertThat(task.getTitle()).isEqualTo("Task title");
        assertThat(task.getDescription()).isEqualTo("Do something important");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(task.getDueDate()).isEqualTo(dto.getDueDate());
        assertThat(task.isDeleted()).isFalse();
        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getUpdatedAt()).isNotNull();
    }
}
