package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskTagCreateDto;
import com.example.db_course.service.TaskTagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskTagController.class)
class TaskTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskTagService taskTagService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTaskTag_validRequest_returnsOkAndCallsService() throws Exception {
        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(1L);
        dto.setTagId(2L);

        Mockito.when(taskTagService.createTaskTag(any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/task-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(taskTagService).createTaskTag(any(TaskTagCreateDto.class));
    }
}
