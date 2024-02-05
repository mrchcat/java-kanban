package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.enums.Status;
import ru.yandex.practicum.taskmanager.enums.Type;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Epictask;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Subtask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.CircularQueue;
import ru.yandex.practicum.taskmanager.utils.Generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO Что возвращать при поступлении ошибочного Id ? Exception, Optional, NULL ?
public class TaskManager {
    private final Repository<Integer, Task> tasks; //хранилище <id задачи, задача>
    private final Repository<Integer, ArrayList<Integer>> subordinates; //хранилище <id эпика, массив id подзадач>
    private final Generator generator; // генератор id
    private final CircularQueue<Task> history;

    public TaskManager(Repository<Integer, Task> tasks,
                       Repository<Integer, ArrayList<Integer>> subordinates,
                       Generator generator, CircularQueue<Task> queue) {
        this.tasks = tasks;
        this.subordinates = subordinates;
        this.generator = generator;
        this.history = queue;
    }

    public List<Task> getHistory() {
        return history.getAll();
    }

    public void clear() {
        tasks.clear();
        subordinates.clear();
    }

    public Selftask add(Selftask task) {
        if (task == null) return null;
        Selftask copy = new Selftask(task.getName(), task.getDescription());
        Integer id = generator.getId();
        copy.setId(id);
        tasks.put(id, copy);
        return (Selftask) get(id);
    }

    public Epictask add(Epictask task) {
        if (task == null) return null;
        Epictask copy = new Epictask(task.getName(), task.getDescription());
        Integer id = generator.getId();
        copy.setId(id);
        tasks.put(id, copy);
        subordinates.put(id, new ArrayList<>());
        return (Epictask) get(id);
    }

    public Subtask add(Subtask task) {
        Integer epicId = task.getEpicId();
        Task someTask = tasks.get(epicId);
        if ((someTask != null) && (someTask.getType() == Type.EPIC)) {
            Subtask copy = new Subtask(task.getName(), task.getDescription(), epicId);
            Integer id = generator.getId();
            copy.setId(id);
            tasks.put(id, copy);
            subordinates.get(epicId).add(id);
            return (Subtask) get(id);
        } else return null;
    }

    public Task get(Integer id) {
        Task original = tasks.get(id);
        Task toReturn = null;
        if (original != null) {
            switch (original.getType()) {
                case Type.SELF -> toReturn = copy((Selftask) original);
                case Type.EPIC -> toReturn = copy((Epictask) original);
                case Type.SUBTASK -> toReturn = copy((Subtask) original);
            }
        }
        history.put(toReturn);
        return toReturn;
    }

    public Epictask getEpic(Integer id) {
        Task task = get(id);
        Epictask epictask = null;
        if (task.getType() == Type.EPIC) {
            epictask = (Epictask) task;
        }
        history.put(epictask);
        return epictask;
    }

    public Selftask getSelftask(Integer id) {
        Task task = get(id);
        Selftask selftask = null;
        if (task.getType() == Type.SELF) {
            selftask = (Selftask) task;
        }
        history.put(selftask);
        return selftask;
    }

    public Subtask getSubtask(Integer id) {
        Task task = get(id);
        Subtask subtask = null;
        if (task.getType() == Type.SUBTASK) {
            subtask = (Subtask) task;
        }
        history.put(subtask);
        return subtask;
    }


    private Selftask copy(Selftask original) {
        if (original != null) {
            Selftask copy = new Selftask(original.getName(), original.getDescription());
            copy.setId(original.getId());
            copy.setStatus(original.getStatus());
            return copy;
        } else return null;
    }

    private Epictask copy(Epictask original) {
        if (original != null) {
            Epictask copy = new Epictask(original.getName(), original.getDescription());
            copy.setId(original.getId());
            copy.setStatus(original.getStatus());
            return copy;
        } else return null;
    }

