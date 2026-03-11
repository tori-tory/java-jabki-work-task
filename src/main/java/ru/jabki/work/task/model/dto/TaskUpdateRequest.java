package ru.jabki.work.task.model.dto;

import ru.jabki.work.task.model.TaskStatus;

import java.time.LocalDate;

public record TaskUpdateRequest(
        Long id,
        String title,
        String description,
        LocalDate deadLine,
        Long assignee,
        TaskStatus status,
        Long editor) {
}