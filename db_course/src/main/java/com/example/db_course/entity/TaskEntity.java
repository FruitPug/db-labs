package com.example.db_course.entity;

import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tasks")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tasks_project"))
    private ProjectEntity project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tasks_creator"))
    private UserEntity creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id",
            foreignKey = @ForeignKey(name = "fk_tasks_assignee"))
    private UserEntity assignee;


    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "task_status_enum", nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "task_priority_enum", nullable = false)
    private TaskPriority priority;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Version
    @Column(nullable = false)
    private Long version;


    @OneToMany(mappedBy = "task")
    private List<TaskCommentEntity> comments;

    @OneToMany(mappedBy = "task")
    private Set<TaskTagEntity> taskTags;
}
