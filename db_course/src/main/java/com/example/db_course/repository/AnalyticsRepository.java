package com.example.db_course.repository;

import com.example.db_course.dto.response.ProjectTaskStatusStatsDto;
import com.example.db_course.dto.response.UserDoneTasksStatsDto;
import com.example.db_course.entity.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public Page<ProjectTaskStatusStatsDto> tasksByProjectAndStatus(Pageable pageable) {
        String dataSql = """
                select
                    p.id as project_id,
                    t.status as status,
                    count(*) as task_count
                from projects p
                join tasks t on t.project_id = p.id
                where p.is_deleted = false
                  and t.is_deleted = false
                group by p.id, t.status
                order by p.id, t.status
                limit ? offset ?
                """;

        String countSql = """
                select count(*) from (
                    select p.id, t.status
                    from projects p
                    join tasks t on t.project_id = p.id
                    where p.is_deleted = false
                      and t.is_deleted = false
                    group by p.id, t.status
                ) as x
                """;

        var list = jdbcTemplate.query(
                dataSql,
                ps -> {
                    ps.setInt(1, pageable.getPageSize());
                    ps.setLong(2, pageable.getOffset());
                },
                (rs, rowNum) -> ProjectTaskStatusStatsDto.builder()
                        .projectId(rs.getLong("project_id"))
                        .status(TaskStatus.valueOf(rs.getString("status")))
                        .taskCount(rs.getLong("task_count"))
                        .build()
        );

        long total = queryForLong(countSql);

        return new PageImpl<>(list, pageable, total);
    }

    public Page<UserDoneTasksStatsDto> topAssigneesByDoneTasks(Pageable pageable) {
        String dataSql = """
                select
                    u.id as user_id,
                    u.email as email,
                    count(t.id) as done_count,
                    dense_rank() over (order by count(t.id) desc) as rank
                from users u
                join tasks t on t.assignee_id = u.id
                join projects p on p.id = t.project_id
                where u.is_deleted = false
                  and p.is_deleted = false
                  and t.is_deleted = false
                  and t.status = 'DONE'
                group by u.id, u.email
                order by done_count desc, u.id
                limit ? offset ?
                """;

        String countSql = """
                select count(*) from (
                    select u.id
                    from users u
                    join tasks t on t.assignee_id = u.id
                    join projects p on p.id = t.project_id
                    where u.is_deleted = false
                      and p.is_deleted = false
                      and t.is_deleted = false
                      and t.status = 'DONE'
                    group by u.id, u.email
                ) as x
                """;

        var list = jdbcTemplate.query(
                dataSql,
                ps -> {
                    ps.setInt(1, pageable.getPageSize());
                    ps.setLong(2, pageable.getOffset());
                },
                (rs, rowNum) -> UserDoneTasksStatsDto.builder()
                        .userId(rs.getLong("user_id"))
                        .email(rs.getString("email"))
                        .doneCount(rs.getLong("done_count"))
                        .rank(rs.getInt("rank"))
                        .build()
        );

        long total = queryForLong(countSql);

        return new PageImpl<>(list, pageable, total);
    }

    private long queryForLong(String sql) {
        try {
            Long value = jdbcTemplate.queryForObject(sql, Long.class);
            return value == null ? 0L : value;
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }
}
