package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDTO {
    public Subordination subordination;
    public Integer id;
    public String name;
    public String description;
    public Status status;
    public LocalDateTime startTime;
    public Duration duration;
    public Integer epicId;

    private TaskDTO(Subordination subordination, Integer id, String name, String description, Status status, LocalDateTime startTime, Duration duration, Integer epicId) {
        this.subordination = subordination;
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.epicId = epicId;
    }

    public static TaskDTO get(Task task) {
        TaskDTO taskDTO = new TaskDTO(
                task.getSubordination(),
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getStartTime(),
                task.getDuration(),
                null);
        if (task.getSubordination() == Subordination.SUBTASK) {
            taskDTO.epicId = ((Subtask) task).getEpicId();
        }
        return taskDTO;
    }

    public static Selftask toSelftask(TaskDTO taskDTO) {
        Selftask task = new Selftask(taskDTO.name, taskDTO.description, taskDTO.startTime, taskDTO.duration);
        task.setId(taskDTO.id);
        task.setStatus(taskDTO.status);
        return task;
    }

    public static Epictask toEpictask(TaskDTO taskDTO) {
        Epictask task = new Epictask(taskDTO.name, taskDTO.description, taskDTO.startTime, taskDTO.duration);
        task.setId(taskDTO.id);
        task.setStatus(taskDTO.status);
        return task;
    }

    public static Subtask toSubtask(TaskDTO taskDTO) {
        Subtask task = new Subtask(taskDTO.name, taskDTO.description, taskDTO.startTime, taskDTO.duration, taskDTO.epicId);
        task.setId(taskDTO.id);
        task.setStatus(taskDTO.status);
        return task;
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "subordination=" + subordination +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", epicId=" + epicId +
                '}';
    }
}
