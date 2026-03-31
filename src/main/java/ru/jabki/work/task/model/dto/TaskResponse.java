package ru.jabki.work.task.model.dto;

import ru.jabki.work.task.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDate deadLine,
        Long author,
        Long assignee,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}