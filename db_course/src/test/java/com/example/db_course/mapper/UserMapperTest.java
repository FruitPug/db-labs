package com.example.db_course.mapper;

import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void fromCreateDto_mapsFieldsAndSetsDefaults() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("user@test.com");
        dto.setFullName("Test User");
        dto.setRole(UserRole.MANAGER);

        UserEntity entity = UserMapper.fromCreateDto(dto);

        assertThat(entity.getEmail()).isEqualTo("user@test.com");
        assertThat(entity.getFullName()).isEqualTo("Test User");
        assertThat(entity.getRole()).isEqualTo(UserRole.MANAGER);
        assertThat(entity.isDeleted()).isFalse();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }
}
