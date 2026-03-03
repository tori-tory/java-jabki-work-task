package ru.jabki.work.task.model.dto;

import ru.jabki.work.task.model.TaskStatus;

import java.time.LocalDate;

public record TaskRequest(
        String title,
        String description,
        TaskStatus status,
        LocalDate dead_line,
        Long author,
        Long assignee) {
}