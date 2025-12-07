package com.example.db_course.repository;

import com.example.db_course.model.ProjectMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, Long> {
}
