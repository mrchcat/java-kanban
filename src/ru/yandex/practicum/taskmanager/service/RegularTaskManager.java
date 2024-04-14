package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.*;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class RegularTaskManager implements TaskManager {
    private final Repository<Integer, Task> tasks;
    private final Repository<Integer, ArrayList<Integer>> subordinates;
    private final Generator generator;
    private final HistoryManager history;
    private final Repository<LocalDateTime, Integer> starts;
    private final Repository<LocalDateTime, Integer> finishes;

    public RegularTaskManager(Repository<Integer, Task> tasks,
                              Repository<Integer, ArrayList<Integer>> subordinates,
                              Generator generator,
                              HistoryManager history,
                              Repository<LocalDateTime, Integer> starts,
                              Repository<LocalDateTime, Integer> finishes
    ) {
        this.tasks = tasks;
        this.subordinates = subordinates;
        this.generator = generator;
        this.history = history;
        this.starts = starts;
        this.finishes = finishes;
    }

    private void addToTimeline(Task task) {
        Integer id = task.getId();
        LocalDateTime start = task.getStartTime();
        starts.put(start, id);
        LocalDateTime finish = start.plus(task.getDuration());
        finishes.put(finish, id);
    }

    private void deleteFromTimeline(Task task) {
        LocalDateTime start = task.getStartTime();
        starts.delete(start);
        LocalDateTime finish = start.plus(task.getDuration());
        finishes.delete(finish);
    }

    public List<Task> getPrioritizedTasks() {
        if (starts.isEmpty()) return Collections.emptyList();
        else return starts.values().stream().map(id -> tasks.get(id).copy()).toList();
    }

    private boolean isIntersectedByTime(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime finish = start.plus(task.getDuration());
        var startBeforeTaskEnds = starts.headMap(finish);
        if (startBeforeTaskEnds.isEmpty()) return false;

        var finishAfterTaskStarts = finishes.tailMap(start, false);
        if (finishAfterTaskStarts.isEmpty()) return false;

        HashSet<Integer> set = new HashSet<>(finishAfterTaskStarts.values());
        return startBeforeTaskEnds.values().stream().anyMatch(set::contains);
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
        starts.clear();
    }

    @Override
    public Selftask add(Selftask task) {
        if (task == null) {
            return null;
        }
        boolean isTimeDefined = task.isTimeDefined();
        if (isTimeDefined && isIntersectedByTime(task)) {
            return null;
        }
        Selftask copy = task.copy();
        Integer id = generator.getId();
        copy.setId(id);
        copy.setStatus(Status.NEW);
        tasks.put(id, copy);
        if (isTimeDefined) {
            addToTimeline(copy);
        }
        return copy.copy();
    }

    @Override
    public Epictask add(Epictask task) {
        if (task == null) {
            return null;
        }
        Epictask copy = task.copy();
        Integer id = generator.getId();
        copy.setId(id);
        copy.setStatus(Status.NEW);
        tasks.put(id, copy);
        subordinates.put(id, new ArrayList<>());
        return copy.copy();
    }

    @Override
    public Subtask add(Subtask task) {
        if (task == null) {
            return null;
        }
        boolean isTimeDefined = task.isTimeDefined();
        if (isTimeDefined && isIntersectedByTime(task)) {
            return null;
        }
        Integer epicId = task.getEpicId();
        Task epicTask = tasks.get(epicId);
        if ((epicTask == null) || (epicTask.getSubordination() != Subordination.EPIC)) {
            return null;
        }
        Subtask copy = task.copy();
        Integer id = generator.getId();
        copy.setId(id);
        copy.setStatus(Status.NEW);
        tasks.put(id, copy);
        subordinates.get(epicId).add(id);
        if (isTimeDefined) {
            addToTimeline(copy);
            updateEpicTimeWhenAddSub((Epictask) epicTask, task.getStartTime(), task.getDuration());
        }
        return copy.copy();
    }

    private void updateEpicTimeWhenAddSub(Epictask epic, LocalDateTime subStart, Duration subDuration) {
        if (!epic.isTimeDefined()) {
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
            case SELF -> {
                tasks.delete(taskId);
                deleteFromTimeline(taskToDeleted);
            }
            case Subordination.EPIC -> {
                subordinates.get(taskId).forEach(tasks::delete);
                subordinates.delete(taskId);
                tasks.delete(taskId);
            }
            case Subordination.SUBTASK -> {
                Subtask subtaskToDelete = (Subtask) taskToDeleted;
                Epictask epic = (Epictask) tasks.get(subtaskToDelete.getEpicId());
                Integer epicId = epic.getId();
                if (epic.getSubordination() != Subordination.EPIC) {
                    throw new IllegalArgumentException("Incorrect EpicId");
                }
                if (!subordinates.get(epicId).contains(subtaskToDelete.getId())) {
                    throw new IllegalArgumentException("Subtask is absent in the list of Epic task");
                }
                tasks.delete(taskId);
                var listOfSubs = subordinates.get(epicId);
                listOfSubs.remove(taskId);
                if (listOfSubs.isEmpty()) {
                    epic.setStatus(Status.NEW);
                }
                if (taskToDeleted.isTimeDefined()) {
                    updateEpicTimeWhenDeleteUpdateSub(epic, listOfSubs);
                    deleteFromTimeline(taskToDeleted);
                }
            }
        }
        return taskToDeleted;
    }

    private void updateEpicTimeWhenDeleteUpdateSub(Epictask epic, ArrayList<Integer> subIds) {
        if (subIds.isEmpty()) {
            epic.switchOffTime();
            return;
        }
        Task first = tasks.get(subIds.getFirst());
        LocalDateTime minStart = first.getStartTime();
        LocalDateTime maxFinish = minStart.plus(first.getDuration());
        for (int i = 1; i < subIds.size(); i++) {
            Task sub = tasks.get(subIds.get(i));
            LocalDateTime subStart = sub.getStartTime();
            Duration subDuration = sub.getDuration();
            LocalDateTime subFinish = subStart.plus(subDuration);
            if (subStart.isBefore(minStart)) {
                minStart = subStart;
            }
            if (subFinish.isAfter(maxFinish)) {
                maxFinish = subFinish;
            }
        }
        LocalDateTime epicStart = epic.getStartTime();
        if (!epicStart.isEqual(minStart)) {
            epicStart = minStart;
        }
        LocalDateTime epicFinish = epicStart.plus(epic.getDuration());
        if (!epicFinish.isEqual(maxFinish)) {
            epicFinish = maxFinish;
        }
        Duration epicDuration = Duration.between(epicStart, epicFinish);
        epic.setStartTime(epicStart);
        epic.setDuration(epicDuration);
    }

    @Override
    public List<Task> getAll() {
        return tasks.values().stream().map(Task::copy).toList();
    }

    @Override
    public List<Subtask> getAllSubs(int id) {
        Task epic = tasks.get(id);
        if ((epic == null) || (epic.getSubordination() != Subordination.EPIC)) {
            return Collections.emptyList();
        }
        return subordinates.get(epic.getId()).stream()
                .map(u -> (Subtask) tasks.get(u))
                .map(Subtask::copy)
                .toList();
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
        Task oldTask = tasks.get(task.getId());
        if (oldTask == null) {
            return null;
        }
        if (!isTaskIntersectedWhenUpdate(oldTask, task)) {
            return null;
        }
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        oldTask.setStartTime(task.getStartTime());
        oldTask.setDuration(task.getDuration());
        return (Selftask) oldTask.copy();
    }

    private boolean isTaskIntersectedWhenUpdate(Task oldTask, Task task) {
        boolean oldDefined = oldTask.isTimeDefined();
        boolean newDefined = task.isTimeDefined();
        LocalDateTime oldStart = oldTask.getStartTime();
        LocalDateTime newStart = task.getStartTime();
        Duration oldDuration = oldTask.getDuration();
        Duration newDuration = task.getDuration();

        if (oldDefined) {
            if (newDefined) {
                if ((!oldStart.isEqual(newStart)) || (!oldDuration.equals(newDuration))) {
                    deleteFromTimeline(oldTask);
                    if (isIntersectedByTime(task)) {
                        addToTimeline(oldTask);
                        return false;
                    }
                    addToTimeline(task);
                }
            } else {
                deleteFromTimeline(oldTask);
            }
        } else {
            if (newDefined) {
                if (isIntersectedByTime(task)) {
                    return false;
                } else addToTimeline(task);
            }
        }
        return true;
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
        if (!isTaskIntersectedWhenUpdate(oldTask, task)) {
            return null;
        }

        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        oldTask.setStartTime(task.getStartTime());
        oldTask.setDuration(task.getDuration());

        Integer epicId = task.getEpicId();
        Epictask epic = (Epictask) tasks.get(epicId);
        updateEpicTimeWhenDeleteUpdateSub(epic, subordinates.get(epicId));

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

    public List<Task> getAllSelftasks() {
        return getAll().stream()
                .filter(u -> u.getSubordination() == Subordination.SELF)
                .toList();
    }

    public List<Task> getAllSubtasks() {
        return getAll().stream()
                .filter(u -> u.getSubordination() == Subordination.SUBTASK)
                .toList();
    }

    public List<Task> getAllEpictasks() {
        return getAll().stream()
                .filter(u -> u.getSubordination() == Subordination.EPIC)
                .toList();
    }

    private boolean isAllSubsNew(Epictask epic) {
        return subordinates.get(epic.getId()).stream().map(u -> tasks.get(u).getStatus()).allMatch(Status.NEW::equals);
    }

    private boolean isAllSubsDone(Epictask epic) {
        return subordinates.get(epic.getId()).stream().map(u -> tasks.get(u).getStatus()).allMatch(Status.DONE::equals);
    }
}

