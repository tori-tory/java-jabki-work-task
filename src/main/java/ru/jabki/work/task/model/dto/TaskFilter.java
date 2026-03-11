package ru.jabki.work.task.model.dto;

import ru.jabki.work.task.model.TaskStatus;

public record TaskFilter(TaskStatus status, Long assignee) {
}