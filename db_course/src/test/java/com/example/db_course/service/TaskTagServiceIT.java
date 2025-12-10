package com.example.db_course.service;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TaskTagCreateDto;
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

public class TaskTagServiceIT extends IntegrationTestBase {

    @Autowired
    private TaskTagService taskTagService;

    @Autowired
    private TaskTagRepository taskTagRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TagRepository tagRepository;

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

        TagEntity tag = TagEntity.builder()
                .name("test_tag")
                .color("red")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(task.getId());
        dto.setTagId(tag.getId());

        taskTagService.createTaskTag(dto);

        List<TaskTagEntity> taskTags = taskTagRepository.findAll();
        assertThat(taskTags).hasSize(1);
        TaskTagEntity taskTag = taskTags.get(0);
        assertThat(taskTag.getTask().getId()).isEqualTo(task.getId());
        assertThat(taskTag.getTag().getId()).isEqualTo(tag.getId());
    }

    @Test
    @Transactional
    void createTaskTag_whenTaskIsMissing() {
        TagEntity tag = TagEntity.builder()
                .name("test_tag")
                .color("red")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(999999L);
        dto.setTagId(tag.getId());

        assertThatThrownBy(() -> taskTagService.createTaskTag(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task not found");
    }
}
