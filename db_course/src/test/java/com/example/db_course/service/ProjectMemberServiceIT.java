package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.ProjectMemberCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ProjectMemberServiceIT extends IntegrationTestBase {

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void createProjectMember() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectMemberCreateDto dto = new ProjectMemberCreateDto();
        dto.setProjectId(project.getId());
        dto.setUserId(user.getId());
        dto.setRole(ProjectMemberRole.CONTRIBUTOR);

        projectMemberService.createProjectMember(dto);

        List<ProjectMemberEntity> members = projectMemberRepository.findAll();
        assertThat(members).hasSize(1);
        ProjectMemberEntity member = members.get(0);
        assertThat(member.getProject().getId()).isEqualTo(project.getId());
        assertThat(member.getUser().getId()).isEqualTo(user.getId());
        assertThat(member.getRole()).isEqualTo(ProjectMemberRole.CONTRIBUTOR);
    }

    @Test
    @Transactional
    void createProjectMember_whenProjectMissing() {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectMemberCreateDto dto = new ProjectMemberCreateDto();
        dto.setProjectId(999999L);
        dto.setUserId(user.getId());
        dto.setRole(ProjectMemberRole.CONTRIBUTOR);

        assertThatThrownBy(() -> projectMemberService.createProjectMember(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    @Transactional
    void hardDeleteProjectMember_physicallyRemovesRow() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectMemberEntity member = EntityCreator.getProjectMemberEntity(user, project);
        projectMemberRepository.save(member);

        Long id = member.getId();

        assertThat(projectMemberRepository.findRawById(id)).isPresent();

        ResponseEntity<Void> response = projectMemberService.hardDeleteProject(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        entityManager.flush();
        entityManager.clear();

        assertThat(projectRepository.findRawById(id)).isEmpty();
    }

    @Test
    @Transactional
    void hardDeleteProject_whenNotFound_throwsException() {
        Long nonExistingId = 999999L;

        assertThatThrownBy(() -> projectMemberService.hardDeleteProject(nonExistingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Project member not found");
    }
}
