package ru.jabki.work.task.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.jabki.work.task.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .taskId(rs.getLong("task_id"))
                .editorId(rs.getLong("editor_id"))
                .logMessage(rs.getString("log_message"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .build();
    }
}