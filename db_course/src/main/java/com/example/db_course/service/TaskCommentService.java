package com.example.db_course.service;

import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.entity.TaskCommentEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.mapper.TaskCommentMapper;
import com.example.db_course.repository.TaskCommentRepository;
import com.example.db_course.repository.TaskRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskCommentService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskCommentRepository taskCommentRepository;

    @Transactional
    public ResponseEntity<Void> createComment(TaskCommentCreateDto dto) {
        TaskEntity task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        UserEntity author = userRepository.findById(dto.getAuthorUserId())
                .orElseThrow(() -> new IllegalArgumentException("Author user not found"));

        TaskCommentEntity comment = TaskCommentMapper.createTaskCommentEntity(task, author, dto);
        taskCommentRepository.save(comment);

        return ResponseEntity.ok().build();
    }
}
