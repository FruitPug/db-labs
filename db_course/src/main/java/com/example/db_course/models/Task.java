package com.example.db_course.models;

import com.example.db_course.models.enums.TaskPriority;
import com.example.db_course.models.enums.TaskStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tasks_project"))
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tasks_creator"))
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id",
            foreignKey = @ForeignKey(name = "fk_tasks_assignee"))
    private User assignee;


    @Column(nullable = false, length = 255)
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


    @OneToMany(mappedBy = "task")
    private List<TaskComment> comments;

    @OneToMany(mappedBy = "task")
    private Set<TaskTag> taskTags;
}
