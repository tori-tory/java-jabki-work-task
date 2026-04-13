package ru.jabki.work.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.jabki.work.task.model.TaskStatus;
import ru.jabki.work.task.model.dto.TaskFilter;
import ru.jabki.work.task.model.dto.TaskReportParams;
import ru.jabki.work.task.model.dto.TaskReportResponse;
import ru.jabki.work.task.model.dto.TaskRequest;
import ru.jabki.work.task.model.dto.TaskResponse;
import ru.jabki.work.task.model.dto.TaskUpdateRequest;
import ru.jabki.work.task.service.TaskService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/task")
@Tag(name = "Задачи")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Создать задачу")
    public TaskResponse create(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.create(taskRequest);
    }

    @PatchMapping
    @Operation(summary = "Обновить задачу")
    public TaskResponse update(@RequestBody TaskUpdateRequest taskUpdateRequest){
        return taskService.update(taskUpdateRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary =  "Получить задачу по id")
    public TaskResponse getById(@PathVariable("id") Long id){
        return taskService.getById(id);
    }

    @GetMapping("/exists/assignee/{userId}")
    @Operation(summary = "Проверка, назначены ли на пользователя задачи")
    public boolean existsByAssigneeId(@PathVariable("userId") Long userId){
        return taskService.existsByAssigneeId(userId);
    }
    @GetMapping("/tasks")
    @Operation(summary = "Получить список задач по статусу и/или исполнителю")
    public List<TaskResponse> findTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assignee){
        TaskFilter filter = new TaskFilter(status, assignee);
        return taskService.getTaskListByFilter(filter);
    }

    @PostMapping("/tasks/by-assignees")
    @Operation(summary = "Получить список задач по одному и более исполнителю")
    public List<TaskResponse> getTasksByAssogneeList(@RequestBody List<Long> ids) {
        return taskService.getTaskListByAssigneeIds(ids);
    }

    @PostMapping("/reports/by-assignees")
    @Operation(summary = "Получить отчет по исполнителям за период")
    public TaskReportResponse getReport(
            @RequestBody TaskReportParams params) {
        return taskService.taskReport(params);
    }
}