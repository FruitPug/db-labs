package com.example.db_course.unit.mapper;

import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.mapper.ProjectMemberMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMemberMapperTest {

    @Test
    void createProjectMemberEntity_setsAllFields() {
        ProjectEntity project = ProjectEntity.builder()
                .id(1L).build();

        UserEntity user = UserEntity.builder()
                .id(2L).build();

        ProjectMemberRole role = ProjectMemberRole.CONTRIBUTOR;

        ProjectMemberEntity member = ProjectMemberMapper.createProjectMemberEntity(
                project,
                user,
                role
        );

        assertThat(member.getProject()).isSameAs(project);
        assertThat(member.getUser()).isSameAs(user);
        assertThat(member.getRole()).isEqualTo(role);
        assertThat(member.getJoinedAt()).isNotNull();
    }
}
