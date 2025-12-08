package com.example.db_course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "task_tags",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_task_tags_task_tag",
                columnNames = {"task_id", "tag_id"}
        )
)

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_tags_task"))
    private TaskEntity task;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_tags_tag"))
    private TagEntity tag;
}
