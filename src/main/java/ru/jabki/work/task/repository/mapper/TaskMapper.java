package ru.jabki.work.task.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.jabki.work.task.model.Task;
import ru.jabki.work.task.model.TaskStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class TaskMapper implements RowMapper<Task> {

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Task.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .status(TaskStatus.valueOf(rs.getString("status")))
                .deadLine(rs.getObject("dead_line", LocalDate.class))
                .author(rs.getLong("author_id"))
                .assignee(rs.getLong("assignee_id"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                .build();
    }
}