package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.*;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegularTaskManager implements TaskManager {
    private final Repository<Integer, Task> tasks;
    private final Repository<Integer, ArrayList<Integer>> subordinates;
    private final Generator generator;
    private final HistoryManager history;

    public RegularTaskManager(Repository<Integer, Task> tasks,
                              Repository<Integer, ArrayList<Integer>> subordinates,
                              Generator generator,
                              HistoryManager history) {
        this.tasks = tasks;
        this.subordinates = subordinates;
        this.generator = generator;
        this.history = history;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void clearHistory() {
        history.clear();
    }

    @Override
    public void clear() {
        tasks.clear();
        subordinates.clear();
    }

    @Override
    public Selftask add(Selftask task) {
        if (task == null) {
            return null;
        }
        Selftask copy = new Selftask(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration());
        Integer id = generator.getId();
        copy.setId(id);
        tasks.put(id, copy);
        return (Selftask) copy.copy();
    }

    @Override
    public Epictask add(Epictask task) {
        if (task == null) {
            return null;
        }
        Epictask copy = new Epictask(task.getName(), task.getDescription());
        Integer id = generator.getId();
        copy.setId(id);
        tasks.put(id, copy);
        subordinates.put(id, new ArrayList<>());
        return (Epictask) copy.copy();
    }

    @Override
    public Subtask add(Subtask task) {
        if (task == null) {
            return null;
        }
        Integer epicId = task.getEpicId();
        Task epicTask = tasks.get(epicId);
        if ((epicTask != null) && (epicTask.getSubordination() == Subordination.EPIC)) {
            Subtask copy = new Subtask(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration(), epicId);
            Integer id = generator.getId();
            copy.setId(id);
            tasks.put(id, copy);
            subordinates.get(epicId).add(id);
            updateEpicTimeWhenAddSub((Epictask) epicTask, task.getStartTime(), task.getDuration());
            return copy.copy();
        } else {
            return null;
        }
    }

    private void updateEpicTimeWhenAddSub(Epictask epic, LocalDateTime subStart, Duration subDuration) {
        if (!epic.isTimeDefined()) {
            epic.setTimeDefined(true);
            epic.setStartTime(subStart);
            epic.setDuration(subDuration);
            return;
        }
        LocalDateTime epicStart = epic.getStartTime();
        LocalDateTime epicFinish = epicStart.plus(epic.getDuration());
        LocalDateTime subFinish = subStart.plus(subDuration);
        if (epicStart.isAfter(subStart)) {
            epicStart = subStart;
        }
        if (epicFinish.isBefore(subFinish)) {
            epicFinish = subFinish;
        }
        epic.setStartTime(epicStart);
        epic.setDuration(Duration.between(epicStart, epicFinish));
    }

    @Override
    public Task get(Integer id) {
        if (id == null) return null;
        Task original = tasks.get(id);
        Task toReturn = null;
        if (original != null) {
            toReturn = original.copy();
            history.add(toReturn);
        }
        return toReturn;
    }

    @Override
    public Epictask getEpic(Integer id) {
        if (id == null) {
            return null;
        }
        Task task = get(id);
        Epictask epictask = null;
        if ((task != null) && (task.getSubordination() == Subordination.EPIC)) {
            epictask = (Epictask) task;
            history.add(epictask);
        }
        return epictask;
    }

    @Override
    public Selftask getSelftask(Integer id) {
        if (id == null) {
            return null;
        }
        Task task = get(id);
        Selftask selftask = null;
        if ((task != null) && (task.getSubordination() == Subordination.SELF)) {
            selftask = (Selftask) task;
            history.add(selftask);
        }
        return selftask;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        if (id == null) {
            return null;
        }
        Task task = get(id);
        Subtask subtask = null;
        if ((task != null) && (task.getSubordination() == Subordination.SUBTASK)) {
            subtask = (Subtask) task;
            history.add(subtask);
        }
        return subtask;
    }


    @Override
    public Task delete(Integer taskId) {
        Task taskToDeleted = tasks.get(taskId);
        if (taskToDeleted == null) {
            return null;
        }
        switch (taskToDeleted.getSubordination()) {
            case SELF -> tasks.delete(taskId);
            case Subordination.EPIC -> {
                for (Integer subId : subordinates.get(taskId)) {
                    tasks.delete(subId);
                }
                subordinates.delete(taskId);
                tasks.delete(taskId);
            }
            case Subordination.SUBTASK -> {
                Subtask subtaskToDelete = (Subtask) taskToDeleted;
                Epictask epic = (Epictask) tasks.get(subtaskToDelete.getEpicId());
                if (epic.getSubordination() != Subordination.EPIC) {
                    throw new IllegalArgumentException("Incorrect EpicId");
                }
                if (!subordinates.get(epic.getId()).contains(subtaskToDelete.getId())) {
                    throw new IllegalArgumentException("Subtask is absent in the list of Epic task");
                }
                Integer epicId = epic.getId();
                subordinates.get(epicId).remove(taskId);
                var listOfSubs = subordinates.get(epicId);
                if (listOfSubs.isEmpty()) {
                    epic.setStatus(Status.NEW);
                    epic.setTimeDefined(false);
                } else {
                    updateEpicTimeWhenDeleteOrUpdateSub(epic, listOfSubs);
                }
                tasks.delete(taskId);
            }
        }
        return taskToDeleted;
    }

    private void updateEpicTimeWhenDeleteOrUpdateSub(Epictask epic, ArrayList<Integer> subIds) {
        LocalDateTime epicStart = epic.getStartTime();
        LocalDateTime epicFinish = epicStart.plus(epic.getDuration());
        LocalDateTime subStart, subFinish;
        Duration subDuration;
        Task sub;
        for (int i = 0; i < subIds.size(); i++) {
            sub = tasks.get(subIds.get(i));
            subStart = sub.getStartTime();
            subDuration = sub.getDuration();
            subFinish = subStart.plus(subDuration);
            if (subStart.isBefore(epicStart)) {
                epicStart = subStart;
            }
            if (subFinish.isAfter(epicFinish)) {
                epicFinish = subFinish;
            }
            epic.setStartTime(epicStart);
            epic.setDuration(Duration.between(epicStart, epicFinish));
        }
    }

    @Override
    public List<Task> getAll() {
        List<Task> copyList = new ArrayList<>();
        for (var task : tasks.values()) {
            copyList.add(task.copy());
        }
        if (copyList.isEmpty()) {
            return Collections.emptyList();
        } else return copyList;
    }

    @Override
    public List<Subtask> getAllSubs(int id) {
        Task task = tasks.get(id);
        List<Subtask> copyList = Collections.emptyList();
        if ((task != null) && (task.getSubordination() == Subordination.EPIC)) {
            Epictask epic = (Epictask) task;
            Integer epicId = epic.getId();
            var subList = subordinates.get(epicId);
            if (!subList.isEmpty()) {
                copyList = new ArrayList<>();
                for (Integer subId : subList) {
                    Subtask subTask = (Subtask) tasks.get(subId);
                    copyList.add(subTask.copy());
                }
            }
        }
        if (copyList.isEmpty()) {
            return Collections.emptyList();
        } else return copyList;
    }

    // Обновление по образцу, содержащемуся в task.
    // Обновлению подлежат только текстовые поля и статус, остальные поля игнорируются.
    @Override
    public Task update(Task task) {
        if ((task == null) || (task.getId() == null)) {
            return null;
        }
        switch (task.getSubordination()) {
            case SELF -> {
                return update((Selftask) task);
            }
            case EPIC -> {
                return update((Epictask) task);
            }
            case SUBTASK -> {
                return update((Subtask) task);
            }
            default -> throw new IllegalArgumentException("Некорректный статус задачи");
        }
    }

    @Override
    public Selftask update(Selftask task) {
        if ((task == null) || (task.getId() == null)) {
            return null;
        }
        Integer id = task.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) {
            return null;
        }

        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        oldTask.setStartTime(task.getStartTime());
        oldTask.setDuration(task.getDuration());
        return (Selftask) oldTask.copy();
    }

    @Override
    public Epictask update(Epictask task) {
        if ((task == null) || (task.getId() == null)) {
            return null;
        }
        Integer id = task.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) {
            return null;
        }

        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        return (Epictask) oldTask.copy();
    }

    @Override
    public Subtask update(Subtask task) {
        if ((task == null) || (task.getId() == null)) {
            return null;
        }
        Integer id = task.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) {
            return null;
        }

        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        oldTask.setStartTime(task.getStartTime());
        oldTask.setDuration(task.getDuration());

        Integer epicId = task.getEpicId();
        Epictask epic = (Epictask) tasks.get(epicId);
        updateEpicTimeWhenDeleteOrUpdateSub(epic, subordinates.get(epicId));

        Status status = task.getStatus();
        switch (status) {
            case Status.NEW -> {
                if (isAllSubsNew(epic)) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
            case Status.IN_PROGRESS -> epic.setStatus(Status.IN_PROGRESS);
            case Status.DONE -> {
                if (isAllSubsDone(epic)) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
            default -> throw new IllegalArgumentException("Некорректный статус задачи");
        }
        return (Subtask) oldTask.copy();
    }

    private boolean isAllSubsNew(Epictask epic) {
        var subList = subordinates.get(epic.getId());
        for (Integer subId : subList) {
            if (tasks.get(subId).getStatus() != Status.NEW) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllSubsDone(Epictask epic) {
        var subList = subordinates.get(epic.getId());
        for (Integer subId : subList) {
            if (tasks.get(subId).getStatus() != Status.DONE) {
                return false;
            }
        }
        return true;
    }
}

