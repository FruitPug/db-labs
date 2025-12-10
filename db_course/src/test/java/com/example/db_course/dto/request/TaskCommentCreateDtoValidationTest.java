package com.example.db_course.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskCommentCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenRequiredFieldsMissing_validationFails() {
        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        // all null / blank

        Set<ConstraintViolation<TaskCommentCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllFieldsValid_validationPasses() {
        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(1L);
        dto.setAuthorUserId(2L);
        dto.setBody("Looks good");

        Set<ConstraintViolation<TaskCommentCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
