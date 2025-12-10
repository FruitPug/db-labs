package com.example.db_course.repository;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.UserRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryIT extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void findById_returnsSavedUser() {
        UserEntity user = UserEntity.builder()
                .email("user@test.com")
                .fullName("Test Tester")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        userRepository.save(user);

        Optional<UserEntity> found = userRepository.findById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(user.getId());
    }
}
