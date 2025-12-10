package com.example.db_course.repository;

import com.example.db_course.entity.TaskCommentEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {
    Optional<TaskCommentEntity> findByAuthorAndTask(UserEntity author, TaskEntity task);
}
