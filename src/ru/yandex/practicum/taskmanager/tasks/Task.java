package ru.yandex.practicum.taskmanager.tasks;

public abstract class Task {
    private Integer id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
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
}