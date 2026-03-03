package ru.jabki.work.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jabki.work.task.client.UserClient;
import ru.jabki.work.task.exception.NotFoundException;
import ru.jabki.work.task.exception.TaskException;
import ru.jabki.work.task.model.Task;
import ru.jabki.work.task.model.TaskStatus;
import ru.jabki.work.task.model.dto.TaskFilter;
import ru.jabki.work.task.model.dto.TaskRequest;
import ru.jabki.work.task.model.dto.TaskResponse;
import ru.jabki.work.task.model.dto.TaskUpdateRequest;
import ru.jabki.work.task.repository.TaskRepository;
import ru.jabki.work.task.repository.mapper.TaskResponseMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserClient userClient;

    @Transactional
    public TaskResponse create(final TaskRequest taskRequest) {
        validateCreate(taskRequest);
        Task task = Task.builder()
                .title(taskRequest.title())
                .description(taskRequest.description())
                .status(taskRequest.status())
                .deadLine(taskRequest.dead_line())
                .assignee(taskRequest.assignee())
                .author(taskRequest.author())
                .build();

        Task newTask = taskRepository.insert(task);

        return new TaskResponseMapper().toTaskResponse(newTask);
    }

    @Transactional
    public TaskResponse update(TaskUpdateRequest taskUpdateRequest){
        Task task = taskRepository.getById(taskUpdateRequest.id());
        if (task == null) {
            throw new NotFoundException(String.format("Задача с id %d не найдена", taskUpdateRequest.id()));
        }

        validateUpdate(taskUpdateRequest);

        task.setTitle(taskUpdateRequest.title());
        task.setStatus(taskUpdateRequest.status());

        Task updatedtask = taskRepository.update(task);
        return new TaskResponseMapper().toTaskResponse(updatedtask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(final Long id){
        Task task = taskRepository.getById(id);
        if (task == null) {
            throw new NotFoundException(String.format("Задача с id %d не найдена", id));
        }
        return new TaskResponseMapper().toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTaskListByFilter(final TaskFilter taskFilter){
        List<Task> tasks = taskRepository.getTaskListByFilter(taskFilter);

        return tasks.stream()
                .map(task -> new TaskResponseMapper().toTaskResponse(task))
                .collect(Collectors.toList());
    }

    private void validateCreate(final TaskRequest taskRequest){
        validateTitle(taskRequest.title());
        validateStatus(taskRequest.status());
        checkUser(taskRequest.author(), "Автор");
        checkUser(taskRequest.assignee(), "Исполнитель");
    }

    private void validateUpdate(final TaskUpdateRequest taskUpdateRequest){
        validateTitle(taskUpdateRequest.title());
        validateStatus(taskUpdateRequest.status());
        checkUser(taskUpdateRequest.editor(), "Редактор");
    }

    private void validateTitle(final String title){
        if ((title == null) || title.isBlank()) {
            throw new TaskException("Заголовок не может быть пустым");
        }
    }

    private void validateStatus(final TaskStatus taskStatus){
        if (taskStatus == null) {
            throw new TaskException("Статус не задан");
        }
    }

    private void checkUser(Long id, String field) {
        if (id == null) {
            throw new TaskException(String.format("Значение %s не задано", field));
        }
        if (!userClient.existsById(id)) {
            throw new NotFoundException(String.format("Значение %s с id %s не найдено",field, id));
        }
    }
}