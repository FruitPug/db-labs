package com.example.db_course.models;

import jakarta.persistence.*;

@Entity
@Table(
        name = "task_tags",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_task_tags_task_tag",
                columnNames = {"task_id", "tag_id"}
        )
)
public class TaskTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_tags_task"))
    private Task task;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_tags_tag"))
    private Tag tag;
}
