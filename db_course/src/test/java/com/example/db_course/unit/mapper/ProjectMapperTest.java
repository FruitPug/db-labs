package com.example.db_course.unit.mapper;

import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.enums.ProjectStatus;
import com.example.db_course.mapper.ProjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMapperTest {

    @Test
    void fromCreateDto_mapsFieldsCorrectlyAndSetsDefaults() {
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("Project name");
        dto.setDescription("Description");

        ProjectEntity entity = ProjectMapper.fromCreateDto(dto);

        assertThat(entity.getName()).isEqualTo("Project name");
        assertThat(entity.getDescription()).isEqualTo("Description");
        assertThat(entity.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(entity.isDeleted()).isFalse();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }
}
