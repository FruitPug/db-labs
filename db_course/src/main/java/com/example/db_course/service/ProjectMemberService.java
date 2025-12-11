package com.example.db_course.service;

import com.example.db_course.dto.request.ProjectMemberCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.ProjectMemberEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.mapper.ProjectMemberMapper;
import com.example.db_course.repository.ProjectMemberRepository;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ResponseEntity<Void> createProjectMember(ProjectMemberCreateDto dto) {
        ProjectEntity project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ProjectMemberEntity member = ProjectMemberMapper.createProjectMemberEntity(
                project,
                user,
                dto.getRole()
        );

        projectMemberRepository.save(member);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> hardDeleteProject(Long id) {
        int affected = projectMemberRepository.hardDeleteById(id);

        if (affected == 0) {
            throw new IllegalArgumentException("Project member not found");
        }

        return ResponseEntity.ok().build();
    }
}
