package ru.jabki.work.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jabki.work.task.model.Event;
import ru.jabki.work.task.model.dto.EventRequest;

import ru.jabki.work.task.model.dto.EventResponse;
import ru.jabki.work.task.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public void create(final EventRequest eventRequest) {
        Event event = Event.builder()
                .taskId(eventRequest.taskId())
                .editorId(eventRequest.editorId())
                .logMessage(eventRequest.logMessage())
                .build();

        eventRepository.insert(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getByTask(final Long taskId) {
        List<Event> events = eventRepository.getByTask(taskId);

        return events.stream()
                .map(event -> new EventResponse(
                        event.getTaskId(),
                        event.getEditorId(),
                        event.getCreatedAt(),
                        event.getLogMessage()
                ))
                .toList();
    }
}