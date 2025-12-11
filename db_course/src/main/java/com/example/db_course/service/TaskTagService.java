package com.example.db_course.service;

import com.example.db_course.dto.request.TaskTagCreateDto;
import com.example.db_course.entity.TagEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.TaskTagEntity;
import com.example.db_course.mapper.TaskTagMapper;
import com.example.db_course.repository.TagRepository;
import com.example.db_course.repository.TaskRepository;
import com.example.db_course.repository.TaskTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskTagService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final TaskTagRepository taskTagRepository;

    @Transactional
    public ResponseEntity<Void> createTaskTag(TaskTagCreateDto dto) {

        TaskEntity task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        TagEntity tag = tagRepository.findById(dto.getTagId())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        TaskTagEntity taskTag = TaskTagMapper.createTaskTagEntity(task, tag);
        taskTagRepository.save(taskTag);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> hardDeleteProject(Long id) {
        int affected = taskTagRepository.hardDeleteById(id);

        if (affected == 0) {
            throw new IllegalArgumentException("Task tag not found");
        }

        return ResponseEntity.ok().build();
    }
}
