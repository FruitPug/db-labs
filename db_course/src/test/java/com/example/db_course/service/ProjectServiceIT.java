package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.dto.request.ProjectCreateWithOwnerDto;
import com.example.db_course.dto.response.ProjectResponseDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.repository.ProjectMemberRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class ProjectServiceIT extends IntegrationTestBase {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void createProject() {
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Test Project");
        dto.setDescription("Test description");

        projectService.createProject(dto);

        List<ProjectEntity> projects = projectRepository.findAll();
        assertThat(projects).hasSize(1);
        ProjectEntity project = projects.get(0);
        assertThat(project.getName()).isEqualTo("Test Project");
        assertThat(project.isDeleted()).isFalse();
    }

    @Test
    @Transactional
    void createProjectWithOwner() {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectCreateWithOwnerDto dto = new ProjectCreateWithOwnerDto();
        dto.setName("Test Project");
        dto.setDescription("Test description");
        dto.setOwnerId(user.getId());

        projectService.createProjectWithOwner(dto);

        List<ProjectEntity> projects = projectRepository.findAll();
        assertThat(projects).hasSize(1);
        ProjectEntity project = projects.get(0);
        Optional<ProjectMemberEntity> memberEntity = projectMemberRepository.findByUserAndProject(user, project);

        assertThat(memberEntity).isPresent();
        assertThat(project.getName()).isEqualTo(dto.getName());
        assertThat(project.getDescription()).isEqualTo(dto.getDescription());
        assertThat(project.isDeleted()).isFalse();
        assertThat(memberEntity.get().getRole()).isEqualTo(ProjectMemberRole.OWNER);
    }

    @Test
    @Transactional
    void softDeleteProject_marksDeletedAndFiltersFromFindById() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        Long id = project.getId();

        assertThat(projectRepository.findById(id)).isPresent();

        ResponseEntity<Void> response = projectService.softDeleteProject(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

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
    void hardDeleteProject_physicallyRemovesRow() {
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

        ResponseEntity<Void> response = projectService.hardDeleteProject(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        entityManager.flush();
        entityManager.clear();

        assertThat(projectRepository.findRawById(id)).isEmpty();
        assertThat(projectMemberRepository.findRawById(projectMember.getId()).isEmpty());
        assertThat(taskRepository.findRawById(task.getId()).isEmpty());
    }

    @Test
    @Transactional
    void hardDeleteProject_whenNotFound_throwsException() {
        Long nonExistingId = 999999L;

        assertThatThrownBy(() -> projectService.hardDeleteProject(nonExistingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    @Transactional
    void getProjectFiltered_filtersByStatusAndExcludesSoftDeleted() {
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

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ProjectResponseDto> page = projectService
                .getProjectsFiltered(ProjectStatus.ACTIVE, pageable)
                .getBody();

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
        ProjectResponseDto dto = page.getContent().get(0);
        assertThat(dto.getName()).isEqualTo(project1.getName());
        assertThat(dto.getDescription()).isEqualTo(project1.getDescription());
        assertThat(dto.getStatus()).isEqualTo(project1.getStatus());
    }
}
