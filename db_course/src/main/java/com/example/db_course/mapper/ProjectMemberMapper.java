package com.example.db_course.mapper;

import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;

import java.time.LocalDateTime;

public class ProjectMemberMapper {

    public static ProjectMemberEntity createProjectMemberEntity(
            ProjectEntity project,
            UserEntity user,
            ProjectMemberRole role
    ){
        ProjectMemberEntity member = new ProjectMemberEntity();
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());

        return member;
    }
}
