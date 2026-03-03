package ru.jabki.work.task.model.dto;

import ru.jabki.work.task.model.TaskStatus;

public record TaskUpdateRequest(
        Long id,
        String title,
        TaskStatus status,
        Long editor) {
}