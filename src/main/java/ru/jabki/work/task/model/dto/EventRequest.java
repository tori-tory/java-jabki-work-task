package ru.jabki.work.task.model.dto;

public record EventRequest(
        Long taskId,
        Long editorId,
        String logMessage) {
}