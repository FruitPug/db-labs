package com.example.db_course.integration.scenarios;

import com.example.db_course.EntityCreator;
import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.UserRole;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIT extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;

    @Test
    @Transactional
    void createUser() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("user@test.com");
        dto.setFullName("Test Tester");
        dto.setRole(UserRole.DEVELOPER);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.findAll()).hasSize(1);
        UserEntity user = userRepository.findAll().get(0);
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getFullName()).isEqualTo(dto.getFullName());
        assertThat(user.getRole()).isEqualTo(dto.getRole());
    }

    @Test
    @Transactional
    void softDeleteUser_marksDeletedAndFiltersFromFindById() throws Exception {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        Long id = user.getId();

        assertThat(userRepository.findById(id)).isPresent();

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.findById(id)).isEmpty();

        Optional<UserEntity> raw = userRepository.findRawById(id);
        assertThat(raw).isPresent();
        assertThat(raw.get().isDeleted()).isTrue();
        assertThat(raw.get().getDeletedAt()).isNotNull();
    }

    @Test
    @Transactional
    void getUsersFiltered_filtersByRoleAndExcludesSoftDeleted() throws Exception {
        UserEntity user1 = UserEntity.builder()
                .email("user1@test.com")
                .fullName("Test 1 Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(user1);

        UserEntity user2 = UserEntity.builder()
                .email("user2@test.com")
                .fullName("Test 2 Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
        userRepository.save(user2);

        UserEntity user3 = UserEntity.builder()
                .email("user3@test.com")
                .fullName("Test 3 Tester")
                .role(UserRole.MANAGER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(user3);

        mockMvc.perform(get("/users")
                        .param("role", "DEVELOPER")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value(user1.getFullName()))
                .andExpect(jsonPath("$.content[0].role").value(UserRole.DEVELOPER.name()));
    }
}
