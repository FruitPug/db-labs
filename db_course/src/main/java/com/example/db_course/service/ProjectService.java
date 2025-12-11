package com.example.db_course.service;

import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.mapper.ProjectMapper;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.service.helper.SoftDeleteHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SoftDeleteHelper softDeleteHelper;

    @Transactional
    public ResponseEntity<Void> createProject(ProjectCreateDto projectCreateDto) {
        ProjectEntity project = ProjectMapper.fromCreateDto(projectCreateDto);
        projectRepository.save(project);
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