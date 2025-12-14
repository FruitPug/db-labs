package com.example.db_course.unit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TagCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenNameBlank_validationFails() {
        TagCreateDto dto = new TagCreateDto();
        dto.setName("   "); // invalid
        dto.setColor("red");

        Set<ConstraintViolation<TagCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenAllFieldsValid_validationPasses() {
        TagCreateDto dto = new TagCreateDto();
        dto.setName("backend");
        dto.setColor("red");

        Set<ConstraintViolation<TagCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
