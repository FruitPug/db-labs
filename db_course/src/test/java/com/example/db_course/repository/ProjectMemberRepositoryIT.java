package com.example.db_course.repository;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.entity.enums.UserRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMemberRepositoryIT extends IntegrationTestBase {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Transactional
    void findByProjectAndUser_returnsSavedRole() {
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
                .name("Test project")
                .description("Project description")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        projectRepository.save(project);

        ProjectMemberEntity projectMember = ProjectMemberEntity.builder()
                .project(project)
                .user(user)
                .role(ProjectMemberRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();

        projectMemberRepository.save(projectMember);

        Optional<ProjectMemberEntity> found = projectMemberRepository.findByUserAndProject(user, project);

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(ProjectMemberRole.OWNER);
    }
}
