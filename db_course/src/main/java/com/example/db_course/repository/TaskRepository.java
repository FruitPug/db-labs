package com.example.db_course.repository;

import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query(value = "select * from tasks where id = :id", nativeQuery = true)
    Optional<TaskEntity> findRawById(Long id);

    @Query("""
        select t from TaskEntity t
        where (t.status = :status)
            and (t.priority = :priority)
            and (t.project.id = :projectId)
            and (:assigneeId is null or t.assignee.id = :assigneeId)
    """)
    Page<TaskEntity> searchTasksFiltered(
            TaskStatus status,
            TaskPriority priority,
            Long projectId,
            Long assigneeId,
            Pageable pageable
    );


}
