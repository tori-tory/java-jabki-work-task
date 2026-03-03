package ru.jabki.work.task.exception;

import lombok.Data;

@Data
public class ApiError {
    final boolean success;
    final String message;
}