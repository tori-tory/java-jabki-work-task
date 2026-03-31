package ru.jabki.work.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jabki.work.task.model.dto.EventResponse;
import ru.jabki.work.task.service.EventService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/event")
@Tag(name = "События")
public class EventController {

    private final EventService eventService;

    @GetMapping("/task/{id}")
    @Operation(summary = "История событий по задаче")
    public List<EventResponse> getByTask(@PathVariable("id") Long taskId){
        return eventService.getByTask(taskId);
    }
}