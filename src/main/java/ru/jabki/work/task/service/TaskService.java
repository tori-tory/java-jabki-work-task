package ru.jabki.work.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jabki.work.task.client.UserClient;
import ru.jabki.work.task.exception.NotFoundException;
import ru.jabki.work.task.exception.TaskException;
import ru.jabki.work.task.model.Task;
import ru.jabki.work.task.model.TaskStatus;
import ru.jabki.work.task.model.dto.EventRequest;
import ru.jabki.work.task.model.dto.TaskFilter;
import ru.jabki.work.task.model.dto.TaskRequest;
import ru.jabki.work.task.model.dto.TaskResponse;
import ru.jabki.work.task.model.dto.TaskUpdateRequest;
import ru.jabki.work.task.repository.TaskRepository;
import ru.jabki.work.task.repository.mapper.TaskResponseMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final EventService eventService;
    private final StatusChangeService statusChangeService;
    private final UserClient userClient;
    private final TaskResponseMapper taskResponseMapper;

    @Transactional
    public TaskResponse create(final TaskRequest taskRequest) {
        validateCreate(taskRequest);

        Task task = Task.builder()
                .title(taskRequest.title())
                .description(taskRequest.description())
                .status(TaskStatus.TO_DO)
                .deadLine(taskRequest.deadLine())
                .assignee(taskRequest.assignee())
                .author(taskRequest.author())
                .build();

        Task newTask = taskRepository.insert(task);

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Создана задача. ")
                .append(newTask.getId())
                .append(String.format(": %s", newTask.getTitle()));
        eventService.create(new EventRequest(newTask.getId(), newTask.getAuthor(), logMessage.toString()));

        return taskResponseMapper.toTaskResponse(newTask);
    }

    @Transactional
    public TaskResponse update(TaskUpdateRequest taskUpdateRequest){
        Task task = taskRepository.getById(taskUpdateRequest.id());
        if (task == null) {
            throw new NotFoundException(String.format("Задача с id %d не найдена", taskUpdateRequest.id()));
        }

        validateUpdate(taskUpdateRequest);

        StringBuilder logMessage = new StringBuilder();

        if (task.getStatus() != taskUpdateRequest.status()) {
            checkRoleManager(taskUpdateRequest.editor());
            checkStatusChange(task.getStatus(), taskUpdateRequest.status());
            logMessage.append(String.format("Статус было: %s, стало: %s. ", task.getStatus().name(), taskUpdateRequest.status()));
            task.setStatus(taskUpdateRequest.status());
        }

        if (!Objects.equals(task.getTitle(), taskUpdateRequest.title())) {
            logMessage.append(String.format("Заголовок было: %s, стало: %s. ", task.getTitle(), taskUpdateRequest.title()));
            task.setTitle(taskUpdateRequest.title());
        }

        if (!Objects.equals(task.getDescription(), taskUpdateRequest.description())) {
            logMessage.append(String.format("Описание было: %s, стало: %s. ", task.getDescription(), taskUpdateRequest.description()));
            task.setDescription(taskUpdateRequest.description());
        }

        if (!Objects.equals(task.getDeadLine(), taskUpdateRequest.deadLine())) {
            validateDeadLine(taskUpdateRequest.deadLine());
            logMessage.append(String.format("Срок выполнения было: %s, стало: %s. ", task.getDeadLine().toString(), taskUpdateRequest.deadLine().toString()));
            task.setDeadLine(taskUpdateRequest.deadLine());
        }

        if (!Objects.equals(task.getAssignee(), taskUpdateRequest.assignee())) {
            logMessage.append(String.format("Исполнитель было: %s, стало: %s. ", task.getAssignee(), taskUpdateRequest.assignee()));
            task.setAssignee(taskUpdateRequest.assignee());
        }

        if (logMessage.isEmpty()) {
            return taskResponseMapper.toTaskResponse(task);
        }

        Task updatedtask = taskRepository.update(task);
        eventService.create(new EventRequest(taskUpdateRequest.id(), taskUpdateRequest.editor(), logMessage.toString()));
        return taskResponseMapper.toTaskResponse(updatedtask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(final Long id){
        Task task = taskRepository.getById(id);
        if (task == null) {
            throw new NotFoundException(String.format("Задача с id %d не найдена", id));
        }
        return taskResponseMapper.toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public boolean existsByAssigneeId(final Long userId) {
        return taskRepository.existsByAssigneeId(userId);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTaskListByFilter(final TaskFilter taskFilter){
        List<Task> tasks = taskRepository.getTaskListByFilter(taskFilter);

        return tasks.stream()
                .map(task -> taskResponseMapper.toTaskResponse(task))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTaskListByAssigneeIds(final List<Long> ids) {
        List<Task> tasks = taskRepository.findByAssignees(ids);
        return tasks.stream()
                .map(task -> taskResponseMapper.toTaskResponse(task))
                .toList();
    }

    private void validateCreate(final TaskRequest taskRequest){
        validateTitle(taskRequest.title());
        checkUser(taskRequest.author(), "Автор");
        checkUser(taskRequest.assignee(), "Исполнитель");
    }

    private void validateUpdate(final TaskUpdateRequest taskUpdateRequest){
        checkUser(taskUpdateRequest.assignee(), "Исполнитель");
        checkUser(taskUpdateRequest.editor(), "Редактор");
        validateTitle(taskUpdateRequest.title());
        validateStatus(taskUpdateRequest.status());
    }

    private void validateTitle(final String title){
        if ((title == null) || title.isBlank()) {
            throw new TaskException("Заголовок не может быть пустым");
        }
    }

    private void validateStatus(final TaskStatus Status){
        if (Status == null) {
            throw new TaskException("Статус не задан");
        }
    }

    private void validateDeadLine(LocalDate deadLine){
        if (deadLine.isBefore(LocalDate.now())) {
            throw new TaskException("Дедлайн должен быть в будущем");
        }
    }

    private void checkStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus) {
        if (!statusChangeService.isAllowed(oldStatus, newStatus)){
            throw new TaskException(String.format("Запрещённый переход из %s в %s", oldStatus.name(), newStatus.name()));
        }
    }

    private void checkRoleManager(final Long editorId){
        if (!userClient.isManager(editorId)) {
            throw new TaskException(String.format("Пользователь %s не имеет роли MANAGER", editorId));
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