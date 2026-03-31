package ru.jabki.work.task.model.dto;

import java.time.LocalDateTime;

public record EventResponse(
        Long taskId,
        Long editorId,
        LocalDateTime createdAt,
        String logMessage) {
}