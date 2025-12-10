package com.example.db_course.service;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.entity.*;
import com.example.db_course.entity.enums.*;
import com.example.db_course.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TaskCommentServiceIT extends IntegrationTestBase {

    @Autowired
    private TaskCommentService taskCommentService;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @Transactional
    void createTaskComment() {
        UserEntity user = UserEntity.builder()
                .email("user@test.com")
                .fullName("Test Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        userRepository.save(user);

        ProjectEntity project = ProjectEntity.builder()
                .name("Test_project")
                .description("Project description")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        projectRepository.save(project);

        TaskEntity task = TaskEntity.builder()
                .project(project)
                .title("Test task")
                .description("Task description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .creator(user)
                .assignee(user)
                .dueDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        taskRepository.save(task);

        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(task.getId());
        dto.setAuthorUserId(user.getId());
        dto.setBody("Test comment");

        taskCommentService.createComment(dto);

        List<TaskCommentEntity> taskComments = taskCommentRepository.findAll();
        assertThat(taskComments).hasSize(1);
        TaskCommentEntity taskComment = taskComments.get(0);
        assertThat(taskComment.getTask().getId()).isEqualTo(task.getId());
        assertThat(taskComment.getAuthor().getId()).isEqualTo(user.getId());
        assertThat(taskComment.getBody()).isEqualTo("Test comment");
    }

    @Test
    @Transactional
    void createTaskComment_whenTaskMissing() {
        UserEntity user = UserEntity.builder()
                .email("user@test.com")
                .fullName("Test Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(999999L);
        dto.setAuthorUserId(user.getId());
        dto.setBody("Test comment");

        assertThatThrownBy(() -> taskCommentService.createComment(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
    }
}
