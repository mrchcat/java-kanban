package ru.yandex.practicum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Task {
    public static final String DELIMITER = "~";
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public abstract Subordination getSubordination();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task task)) {
            return false;
        }
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            throw new IllegalArgumentException("Tasks with null ID can't be compared!");
        } else {
            return id.hashCode();
        }
    }

    public abstract Task copy();

    public String convertToFileRecord() {
        return String.join(DELIMITER,
                id.toString(),
                getSubordination().toString(),
                name,
                status.toString(),
                description,
                "true",
                String.valueOf(startTime.toLocalDate().toEpochDay()),
                String.valueOf(startTime.toLocalTime().toSecondOfDay()),
                String.valueOf(duration.toSeconds()));
    }
}