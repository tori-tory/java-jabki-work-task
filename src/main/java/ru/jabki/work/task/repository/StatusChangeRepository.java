package ru.jabki.work.task.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.jabki.work.task.model.TaskStatus;

@Repository
@AllArgsConstructor
public class StatusChangeRepository {

    private static final String IS_VALID_STATUS_CHANGE = """
            SELECT EXISTS (
                SELECT 1
                FROM work_task.status_change
                WHERE status_from = :status_from
                AND status_to = :status_to
                AND is_active = TRUE
            )
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public boolean isValidStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status_from", oldStatus.name());
        params.addValue("status_to", newStatus.name());
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(IS_VALID_STATUS_CHANGE, params, Boolean.class));
    }
}