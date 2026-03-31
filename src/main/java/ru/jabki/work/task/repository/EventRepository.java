package ru.jabki.work.task.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.jabki.work.task.model.Event;
import ru.jabki.work.task.repository.mapper.EventMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class EventRepository {

    private static final String ADD_EVENT = """
            INSERT INTO work_task.task_event(task_id, editor_id, log_message)
            VALUES (:task_id, :editor_id, :log_message)
            """;

    private final EventMapper eventMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void insert(final Event event){
        jdbcTemplate.update(
                ADD_EVENT,
                new MapSqlParameterSource()
                .addValue("task_id", event.getTaskId())
                .addValue("editor_id", event.getEditorId())
                .addValue("log_message", event.getLogMessage()));
    }

    public List<Event> getByTask(final Long taskId){
        String selectSql = """
                SELECT e.id, e.task_id, e.editor_id, e.log_message, e.created_at
                FROM work_task.task_event e
                WHERE task_id = :task_id
                ORDER BY created_at desc
                """;

        return jdbcTemplate.query(
                selectSql,
                new MapSqlParameterSource().addValue("task_id", taskId),
                eventMapper);
    }
}