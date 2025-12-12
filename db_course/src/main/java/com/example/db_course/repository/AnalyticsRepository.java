package com.example.db_course.repository;

import com.example.db_course.dto.response.ProjectTaskStatusStatsDto;
import com.example.db_course.dto.response.UserDoneTasksStatsDto;
import com.example.db_course.entity.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public Page<ProjectTaskStatusStatsDto> tasksByProjectAndStatus(Pageable pageable) {
        String sql = """
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
                """;

        var list = jdbcTemplate.query(sql, (rs, rowNum) ->
                ProjectTaskStatusStatsDto.builder()
                        .projectId(rs.getLong("project_id"))
                        .status(TaskStatus.valueOf(rs.getString("status")))
                        .taskCount(rs.getLong("task_count"))
                        .build()
        );
        return new PageImpl<>(list, pageable, list.size());
    }

    public Page<UserDoneTasksStatsDto> topAssigneesByDoneTasks(Pageable pageable) {
        String sql = """
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
                limit ?
                """;

        var list = jdbcTemplate.query(sql, ps -> ps.setInt(1, pageable.getPageSize()), (rs, rowNum) ->
                UserDoneTasksStatsDto.builder()
                        .userId(rs.getLong("user_id"))
                        .email(rs.getString("email"))
                        .doneCount(rs.getLong("done_count"))
                        .rank(rs.getInt("rank"))
                        .build()
        );
        return new PageImpl<>(list, pageable, list.size());
    }
}
