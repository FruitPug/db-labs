package com.example.db_course.service;

import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.dto.request.TaskStatusUpdateDto;
import com.example.db_course.dto.response.TaskResponseDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import com.example.db_course.mapper.TaskMapper;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.TaskRepository;
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
public class TaskService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SoftDeleteHelper softDeleteHelper;

    @Transactional
    public ResponseEntity<Void> createTask(TaskCreateDto dto) {

        ProjectEntity project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        UserEntity creator = userRepository.findById(dto.getCreatorUserId())
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        UserEntity assignee = null;
        if (dto.getAssigneeUserId() != null) {
            assignee = userRepository.findById(dto.getAssigneeUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee user not found"));
        }

        TaskEntity task = TaskMapper.createTaskEntity(project, creator, assignee, dto);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> updateTaskStatus(TaskStatusUpdateDto dto) {
        TaskEntity task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (task.getStatus() == TaskStatus.DONE && dto.getStatus() == TaskStatus.TODO) {
            throw new IllegalStateException("Cannot move task from DONE back to TODO");
        }

        task.setStatus(dto.getStatus());
        task.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> softDeleteTask(Long id) {
        return softDeleteHelper.softDelete(
                id,
                taskRepository::findById,
                taskRepository::save,
                () -> new IllegalArgumentException("Task not found")
        );
    }

    @Transactional
    public ResponseEntity<Page<TaskResponseDto>> getTasksFiltered(
            TaskStatus status,
            TaskPriority priority,
            Long projectId,
            Long assigneeId,
            Pageable pageable
    ) {
        Page<TaskEntity> page = taskRepository.searchTasksFiltered(status, priority, projectId, assigneeId, pageable);

        Page<TaskResponseDto> dtoPage = page.map(TaskMapper::toResponseDto);

        return ResponseEntity.ok(dtoPage);
    }
}
