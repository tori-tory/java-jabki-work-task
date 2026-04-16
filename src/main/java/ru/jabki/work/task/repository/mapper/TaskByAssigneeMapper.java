package ru.jabki.work.task.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.jabki.work.task.model.dto.TaskByAssignee;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TaskByAssigneeMapper implements RowMapper<TaskByAssignee> {
    @Override
    public TaskByAssignee mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TaskByAssignee(
               rs.getLong("assignee_id"),
                rs.getLong("task_count"));
    }
}