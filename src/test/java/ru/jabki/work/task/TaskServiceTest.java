package ru.jabki.work.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.jabki.work.task.client.UserClient;
import ru.jabki.work.task.exception.NotFoundException;
import ru.jabki.work.task.exception.TaskException;
import ru.jabki.work.task.model.Task;
import ru.jabki.work.task.model.TaskStatus;
import ru.jabki.work.task.model.dto.TaskRequest;
import ru.jabki.work.task.model.dto.TaskResponse;
import ru.jabki.work.task.model.dto.TaskUpdateRequest;
import ru.jabki.work.task.repository.TaskRepository;
import ru.jabki.work.task.repository.mapper.TaskResponseMapper;
import ru.jabki.work.task.service.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    private UserClient userClient;

    @Mock
    private TaskResponseMapper taskResponseMapper;

    @Test
    void testCreateTask_valid() {
        TaskRequest request = getTaskRequest("Title");
        TaskResponse expectedResponse = getTaskResponse();
        Task task = getTask();

        when(userClient.existsById(anyLong())).thenReturn(true);
        when(taskRepository.insert(any(Task.class))).thenReturn(task);
        when(taskResponseMapper.toTaskResponse(any())).thenReturn(expectedResponse);

        TaskResponse response = taskService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Title", response.title());
        assertEquals("Description", response.description());

        //проверить что "checkUser" вызвался 2 раза
        verify(userClient, times(2)).existsById(anyLong());
        verify(taskRepository, times(1)).insert(any(Task.class));
    }

    @Test
    void testCreateTask_invalidData_nullTitle_throwTaskException() {
        TaskRequest request = getTaskRequest(null);
        Task task = getTask();

        final TaskException exception = assertThrows(
                TaskException.class,
                () -> taskService.create(request)
        );

        assertEquals("Заголовок не может быть пустым", exception.getMessage());
        verify(taskRepository, never()).insert(any());
    }

    @Test
    void testUpdateTask_valid() {
        TaskUpdateRequest request = getTaskUpdateRequest();
        TaskResponse expectedResponse = getTaskResponse();
        Task task = getTask();

        when(userClient.existsById(anyLong())).thenReturn(true);
        when(taskRepository.getById(1L)).thenReturn(task);
        when(taskRepository.update(any(Task.class))).thenReturn(task);
        when(taskResponseMapper.toTaskResponse(any())).thenReturn(expectedResponse);

        TaskResponse response = taskService.update(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Title", response.title());
        assertEquals(TaskStatus.DONE, response.status());

        //проверить что "checkUser" вызвался 2 раза
        verify(userClient, times(2)).existsById(anyLong());
        verify(taskRepository, times(1)).update(any(Task.class));
    }

    @Test
    void testExistsById_NoThrowException_WhenTaskFound() {
        final Task task = getTask();
        TaskResponse expectedResponse = getTaskResponse();
        TaskRequest request = getTaskRequest("Title");
        when(taskRepository.getById(1L)).thenReturn(task);
        when(taskResponseMapper.toTaskResponse(any())).thenReturn(expectedResponse);

        TaskResponse response = taskService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Title", response.title());
        verify(taskRepository, times(1)).getById(1L);
    }

    @Test
    void testExistsById_ThrowException_WhenTaskNotFound() {
        final Task task = getTask();
        Long taskId = 99L;
        when(taskRepository.getById(taskId)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> taskService.getById(taskId)
        );

        assertEquals(String.format("Задача с id %d не найдена", taskId), exception.getMessage());
        verify(taskRepository, times(1)).getById(taskId);
    }

    private TaskRequest getTaskRequest(String title){
        return new TaskRequest(
                title,
                "Description",
                LocalDate.of(2026, 3, 10),
                1L,
                1L);
    }

    private TaskUpdateRequest getTaskUpdateRequest(){
        return new TaskUpdateRequest(
                1L,
                "New Title",
                "Description",
                now(),
                1L,
                TaskStatus.DONE,
                1L);
    }

    private TaskResponse getTaskResponse(){
        return new TaskResponse(
                1L,
                "Title",
                "Description",
                TaskStatus.DONE,
                now(),
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    private Task getTask() {
        return Task.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .status(TaskStatus.TO_DO)
                .deadLine(now())
                .author(1L)
                .assignee(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();
    }
}