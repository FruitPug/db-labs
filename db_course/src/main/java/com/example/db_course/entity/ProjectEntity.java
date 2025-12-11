package com.example.db_course.entity;

import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.entity.interfaces.SoftDeletable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projects")
@SQLRestriction("is_deleted = false")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "project_status_enum", nullable = false)
    private ProjectStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @OneToMany(mappedBy = "project")
    private Set<ProjectMemberEntity> members;

    @OneToMany(mappedBy = "project")
    private List<TaskEntity> tasks;
}
