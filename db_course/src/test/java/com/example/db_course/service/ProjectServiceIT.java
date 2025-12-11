package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class ProjectServiceIT extends IntegrationTestBase {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

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
    void softDeleteProject_marksDeletedAndFiltersFromFindById() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        Long id = project.getId();

        assertThat(projectRepository.findById(id)).isPresent();

        ResponseEntity<Void> response = projectService.softDeleteProject(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        entityManager.flush();
        entityManager.clear();

        assertThat(projectRepository.findById(id)).isEmpty();

        Optional<ProjectEntity> raw = projectRepository.findRawById(id);
        assertThat(raw).isPresent();
        assertThat(raw.get().isDeleted()).isTrue();
        assertThat(raw.get().getDeletedAt()).isNotNull();
    }
}
