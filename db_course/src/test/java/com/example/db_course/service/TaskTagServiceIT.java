package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TaskTagCreateDto;
import com.example.db_course.entity.*;
import com.example.db_course.entity.enums.*;
import com.example.db_course.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TaskTagServiceIT extends IntegrationTestBase {

    @Autowired
    private TaskTagService taskTagService;

    @Autowired
    private TaskTagRepository taskTagRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Transactional
    void createTaskComment() {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(task.getId());
        dto.setTagId(tag.getId());

        taskTagService.createTaskTag(dto);

        List<TaskTagEntity> taskTags = taskTagRepository.findAll();
        assertThat(taskTags).hasSize(1);
        TaskTagEntity taskTag = taskTags.get(0);
        assertThat(taskTag.getTask().getId()).isEqualTo(task.getId());
        assertThat(taskTag.getTag().getId()).isEqualTo(tag.getId());
    }

    @Test
    @Transactional
    void createTaskTag_whenTaskIsMissing() {
        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(999999L);
        dto.setTagId(tag.getId());

        assertThatThrownBy(() -> taskTagService.createTaskTag(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task not found");
    }
}
