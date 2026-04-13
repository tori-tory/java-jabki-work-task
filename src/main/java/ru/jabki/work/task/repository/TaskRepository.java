package ru.jabki.work.task.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.jabki.work.task.model.Task;
import ru.jabki.work.task.model.dto.TaskByAssignee;
import ru.jabki.work.task.model.dto.TaskFilter;
import ru.jabki.work.task.model.dto.TaskByStatus;
import ru.jabki.work.task.model.dto.TaskReportParams;
import ru.jabki.work.task.repository.mapper.TaskByAssigneeMapper;
import ru.jabki.work.task.repository.mapper.TaskByStatusMapper;
import ru.jabki.work.task.repository.mapper.TaskMapper;

import java.util.Collections;
import java.util.List;

@Repository
@AllArgsConstructor
public class TaskRepository {
    private static final String INSERT = """
            INSERT INTO work_task.task(title, description, status, dead_line, author_id, assignee_id)
            VALUES (:title, :description, :status, :dead_line, :author_id, :assignee_id)
            RETURNING *
            """;

    private static final String GET_BY_ID = """
            SELECT *
            FROM work_task.task
            WHERE id = :id
            AND status <> 'DELETE'
            """;

    private static final String EXISTS_BY_ASSIGNEE_ID =  """
            SELECT EXISTS (
                SELECT 1
                FROM work_task.task
                WHERE assignee_id = :assignee_id
                AND status not in ('DONE', 'DELETE')
            )
            """;

    private static final String UPDATE = """
            UPDATE work_task.task
            SET title = :title,
                description = :description,
                status = :status,
                dead_line = :dead_line,
                assignee_id = :assignee_id,
                updated_at = now()
            WHERE id = :id
            RETURNING *
            """;

    private final TaskMapper taskMapper;
    private final TaskByStatusMapper taskByStatusMapper;
    private final TaskByAssigneeMapper taskByAssigneeMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Task insert(final Task task){
        return jdbcTemplate.queryForObject(INSERT, taskToSql(task), taskMapper);
    }

    public Task getById(final Long id){
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, new MapSqlParameterSource("id", id), taskMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean existsByAssigneeId(final Long userId){
        return Boolean.TRUE.equals(
            jdbcTemplate.queryForObject(EXISTS_BY_ASSIGNEE_ID, new MapSqlParameterSource("assignee_id", userId), Boolean.class));
    }

    public Task update(final Task task){
        return jdbcTemplate.queryForObject(UPDATE, taskToSql(task), taskMapper);
    }

    public List<Task> getTaskListByFilter(final TaskFilter taskFilter){
        StringBuilder selectSql = new StringBuilder("""
            SELECT *
            FROM work_task.task
            WHERE 1=1
            AND status <> 'DELETE'
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (taskFilter.status() != null) {
            selectSql.append(" AND status = :status");
            params.addValue("status", taskFilter.status().name());
        }

        if (taskFilter.assignee() != null) {
            selectSql.append(" AND assignee_id = :assignee_id");
            params.addValue("assignee_id", taskFilter.assignee());
        }

        return jdbcTemplate.query(selectSql.toString(), params, taskMapper);
    }

    public List<Task> findByAssignees(List<Long> ids) {
        String sql = """
            SELECT *
            FROM work_task.task
            WHERE assignee_id IN (:ids)
            AND status NOT IN ('DELETE')
            """;
        try {
            return jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids), taskMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long totalByAssignees(TaskReportParams params) {
        String sql = """
            SELECT count(*)
            FROM work_task.task
            WHERE assignee_id IN (:ids)
            AND status NOT IN ('DELETE')
            AND dead_line BETWEEN :date_from AND :date_to
            """;
        return jdbcTemplate.queryForObject(sql, reportParamsToSql(params), Long.class);
    }

    public Double avgDays(TaskReportParams params) {
        String sql = """
            SELECT COALESCE(ROUND(
                    (AVG(EXTRACT(EPOCH FROM (updated_at - created_at))) / 86400)::numeric,
                    3
                ), 0) AS avg_days
            FROM work_task.task
            WHERE status = 'DONE'
            AND assignee_id IN (:ids)
            AND dead_line BETWEEN :date_from AND :date_to
            """;
        return jdbcTemplate.queryForObject(sql, reportParamsToSql(params), Double.class);
    }

    public List<TaskByStatus> taskStatusByAssignees(TaskReportParams params) {
        String sql = """
            SELECT status, count(*) AS task_count
            FROM work_task.task
            WHERE assignee_id IN (:ids)
            AND status NOT IN ('DELETE')
            AND dead_line BETWEEN :date_from AND :date_to
            GROUP BY status
            """;
        try {
            return jdbcTemplate.query(sql, reportParamsToSql(params), taskByStatusMapper);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<TaskByAssignee> taskByAssignees(TaskReportParams params) {
        if (params.assigneeIds() == null || params.assigneeIds().isEmpty()) {
            return Collections.emptyList();
        }

        String sql = """
                SELECT ids.assignee_id, COUNT(t.id) AS task_count
                FROM unnest(CAST(:idsArray AS BIGINT[])) AS ids(assignee_id)
                LEFT JOIN work_task.task t ON t.assignee_id = ids.assignee_id 
                        AND dead_line BETWEEN :date_from AND :date_to
                GROUP BY ids.assignee_id;
                """;
        try {
            return jdbcTemplate.query(sql, reportParamsToSql(params), taskByAssigneeMapper);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    private MapSqlParameterSource taskToSql(final Task task){
        final MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", task.getId());
        params.addValue("title", task.getTitle());
        params.addValue("description", task.getDescription());
        params.addValue("status", task.getStatus().name());
        params.addValue("dead_line", task.getDeadLine());
        params.addValue("author_id", task.getAuthor());
        params.addValue("assignee_id", task.getAssignee());
        return params;
    }

    private MapSqlParameterSource reportParamsToSql(final TaskReportParams reportParams){
        final MapSqlParameterSource params = new MapSqlParameterSource();
        Long[] idsArray = reportParams.assigneeIds().toArray(new Long[0]);

        params.addValue("idsArray", idsArray);
        params.addValue("ids", reportParams.assigneeIds());
        params.addValue("date_from", reportParams.dateFrom());
        params.addValue("date_to", reportParams.dateTo());
        return params;
    }
}