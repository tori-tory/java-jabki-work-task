package ru.jabki.work.task.model.dto;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record TaskRequest(
        String title,
        String description,
        @FutureOrPresent(message = "Дедлайн должен быть в будущем") LocalDate deadLine,
        Long author,
        Long assignee) {
}