package com.example.db_course.unit.dto;

import com.example.db_course.dto.request.ProjectCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenNameIsBlank_validationFails() {
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("");
        dto.setDescription("Desc");

        Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllFieldsValid_validationPasses() {
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Valid");
        dto.setDescription("Desc");

        Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
