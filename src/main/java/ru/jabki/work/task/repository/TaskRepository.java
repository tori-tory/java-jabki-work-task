package ru.jabki.work.task.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.jabki.work.task.model.Task;
import ru.jabki.work.task.model.dto.TaskFilter;
import ru.jabki.work.task.repository.mapper.TaskMapper;

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
}