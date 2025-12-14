package com.example.db_course.integration.repository;

import com.example.db_course.EntityCreator;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectRepositoryIT extends IntegrationTestBase {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Transactional
    void findById_returnsSavedProject() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        Optional<ProjectEntity> found = projectRepository.findById(project.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(project.getId());
    }

    @Test
    @Transactional
    void findRawById_returnsWithNativeQuery() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        Optional<ProjectEntity> found = projectRepository.findRawById(project.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(project.getId());
    }
}
