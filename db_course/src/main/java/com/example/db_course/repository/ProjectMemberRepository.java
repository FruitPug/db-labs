package com.example.db_course.repository;

import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, Long> {

    Optional<ProjectMemberEntity> findByUserAndProject(UserEntity user, ProjectEntity project);

    Optional<ProjectMemberEntity> findByProjectAndRole(ProjectEntity project, ProjectMemberRole role);

    @Query(value = "select * from project_members where id = :id", nativeQuery = true)
    Optional<ProjectMemberEntity> findRawById(Long id);

    @Modifying
    @Query(value = "delete from project_members where id = :id", nativeQuery = true)
    int hardDeleteById(Long id);

    boolean existsByProject_IdAndUser_Id(Long projectId, Long userId);

    @Query("""
        select pm from ProjectMemberEntity pm
        where (:projectId is null or pm.project.id = :projectId)
            and (:userId is null or pm.user.id = :userId)
            and (:role is null or pm.role = :role)
    """)
    Page<ProjectMemberEntity> searchProjectMembersFiltered(
            Long projectId,
            Long userId,
            ProjectMemberRole role,
            Pageable pageable
    );
}
