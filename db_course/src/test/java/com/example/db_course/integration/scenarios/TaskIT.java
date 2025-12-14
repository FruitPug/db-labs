package com.example.db_course.integration.scenarios;

import com.example.db_course.EntityCreator;
import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.dto.request.TaskReassignDto;
import com.example.db_course.dto.request.TaskStatusUpdateDto;
import com.example.db_course.entity.*;
import com.example.db_course.entity.enums.*;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskIT extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectMemberRepository projectMemberRepository;
    @Autowired private TaskCommentRepository taskCommentRepository;

    @Autowired private EntityManager entityManager;

    @Autowired private org.springframework.transaction.PlatformTransactionManager transactionManager;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    void createTask() throws Exception {
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

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        List<TaskEntity> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);
        TaskEntity task = tasks.get(0);
        assertThat(task.getProject().getId()).isEqualTo(project.getId());
        assertThat(task.getCreator().getId()).isEqualTo(user.getId());
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @Transactional
    void createTask_whenCreatorIsMissing() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        TaskCreateDto dto = new TaskCreateDto();
        dto.setProjectId(project.getId());
        dto.setCreatorUserId(999999L);
        dto.setTitle("Task 1");
        dto.setStatus(TaskStatus.TODO);
        dto.setPriority(TaskPriority.MEDIUM);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    void updateTaskStatus() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        task.setStatus(TaskStatus.TODO);
        taskRepository.save(task);

        TaskStatusUpdateDto updateDto = new TaskStatusUpdateDto();
        updateDto.setTaskId(task.getId());
        updateDto.setStatus(TaskStatus.DONE);

        mockMvc.perform(patch("/tasks/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        TaskEntity reloaded = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    @Transactional
    void reassignTask_whenAssigneeIsProjectMember_updatesAssignee() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
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

        UserEntity oldAssignee = UserEntity.builder()
                .email("old@test.com")
                .fullName("Old")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(oldAssignee);

        UserEntity newAssignee = UserEntity.builder()
                .email("new@test.com")
                .fullName("New")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(newAssignee);

        projectMemberRepository.save(ProjectMemberEntity.builder()
                .project(project)
                .user(creator)
                .role(ProjectMemberRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build());

        projectMemberRepository.save(ProjectMemberEntity.builder()
                .project(project)
                .user(oldAssignee)
                .role(ProjectMemberRole.CONTRIBUTOR)
                .joinedAt(LocalDateTime.now())
                .build());

        projectMemberRepository.save(ProjectMemberEntity.builder()
                .project(project)
                .user(newAssignee)
                .role(ProjectMemberRole.CONTRIBUTOR)
                .joinedAt(LocalDateTime.now())
                .build());

        TaskEntity task = EntityCreator.getTaskEntity(creator, project);
        taskRepository.save(task);

        TaskReassignDto dto = new TaskReassignDto();
        dto.setTaskId(task.getId());
        dto.setNewAssigneeUserId(newAssignee.getId());

        mockMvc.perform(patch("/tasks/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        TaskEntity reloaded = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(reloaded.getAssignee().getId()).isEqualTo(newAssignee.getId());
    }

    @Test
    @Transactional
    void reassignTask_whenAssigneeNotProjectMember_throws() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
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

        UserEntity currentAssignee = UserEntity.builder()
                .email("assignee@test.com")
                .fullName("Assignee")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(currentAssignee);

        UserEntity outsider = UserEntity.builder()
                .email("outsider@test.com")
                .fullName("Outsider")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(outsider);

        projectMemberRepository.save(ProjectMemberEntity.builder()
                .project(project)
                .user(creator)
                .role(ProjectMemberRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build());

        projectMemberRepository.save(ProjectMemberEntity.builder()
                .project(project)
                .user(currentAssignee)
                .role(ProjectMemberRole.CONTRIBUTOR)
                .joinedAt(LocalDateTime.now())
                .build());

        TaskEntity task = EntityCreator.getTaskEntity(creator, project);
        taskRepository.save(task);

        TaskReassignDto dto = new TaskReassignDto();
        dto.setTaskId(task.getId());
        dto.setNewAssigneeUserId(outsider.getId());

        mockMvc.perform(patch("/tasks/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void optimisticLock_conflictOnStaleDetachedEntity_throws() {
        org.springframework.transaction.support.TransactionTemplate tx =
                new org.springframework.transaction.support.TransactionTemplate(transactionManager);

        Long taskId = tx.execute(status -> {
            ProjectEntity project = EntityCreator.getProjectEntity();
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

            UserEntity u1 = UserEntity.builder()
                    .email("u1@test.com")
                    .fullName("U1")
                    .role(UserRole.DEVELOPER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .deleted(false)
                    .build();
            userRepository.save(u1);

            projectMemberRepository.save(ProjectMemberEntity.builder()
                    .project(project)
                    .user(creator)
                    .role(ProjectMemberRole.OWNER)
                    .joinedAt(LocalDateTime.now())
                    .build());

            projectMemberRepository.save(ProjectMemberEntity.builder()
                    .project(project)
                    .user(u1)
                    .role(ProjectMemberRole.CONTRIBUTOR)
                    .joinedAt(LocalDateTime.now())
                    .build());

            TaskEntity task = EntityCreator.getTaskEntity(creator, project);
            taskRepository.save(task);

            entityManager.flush();
            entityManager.clear();
            return task.getId();
        });

        TaskEntity stale = tx.execute(status -> {
            Assertions.assertNotNull(taskId);
            TaskEntity t = taskRepository.findById(taskId).orElseThrow();
            entityManager.detach(t);
            return t;
        });

        tx.execute(status -> {
            Assertions.assertNotNull(taskId);
            TaskEntity fresh = taskRepository.findById(taskId).orElseThrow();
            fresh.setTitle("Fresh update");
            fresh.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(fresh);
            entityManager.flush();
            entityManager.clear();
            return null;
        });

        assertThatThrownBy(() -> tx.execute(status -> {
            Assertions.assertNotNull(stale);
            stale.setTitle("Stale update");
            stale.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(stale);
            entityManager.flush();
            return null;
        }))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class)
                .hasRootCauseInstanceOf(org.hibernate.StaleObjectStateException.class);

        cleanDb();
    }

    void cleanDb() {
        jdbcTemplate.execute("TRUNCATE TABLE task_tags RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE task_comments RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE tasks RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE project_members RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE tags RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE projects RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    @Transactional
    void softDeleteTask_marksDeletedAndFiltersFromFindById() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        TaskCommentEntity taskComment = EntityCreator.getTaskCommentEntity(user, task);
        taskCommentRepository.save(taskComment);

        Long id = task.getId();

        assertThat(taskRepository.findById(id)).isPresent();

        mockMvc.perform(delete("/tasks/{id}", id))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        assertThat(taskRepository.findById(id)).isEmpty();
        assertThat(taskCommentRepository.findById(taskComment.getId())).isEmpty();

        Optional<TaskEntity> rawTask = taskRepository.findRawById(id);
        assertThat(rawTask).isPresent();
        assertThat(rawTask.get().isDeleted()).isTrue();
        assertThat(rawTask.get().getDeletedAt()).isNotNull();

        Optional<TaskCommentEntity> rawTaskComment = taskCommentRepository.findRawById(taskComment.getId());
        assertThat(rawTaskComment).isPresent();
        assertThat(rawTaskComment.get().isDeleted()).isTrue();
        assertThat(rawTaskComment.get().getDeletedAt()).isNotNull();
    }

    @Test
    @Transactional
    void getTasksFiltered_filtersByStatusAndPriorityAndExcludesSoftDeleted() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity creator = EntityCreator.getUserEntity();
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

        TaskEntity task1 = TaskEntity.builder()
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
        taskRepository.save(task1);

        TaskEntity task2 = TaskEntity.builder()
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
        taskRepository.save(task2);

        TaskEntity task3 = TaskEntity.builder()
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
        taskRepository.save(task3);

        mockMvc.perform(get("/tasks")
                        .param("status", "TODO")
                        .param("priority", "MEDIUM")
                        .param("projectId", project.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value(task1.getTitle()))
                .andExpect(jsonPath("$.content[0].status").value(task1.getStatus().name()))
                .andExpect(jsonPath("$.content[0].priority").value(task1.getPriority().name()))
                .andExpect(jsonPath("$.content[0].projectId").value(project.getId()));
    }
}
