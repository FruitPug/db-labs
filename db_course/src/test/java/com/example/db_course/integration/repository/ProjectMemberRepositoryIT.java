package com.example.db_course.integration.repository;

import com.example.db_course.EntityCreator;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        ProjectMemberEntity projectMember = EntityCreator.getProjectMemberEntity(user, project);
        projectMemberRepository.save(projectMember);

        Optional<ProjectMemberEntity> found = projectMemberRepository.findByUserAndProject(user, project);

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(ProjectMemberRole.OWNER);
    }

    @Test
    @Transactional
    void findRawById_returnsWithNativeQuery() {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        ProjectMemberEntity pm = EntityCreator.getProjectMemberEntity(user, project);
        projectMemberRepository.save(pm);

        Optional<ProjectMemberEntity> found = projectMemberRepository.findRawById(pm.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(pm.getId());
    }

    @Test
    @Transactional
    void existsByProject_IdAndUser_Id_returnsTrue() {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        ProjectMemberEntity pm = EntityCreator.getProjectMemberEntity(user, project);
        projectMemberRepository.save(pm);

        boolean exists = projectMemberRepository.existsByProject_IdAndUser_Id(project.getId(), user.getId());
        assertThat(exists).isTrue();

        boolean notExists = projectMemberRepository.existsByProject_IdAndUser_Id(project.getId(), user.getId() + 1);
        assertThat(notExists).isFalse();
    }
}
