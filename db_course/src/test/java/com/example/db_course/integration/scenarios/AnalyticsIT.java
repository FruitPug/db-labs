package com.example.db_course.integration.scenarios;

import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.*;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.TaskRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsIT extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private EntityManager entityManager;

    @Test
    @Transactional
    void tasksByProjectAndStatus_returnsCounts_andExcludesDeleted() throws Exception {
        ProjectEntity project1 = ProjectEntity.builder()
                .name("P1")
                .description("d")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project1);

        ProjectEntity project2 = ProjectEntity.builder()
                .name("P2")
                .description("d")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
        projectRepository.save(project2);

        UserEntity creator = UserEntity.builder()
                .email("c@test.com")
                .fullName("Creator")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(creator);

        taskRepository.save(TaskEntity.builder()
                .project(project1)
                .creator(creator)
                .title("t1")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project1)
                .creator(creator)
                .title("t2")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project1)
                .creator(creator)
                .title("t3")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project2)
                .creator(creator)
                .title("t4")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/analytics/projects/tasks-by-status")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.projectId==" + project2.getId() + ")]").isEmpty());

        mockMvc.perform(get("/analytics/projects/tasks-by-status")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.projectId==" + project1.getId()
                        + " && @.status=='TODO')].taskCount").value(1))
                .andExpect(jsonPath("$.content[?(@.projectId==" + project1.getId()
                        + " && @.status=='DONE')].taskCount").value(1));
    }

    @Test
    @Transactional
    void topUsersByDoneTasks_returnsRanking() throws Exception {
        ProjectEntity project = ProjectEntity.builder()
                .name("P")
                .description("d")
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

        UserEntity u1 = UserEntity.builder()
                .email("u1@test.com")
                .fullName("U1")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(u1);

        UserEntity u2 = UserEntity.builder()
                .email("u2@test.com")
                .fullName("U2")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(u2);

        taskRepository.save(TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(u1)
                .title("d1")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(u1)
                .title("d2")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(u2)
                .title("d3")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/analytics/users/top-done")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].userId").value(u1.getId()))
                .andExpect(jsonPath("$.content[0].doneCount").value(2))
                .andExpect(jsonPath("$.content[0].rank").value(1))
                .andExpect(jsonPath("$.content[1].userId").value(u2.getId()))
                .andExpect(jsonPath("$.content[1].doneCount").value(1))
                .andExpect(jsonPath("$.content[1].rank").value(2));
    }
}
