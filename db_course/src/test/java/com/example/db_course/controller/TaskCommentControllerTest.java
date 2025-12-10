package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.service.TaskCommentService;
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

@WebMvcTest(TaskCommentController.class)
class TaskCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskCommentService taskCommentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createComment_validRequest_returnsOkAndCallsService() throws Exception {
        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(1L);
        dto.setAuthorUserId(2L);
        dto.setBody("Nice work");

        Mockito.when(taskCommentService.createComment(any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/task-comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(taskCommentService).createComment(any(TaskCommentCreateDto.class));
    }
}
