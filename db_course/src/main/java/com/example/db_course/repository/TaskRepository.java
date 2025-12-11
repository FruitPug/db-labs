package com.example.db_course.repository;

import com.example.db_course.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query(value = "select * from tasks where id = :id", nativeQuery = true)
    Optional<TaskEntity> findRawById(Long aLong);
}
