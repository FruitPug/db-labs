package com.example.db_course.repository;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
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
}
