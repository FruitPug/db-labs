package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.dto.request.TaskStatusUpdateDto;
import com.example.db_course.dto.response.TaskResponseDto;
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
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void createTask() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
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
        ProjectEntity project = EntityCreator.getProjectEntity();
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

    @Test
    @Transactional
    void updateTaskStatus() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskCreateDto dto = new TaskCreateDto();
        dto.setProjectId(project.getId());
        dto.setCreatorUserId(user.getId());
        dto.setTitle("Task 1");
        dto.setStatus(TaskStatus.TODO);
        dto.setPriority(TaskPriority.MEDIUM);
        taskService.createTask(dto);

        TaskEntity taskEntity = taskRepository.findAll().get(0);

        TaskStatusUpdateDto updateDto = new TaskStatusUpdateDto();
        updateDto.setTaskId(taskEntity.getId());
        updateDto.setStatus(TaskStatus.DONE);
        taskService.updateTaskStatus(updateDto);

        List<TaskEntity> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);
        TaskEntity task = tasks.get(0);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    @Transactional
    void softDeleteTask_marksDeletedAndFiltersFromFindById() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        Long id = task.getId();

        assertThat(taskRepository.findById(id)).isPresent();

        ResponseEntity<Void> response = taskService.softDeleteTask(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        entityManager.flush();
        entityManager.clear();

        assertThat(taskRepository.findById(id)).isEmpty();

        Optional<TaskEntity> raw = taskRepository.findRawById(id);
        assertThat(raw).isPresent();
        assertThat(raw.get().isDeleted()).isTrue();
        assertThat(raw.get().getDeletedAt()).isNotNull();
    }

    @Test
    @Transactional
    void getTasksFiltered_filtersByStatusAndProjectAndExcludesSoftDeleted() {
        ProjectEntity project = ProjectEntity.builder()
                .name("Read test project")
                .description("desc")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project);

        UserEntity creator = UserEntity.builder()
                .email("creator@test.com")
                .fullName("Creator")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(creator);

        UserEntity assignee = UserEntity.builder()
                .email("assignee@test.com")
                .fullName("Assignee")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(assignee);

        TaskEntity t1 = TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(assignee)
                .title("Task 1")
                .description("desc 1")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        taskRepository.save(t1);

        TaskEntity t2 = TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(assignee)
                .title("Task 2")
                .description("desc 2")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
        taskRepository.save(t2);

        TaskEntity t3 = TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(assignee)
                .title("Task 3")
                .description("desc 3")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now().plusDays(3))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        taskRepository.save(t3);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TaskResponseDto> page = taskService
                .getTasksFiltered(TaskStatus.TODO, TaskPriority.MEDIUM, project.getId(), null, pageable)
                .getBody();

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
        TaskResponseDto dto = page.getContent().get(0);
        assertThat(dto.getTitle()).isEqualTo("Task 1");
        assertThat(dto.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(dto.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(dto.getProjectId()).isEqualTo(project.getId());
    }
}

