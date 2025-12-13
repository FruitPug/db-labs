package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.dto.response.UserResponseDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.UserRole;
import com.example.db_course.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class UserServiceIT extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void createUser() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("user@test.com");
        dto.setFullName("Test Tester");
        dto.setRole(UserRole.DEVELOPER);

        userService.createUser(dto);
        List<UserEntity> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        UserEntity user = users.get(0);
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @Transactional
    void softDeleteUser_marksDeletedAndFiltersFromFindById() {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        Long id = user.getId();

        assertThat(userRepository.findById(id)).isPresent();

        ResponseEntity<Void> response = userService.softDeleteUser(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

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
    void getUsersFiltered_filtersByRoleAndExcludesSoftDeleted() {
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

        PageRequest pageable = PageRequest.of(0, 10);
        Page<UserResponseDto> page = userService.getUsersFiltered(UserRole.DEVELOPER, pageable).getBody();

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
        UserResponseDto dto = page.getContent().get(0);
        assertThat(dto.getFullName()).isEqualTo(user1.getFullName());
        assertThat(dto.getRole()).isEqualTo(UserRole.DEVELOPER);
    }
}
