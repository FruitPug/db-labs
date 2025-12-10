package com.example.db_course.repository;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.enums.ProjectStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectRepositoryIT extends IntegrationTestBase {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Transactional
    void findById_returnsSavedProject() {
        ProjectEntity project = ProjectEntity.builder()
                .name("Test project")
                .description("Project description")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        projectRepository.save(project);

        Optional<ProjectEntity> found = projectRepository.findById(project.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(project.getId());
    }
}
