package ru.jabki.work.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jabki.work.task.model.TaskStatus;
import ru.jabki.work.task.repository.StatusChangeRepository;

@Service
@RequiredArgsConstructor
public class StatusChangeService {
    private final StatusChangeRepository statusChangeRepository;

    @Transactional(readOnly = true)
    public boolean isAllowed(TaskStatus oldStatus, TaskStatus newStatus) {
        return statusChangeRepository.isValidStatusChange(oldStatus, newStatus);
    }
}