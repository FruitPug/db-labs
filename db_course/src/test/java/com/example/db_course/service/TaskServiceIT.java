package com.example.db_course.service;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import com.example.db_course.entity.enums.UserRole;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.TaskRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class TaskServiceIT extends IntegrationTestBase {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @Transactional
    void createTask() {
        ProjectEntity project = ProjectEntity.builder()
                .name("Test project")
                .description("Project description")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project);

        UserEntity user = UserEntity.builder()
                .email("user@test.com")
                .fullName("Test Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(user);

        TaskCreateDto dto = new TaskCreateDto();
        dto.setProjectId(project.getId());
        dto.setCreatorUserId(user.getId());
        dto.setTitle("Task 1");
        dto.setStatus(TaskStatus.TODO);
        dto.setPriority(TaskPriority.MEDIUM);

        taskService.createTask(dto);

        List<TaskEntity> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);
        TaskEntity task = tasks.get(0);
        assertThat(task.getProject().getId()).isEqualTo(project.getId());
        assertThat(task.getCreator().getId()).isEqualTo(user.getId());
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @Transactional
    void createTask_whenCreatorIsMissing() {
        ProjectEntity project = ProjectEntity.builder()
                .name("Test project")
                .description("Project description")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project);

        TaskCreateDto dto = new TaskCreateDto();
        dto.setProjectId(project.getId());
        dto.setCreatorUserId(999999L);
        dto.setTitle("Task 1");
        dto.setStatus(TaskStatus.TODO);
        dto.setPriority(TaskPriority.MEDIUM);

        assertThatThrownBy(() -> taskService.createTask(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Creator user not found");
    }
}

