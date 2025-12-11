package com.example.db_course.entity;

import com.example.db_course.entity.interfaces.SoftDeletable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_comments")
@SQLRestriction("is_deleted = false")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentEntity implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_comments_task"))
    private TaskEntity task;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_comments_author"))
    private UserEntity author;

    @Column(name = "body", nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
