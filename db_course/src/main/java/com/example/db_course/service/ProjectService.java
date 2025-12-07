package com.example.db_course.service;

import com.example.db_course.model.ProjectEntity;
import com.example.db_course.model.ProjectMemberEntity;
import com.example.db_course.model.UserEntity;
import com.example.db_course.model.enums.ProjectMemberRole;
import com.example.db_course.model.enums.ProjectStatus;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional
    public ResponseEntity<ProjectEntity> createProjectWithOwner(
            String projectName,
            String description,
            Long ownerUserId
    ) {

        UserEntity owner = userRepository.findById(ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));

        ProjectEntity project = new ProjectEntity();
        project.setName(projectName);
        project.setDescription(description);
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setDeleted(false);

        ProjectEntity savedProject = projectRepository.save(project);

        ProjectMemberEntity member = new ProjectMemberEntity();
        member.setProject(savedProject);
        member.setUser(owner);
        member.setRole(ProjectMemberRole.OWNER);
        member.setJoinedAt(LocalDateTime.now());

        projectMemberRepository.save(member);

        return ResponseEntity.ok(savedProject);
    }
}