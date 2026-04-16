package ru.jabki.work.task.model.dto;

import ru.jabki.work.task.model.TaskStatus;

public record TaskByStatus(TaskStatus status, Long count) {
}