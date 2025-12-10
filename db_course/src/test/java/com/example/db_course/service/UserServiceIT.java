package com.example.db_course.service;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.UserRole;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class UserServiceIT extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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
}
