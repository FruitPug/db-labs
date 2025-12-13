package com.example.db_course.repository;

import com.example.db_course.entity.TagEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.TaskTagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTagEntity, Long> {

    Optional<TaskTagEntity> findByTaskAndTag(TaskEntity task, TagEntity tag);

    @Query(value = "select * from task_tags where id = :id", nativeQuery = true)
    Optional<TaskTagEntity> findRawById(Long id);

    @Modifying
    @Query(value = "delete from task_tags where id = :id", nativeQuery = true)
    int hardDeleteById(Long id);

    @Query("""
        select tt from TaskTagEntity tt
        where (:taskId is null or tt.task.id = :taskId)
            and (:tagId is null or tt.tag.id = :tagId)
    """)
    Page<TaskTagEntity> searchTaskTagsFiltered(Long taskId, Long tagId, Pageable pageable);
}
