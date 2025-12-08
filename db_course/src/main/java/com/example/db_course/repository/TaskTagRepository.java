package com.example.db_course.repository;

import com.example.db_course.entity.TaskTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTagEntity, Long> {
}
