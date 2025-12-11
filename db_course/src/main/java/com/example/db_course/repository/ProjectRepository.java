package com.example.db_course.repository;

import com.example.db_course.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query(value = "select * from projects where id = :id", nativeQuery = true)
    Optional<ProjectEntity> findRawById(Long id);

    @Modifying
    @Query(value = "delete from projects where id = :id", nativeQuery = true)
    int hardDeleteById(Long id);
}
