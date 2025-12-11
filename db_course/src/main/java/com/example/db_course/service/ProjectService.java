package com.example.db_course.service;

import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.mapper.ProjectMapper;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.mapper.ProjectMemberMapper;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.UserRepository;
import com.example.db_course.service.helper.SoftDeleteHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SoftDeleteHelper softDeleteHelper;

    @Transactional
    public ResponseEntity<Void> createProjectWithOwner(ProjectCreateDto projectCreateDto) {

        UserEntity owner = userRepository.findById(projectCreateDto.getOwnerUserId())
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));

        ProjectEntity project = ProjectMapper.fromCreateDto(projectCreateDto);

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
}