    private Subtask copy(Subtask original) {
        if (original != null) {
            Subtask copy = new Subtask(original.getName(), original.getDescription(), original.getEpicId());
            copy.setId(original.getId());
            copy.setStatus(original.getStatus());
            return copy;
        } else return null;
    }

    public Task delete(Integer id) {
        Task deleted = null;
        Task task = tasks.get(id);
        if (task != null) {
            deleted = tasks.delete(id);
            switch (task.getType()) {
                case Type.EPIC -> {
                    for (Integer subId : subordinates.get(id)) {
                        delete(subId);
                    }
                }
                case Type.SUBTASK -> {
                    Subtask subtask = (Subtask) task;
                    Epictask epic = (Epictask) tasks.get(subtask.getEpicId());
                    Integer epicId = epic.getId();
                    subordinates.get(epicId).remove(id);
                    if (subordinates.get(epicId).isEmpty()) {
                        epic.setStatus(Status.NEW);
                    }
                }
            }
        }
        return deleted;
    }

    public List<Task> getAll() {
        List<Task> copyList = new ArrayList<>();
        for (var task : tasks.values()) {
            switch (task.getType()) {
                case Type.SELF -> copyList.add(copy((Selftask) task));
                case Type.EPIC -> copyList.add(copy((Epictask) task));
                case Type.SUBTASK -> copyList.add(copy((Subtask) task));
            }
        }
        if (copyList.isEmpty()) return Collections.emptyList();
        else return copyList;
    }

    public List<Subtask> getAllSubs(int id) {
        Task task = tasks.get(id);
        List<Subtask> copyList = Collections.emptyList();
        if ((task != null) && (task.getType() == Type.EPIC)) {
            Epictask epic = (Epictask) task;
            Integer epicId = epic.getId();
            var subList = subordinates.get(epicId);
            if (!subList.isEmpty()) {
                copyList = new ArrayList<>();
                for (Integer subId : subList) {
                    Subtask subTask = (Subtask) tasks.get(subId);
                    copyList.add(copy(subTask));
                }
            }
        }
        if (copyList.isEmpty()) return Collections.emptyList();
        else return copyList;
    }

    // Обновление по образцу, содержащемуся в task.
    // Обновлению подлежат только текстовые поля и статус, остальные поля игнорируются.
    public Selftask update(Selftask task) {
        if ((task == null) || (task.getId() == null)) return null;
        Integer id = task.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) return null;

        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        return (Selftask) get(id);
    }

    public Epictask update(Epictask task) {
        if ((task == null) || (task.getId() == null)) return null;
        Integer id = task.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) return null;

        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        return (Epictask) get(id);
    }

    public Subtask update(Subtask task) {
        if ((task == null) || (task.getId() == null)) return null;
        Integer id = task.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) return null;

        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());

        Integer epicId = task.getEpicId();
        Epictask epic = (Epictask) tasks.get(epicId);
        Status status = task.getStatus();
        switch (status) {
            case Status.NEW -> {
                if (isAllSubsNew(epic)) epic.setStatus(Status.NEW);
                else epic.setStatus(Status.IN_PROGRESS);
            }
            case Status.IN_PROGRESS -> epic.setStatus(Status.IN_PROGRESS);
            case Status.DONE -> {
                if (isAllSubsDone(epic)) epic.setStatus(Status.DONE);
                else epic.setStatus(Status.IN_PROGRESS);
            }
        }
        return (Subtask) get(id);
    }

    private boolean isAllSubsNew(Epictask epic) {
        var subList = subordinates.get(epic.getId());
        for (Integer subId : subList) {
            if (tasks.get(subId).getStatus() != Status.NEW) return false;
        }
        return true;
    }

    private boolean isAllSubsDone(Epictask epic) {
        var subList = subordinates.get(epic.getId());
        for (Integer subId : subList) {
            if (tasks.get(subId).getStatus() != Status.DONE) return false;
        }
        return true;
    }
}

