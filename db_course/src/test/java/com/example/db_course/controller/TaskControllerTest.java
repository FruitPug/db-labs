package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.dto.request.TaskStatusUpdateDto;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import com.example.db_course.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_validRequest_returnsOkAndCallsService() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setProjectId(1L);
        dto.setCreatorUserId(2L);
        dto.setAssigneeUserId(3L);
        dto.setTitle("Task 1");
        dto.setDescription("Desc");
        dto.setStatus(TaskStatus.TODO);
        dto.setPriority(TaskPriority.MEDIUM);
        dto.setDueDate(LocalDate.now().plusDays(3));

        Mockito.when(taskService.createTask(any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(taskService).createTask(any(TaskCreateDto.class));
    }

    @Test
    void updateTaskStatus_validRequest_returnsOkAndCallsService() throws Exception {
        TaskStatusUpdateDto dto = new TaskStatusUpdateDto();
        dto.setTaskId(1L);
        dto.setStatus(TaskStatus.DONE);

        Mockito.when(taskService.updateTaskStatus(any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/tasks/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(taskService).updateTaskStatus(any(TaskStatusUpdateDto.class));
    }
}
