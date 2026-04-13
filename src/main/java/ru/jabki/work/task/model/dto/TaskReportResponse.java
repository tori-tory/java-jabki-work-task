package ru.jabki.work.task.model.dto;

import ru.jabki.work.task.model.TaskStatus;

import java.util.Map;

public record TaskReportResponse(
    Long totalTasks,
    Map<TaskStatus, Long> taskByStatus,
    Map<Long, Long> taskByAssignee,
    Double avgDays) {
}