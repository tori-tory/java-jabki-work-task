package ru.jabki.work.task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Long id;
    private Long taskId;
    private Long editorId;
    private LocalDateTime createdAt;
    private String logMessage;
}