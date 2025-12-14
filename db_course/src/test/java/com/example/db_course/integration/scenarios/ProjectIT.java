package com.example.db_course.integration.scenarios;

import com.example.db_course.EntityCreator;
import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.dto.request.ProjectCreateWithOwnerDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.TaskRepository;
import com.example.db_course.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectIT extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectMemberRepository projectMemberRepository;
    @Autowired private TaskRepository taskRepository;

    @Autowired private EntityManager entityManager;

    @Test
    @Transactional
    void createProject() throws Exception {
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Test Project");
        dto.setDescription("Test description");

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        List<ProjectEntity> projects = projectRepository.findAll();
        assertThat(projects).hasSize(1);

        ProjectEntity project = projects.get(0);
        assertThat(project.getName()).isEqualTo(dto.getName());
        assertThat(project.getDescription()).isEqualTo(dto.getDescription());
        assertThat(project.isDeleted()).isFalse();
    }

    @Test
    @Transactional
    void createProjectWithOwner() throws Exception {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectCreateWithOwnerDto dto = new ProjectCreateWithOwnerDto();
        dto.setName("Test Project");
        dto.setDescription("Test description");
        dto.setOwnerId(user.getId());

        mockMvc.perform(post("/projects/with-owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        List<ProjectEntity> projects = projectRepository.findAll();
        assertThat(projects).hasSize(1);

        ProjectEntity project = projects.get(0);

        Optional<ProjectMemberEntity> memberEntity =
                projectMemberRepository.findByUserAndProject(user, project);

        assertThat(memberEntity).isPresent();
        assertThat(project.getName()).isEqualTo(dto.getName());
        assertThat(project.getDescription()).isEqualTo(dto.getDescription());
        assertThat(project.isDeleted()).isFalse();
        assertThat(memberEntity.get().getRole()).isEqualTo(ProjectMemberRole.OWNER);
    }

    @Test
    @Transactional
    void softDeleteProject_marksDeletedAndFiltersFromFindById() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        Long id = project.getId();

        assertThat(projectRepository.findById(id)).isPresent();

        mockMvc.perform(delete("/projects/{id}", id))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        assertThat(projectRepository.findById(id)).isEmpty();
        assertThat(taskRepository.findById(task.getId())).isEmpty();

        Optional<ProjectEntity> rawProject = projectRepository.findRawById(id);
        assertThat(rawProject).isPresent();
        assertThat(rawProject.get().isDeleted()).isTrue();
        assertThat(rawProject.get().getDeletedAt()).isNotNull();

        Optional<TaskEntity> rawTask = taskRepository.findRawById(task.getId());
        assertThat(rawTask).isPresent();
        assertThat(rawTask.get().isDeleted()).isTrue();
        assertThat(rawTask.get().getDeletedAt()).isNotNull();
    }

    @Test
    @Transactional
    void hardDeleteProject_physicallyRemovesRow() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        ProjectMemberEntity projectMember = EntityCreator.getProjectMemberEntity(user, project);
        projectMemberRepository.save(projectMember);

        Long id = project.getId();

        assertThat(projectRepository.findRawById(id)).isPresent();

        mockMvc.perform(delete("/projects/{id}/hard", id))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        assertThat(projectRepository.findRawById(id)).isEmpty();
        assertThat(projectMemberRepository.findRawById(projectMember.getId())).isEmpty();
        assertThat(taskRepository.findRawById(task.getId())).isEmpty();
    }

    @Test
    @Transactional
    void hardDeleteProject_whenNotFound_returns4xx() throws Exception {
        Long nonExistingId = 999999L;

        mockMvc.perform(delete("/projects/{id}/hard", nonExistingId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    void getProjectFiltered_filtersByStatusAndExcludesSoftDeleted() throws Exception {
        ProjectEntity project1 = ProjectEntity.builder()
                .name("Test project 1")
                .description("desc1")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project1);

        ProjectEntity project2 = ProjectEntity.builder()
                .name("Test project 2")
                .description("desc2")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
        projectRepository.save(project2);

        ProjectEntity project3 = ProjectEntity.builder()
                .name("Test project 3")
                .description("desc3")
                .status(ProjectStatus.ARCHIVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project3);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/projects")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value(project1.getName()))
                .andExpect(jsonPath("$.content[0].description").value(project1.getDescription()))
                .andExpect(jsonPath("$.content[0].status").value(project1.getStatus().name()));
    }
}
