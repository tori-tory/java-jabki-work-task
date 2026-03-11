package ru.jabki.work.task.repository.mapper;

import org.springframework.stereotype.Component;
import ru.jabki.work.task.model.Task;
import ru.jabki.work.task.model.dto.TaskResponse;

@Component
public class TaskResponseMapper {
    public TaskResponse toTaskResponse(Task task){
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadLine(),
                task.getAuthor(),
                task.getAssignee(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}