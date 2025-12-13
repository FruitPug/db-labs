package com.example.db_course.repository;

import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query(value = "select * from tasks where id = :id", nativeQuery = true)
    Optional<TaskEntity> findRawById(Long id);

    @Query("""
        select t from TaskEntity t
        where (:projectId is null or t.project.id = :projectId)
            and (:priority is null or t.priority = :priority)
            and (:status is null or t.status = :status)
            and (:assigneeId is null or t.assignee.id = :assigneeId)
    """)
    Page<TaskEntity> searchTasksFiltered(
            TaskStatus status,
            TaskPriority priority,
            Long projectId,
            Long assigneeId,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
        update tasks
        set is_deleted = true,
            deleted_at = :now,
            updated_at = :now
        where project_id = :projectId
          and is_deleted = false
        """, nativeQuery = true)
    void softDeleteByProjectId(
            Long projectId,
            LocalDateTime now
    );
}
