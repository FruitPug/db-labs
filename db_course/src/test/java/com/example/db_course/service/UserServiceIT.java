package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.UserRole;
import com.example.db_course.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

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
}
