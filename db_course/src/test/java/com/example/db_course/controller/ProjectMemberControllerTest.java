package com.example.db_course.controller;

import com.example.db_course.dto.request.ProjectMemberCreateDto;
import com.example.db_course.entity.enums.ProjectMemberRole;
import com.example.db_course.service.ProjectMemberService;
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

@WebMvcTest(ProjectMemberController.class)
class ProjectMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectMemberService projectMemberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProjectMember_validRequest_returnsOkAndCallsService() throws Exception {
        ProjectMemberCreateDto dto = new ProjectMemberCreateDto();
        dto.setProjectId(1L);
        dto.setUserId(2L);
        dto.setRole(ProjectMemberRole.CONTRIBUTOR);

        Mockito.when(projectMemberService.createProjectMember(any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/project-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(projectMemberService).createProjectMember(any(ProjectMemberCreateDto.class));
    }
}
