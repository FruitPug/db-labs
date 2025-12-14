package com.example.db_course.unit.dto;

import com.example.db_course.dto.request.TaskTagCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTagCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenRequiredFieldsMissing_validationFails() {
        TaskTagCreateDto dto = new TaskTagCreateDto();
        // both null

        Set<ConstraintViolation<TaskTagCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllFieldsValid_validationPasses() {
        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(1L);
        dto.setTagId(2L);

        Set<ConstraintViolation<TaskTagCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
