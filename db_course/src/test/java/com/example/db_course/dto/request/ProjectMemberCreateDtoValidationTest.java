package com.example.db_course.dto.request;

import com.example.db_course.entity.enums.ProjectMemberRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMemberCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenRequiredFieldsMissing_validationFails() {
        ProjectMemberCreateDto dto = new ProjectMemberCreateDto();
        // all null -> invalid

        Set<ConstraintViolation<ProjectMemberCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllFieldsValid_validationPasses() {
        ProjectMemberCreateDto dto = new ProjectMemberCreateDto();
        dto.setProjectId(1L);
        dto.setUserId(2L);
        dto.setRole(ProjectMemberRole.CONTRIBUTOR);

        Set<ConstraintViolation<ProjectMemberCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
