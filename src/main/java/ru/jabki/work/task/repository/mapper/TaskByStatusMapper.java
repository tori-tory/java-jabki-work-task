package ru.jabki.work.task.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.jabki.work.task.model.TaskStatus;
import ru.jabki.work.task.model.dto.TaskByStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TaskByStatusMapper implements RowMapper<TaskByStatus> {
    @Override
    public TaskByStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TaskByStatus(
                TaskStatus.valueOf(rs.getString("status")),
                rs.getLong("task_count"));
    }
}