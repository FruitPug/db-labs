package com.example.db_course;

import com.example.db_course.entity.*;
import com.example.db_course.entity.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EntityCreator {

    public static ProjectEntity getProjectEntity() {
        return ProjectEntity.builder()
                .name("Test project")
                .description("Project description")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    public static UserEntity getUserEntity() {
        return UserEntity.builder()
                .email("user@test.com")
                .fullName("Test Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    public static ProjectMemberEntity getProjectMemberEntity(UserEntity user, ProjectEntity project){
        return ProjectMemberEntity.builder()
                .project(project)
                .user(user)
                .role(ProjectMemberRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public static TaskEntity getTaskEntity(UserEntity user, ProjectEntity project) {
        return TaskEntity.builder()
                .project(project)
                .title("Test task")
                .description("Task description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .creator(user)
                .assignee(user)
                .dueDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    public static TaskCommentEntity getTaskCommentEntity(UserEntity user, TaskEntity task) {
        return TaskCommentEntity.builder()
                .task(task)
                .author(user)
                .body("Task comment description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    public static TaskTagEntity getTaskTagEntity(TagEntity tag, TaskEntity task){
        return TaskTagEntity.builder()
                .task(task)
                .tag(tag)
                .build();
    }

    public static TagEntity getTagEntity() {
        return TagEntity.builder()
                .name("Test tag")
                .color("Red")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }
}
