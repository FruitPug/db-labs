package com.example.db_course.unit.dto;

import com.example.db_course.entity.enums.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStatusUpdateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenRequiredFieldsMissing_validationFails() {
        TaskStatusUpdateDto dto = new TaskStatusUpdateDto();
        // everything is null/empty

        Set<ConstraintViolation<TaskStatusUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllRequiredFieldsValid_validationPasses() {
        TaskStatusUpdateDto dto = new TaskStatusUpdateDto();
        dto.setTaskId(1L);
        dto.setStatus(TaskStatus.DONE);

        Set<ConstraintViolation<TaskStatusUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
