package com.example.db_course.service;

import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.dto.request.ProjectCreateWithOwnerDto;
import com.example.db_course.dto.request.ProjectStatusUpdateDto;
import com.example.db_course.dto.response.ProjectResponseDto;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.mapper.ProjectMapper;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.mapper.ProjectMemberMapper;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.UserRepository;
import com.example.db_course.service.helper.SoftDeleteHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SoftDeleteHelper softDeleteHelper;

    @Transactional
    public ResponseEntity<Void> createProject(ProjectCreateDto projectCreateDto) {
        ProjectEntity project = ProjectMapper.fromCreateDto(projectCreateDto);
        projectRepository.save(project);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> createProjectWithOwner(ProjectCreateWithOwnerDto projectCreateWithOwnerDto) {

        UserEntity owner = userRepository.findById(projectCreateWithOwnerDto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));

        ProjectEntity project = ProjectMapper.fromCreateDto(projectCreateWithOwnerDto);

        ProjectEntity savedProject = projectRepository.save(project);

        ProjectMemberEntity member = ProjectMemberMapper.createProjectMemberEntity(
                savedProject,
                owner,
                ProjectMemberRole.OWNER
        );

        projectMemberRepository.save(member);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> softDeleteProject(Long id) {
        return softDeleteHelper.softDelete(
                id,
                projectRepository::findById,
                projectRepository::save,
                () -> new IllegalArgumentException("Project not found")
        );
    }

    @Transactional
    public ResponseEntity<Void> hardDeleteProject(Long id) {
        int affected = projectRepository.hardDeleteById(id);

        if (affected == 0) {
            throw new IllegalArgumentException("Project not found");
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> updateProjectStatus(ProjectStatusUpdateDto dto) {
        ProjectEntity project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        project.setStatus(dto.getStatus());
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Page<ProjectResponseDto>> getProjectsFiltered(
            ProjectStatus status,
            Pageable pageable
    ) {
        Page<ProjectEntity> page = projectRepository.searchProjectsFiltered(status, pageable);

        Page<ProjectResponseDto> dtoPage = page.map(ProjectMapper::toResponseDto);

        return ResponseEntity.ok(dtoPage);
    }
}