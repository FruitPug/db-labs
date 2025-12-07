package com.example.db_course.model;

import com.example.db_course.model.enums.ProjectMemberRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_project_members_project_user",
                columnNames = {"project_id", "user_id"}
        )
)
public class ProjectMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_members_project"))
    private ProjectEntity project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_members_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "project_member_role_enum", nullable = false)
    private ProjectMemberRole role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}
