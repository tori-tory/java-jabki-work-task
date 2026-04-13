package ru.jabki.work.task.model.dto;

import java.time.LocalDate;
import java.util.List;

public record TaskReportParams(List<Long> assigneeIds, LocalDate dateFrom, LocalDate dateTo) {
}