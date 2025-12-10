package com.example.db_course.repository;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskCommentEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import com.example.db_course.entity.enums.UserRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TaskCommentRepositoryIT extends IntegrationTestBase {

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Transactional
    void findByAuthorAndTask_returnsSavedComment() {
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

        TaskCommentEntity taskComment = TaskCommentEntity.builder()
                .task(task)
                .author(user)
                .body("Task comment description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        taskCommentRepository.save(taskComment);

        Optional<TaskCommentEntity> found = taskCommentRepository.findByAuthorAndTask(user, task);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(taskComment.getId());
    }
}
