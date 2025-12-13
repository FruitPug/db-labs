package com.example.db_course.repository;

import com.example.db_course.entity.TaskCommentEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {

    Optional<TaskCommentEntity> findByAuthorAndTask(UserEntity author, TaskEntity task);

    @Query(value = "select * from task_comments where id = :id", nativeQuery = true)
    Optional<TaskCommentEntity> findRawById(Long id);

    @Query("""
        select tc from TaskCommentEntity tc
        where (:taskId is null or tc.task.id = :taskId)
            and (:userId is null or tc.author.id = :userId)
    """)
    Page<TaskCommentEntity> searchCommentsFiltered(
            Long taskId,
            Long userId,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
        update task_comments
        set is_deleted = true,
            deleted_at = :now,
            updated_at = :now
        where task_id = :taskId
          and is_deleted = false
        """, nativeQuery = true)
    void softDeleteByTaskId(
            Long taskId,
            LocalDateTime now
    );
}
