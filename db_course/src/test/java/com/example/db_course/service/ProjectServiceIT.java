package com.example.db_course.service;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.entity.enums.UserRole;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProjectServiceIT extends IntegrationTestBase {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Test
    @Transactional
    void createProjectWithOwner() {
        UserEntity user = UserEntity.builder()
                .email("user@test.com")
                .fullName("Test Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(user);

        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Test Project");
        dto.setDescription("Test description");
        dto.setOwnerUserId(user.getId());

        projectService.createProjectWithOwner(dto);

        List<ProjectEntity> projects = projectRepository.findAll();
        assertThat(projects).hasSize(1);
        ProjectEntity project = projects.get(0);
        assertThat(project.getName()).isEqualTo("Test Project");
        assertThat(project.isDeleted()).isFalse();

        List<ProjectMemberEntity> members = projectMemberRepository.findAll();
        assertThat(members).hasSize(1);
        ProjectMemberEntity member = members.get(0);
        assertThat(member.getProject().getId()).isEqualTo(project.getId());
        assertThat(member.getUser().getId()).isEqualTo(user.getId());
        assertThat(member.getRole()).isEqualTo(ProjectMemberRole.OWNER);
    }

    @Test
    @Transactional
    void createProjectWithOwner_whenOwnerMissing() {
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Bad Project");
        dto.setDescription("Should fail");
        dto.setOwnerUserId(999999L);

        assertThatThrownBy(() -> projectService.createProjectWithOwner(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Owner user not found");

        assertThat(projectRepository.count()).isZero();
        assertThat(projectMemberRepository.count()).isZero();
    }
}
