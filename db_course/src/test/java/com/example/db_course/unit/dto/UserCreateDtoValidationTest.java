package com.example.db_course.unit.dto;

import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.entity.enums.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenEmailOrFullNameBlank_validationFails() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("   "); // invalid
        dto.setFullName("");
        dto.setRole(UserRole.DEVELOPER);

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllFieldsValid_validationPasses() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("user@test.com");
        dto.setFullName("Test User");
        dto.setRole(UserRole.DEVELOPER);

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
