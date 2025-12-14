package com.example.db_course.unit.dto;

import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenRequiredFieldsMissing_validationFails() {
        TaskCreateDto dto = new TaskCreateDto();
        // everything is null/empty

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllRequiredFieldsValid_validationPasses() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setProjectId(1L);
        dto.setCreatorUserId(2L);
        dto.setTitle("Implement feature");
        dto.setStatus(TaskStatus.TODO);
        dto.setPriority(TaskPriority.MEDIUM);
        // assignee and dueDate are optional

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
