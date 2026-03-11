package ru.jabki.work.task.model.dto;

import java.time.LocalDate;

public record TaskRequest(
        String title,
        String description,
        LocalDate deadLine,
        Long author,
        Long assignee) {
}