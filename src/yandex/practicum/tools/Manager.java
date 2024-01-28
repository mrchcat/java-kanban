package yandex.practicum.tools;

import yandex.practicum.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Manager {
    private final HashMap<Integer, Task> tasks; //id таска - сам таск
    private final HashMap<Integer, ArrayList<Integer>> subordinates; //id эпика - id подчиненной задачи

    private static int count; //номера тасков

    public Manager() {
        this.tasks = new HashMap<>();
        this.subordinates = new HashMap<>();
        this.count = 1;
    }

    public void clear() {
        tasks.clear();
    }

    private Integer generateId() {
        return count++;
    }

    public Selftask add(Selftask task) {
        if (task == null) return null;
        Selftask copy = new Selftask(task.getName(), task.getDescription());
        Integer id = generateId();
        copy.setId(id);
        tasks.put(id, copy);
        return (Selftask) get(id);
    }

    public Epictask add(Epictask task) {
        if (task == null) return null;
        Epictask copy = new Epictask(task.getName(), task.getDescription());
        Integer id = generateId();
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
            Integer id = generateId();
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
        return toReturn;
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
            deleted = tasks.remove(id);
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
        return copyList;
    }

    public List<Subtask> getAllSubs(int id) {
        Task task = tasks.get(id);
        List<Subtask> copyList = null;
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
        return copyList;
    }

//Обновление по образцу, содержащемуся в task. Обновлению подлежат только текстовые поля и статус.
// Остальные поля игнорируются

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
                if (isAllSubNew(epic)) epic.setStatus(Status.NEW);
                else epic.setStatus(Status.IN_PROGRESS);
            }
            case Status.IN_PROGRESS -> epic.setStatus(Status.IN_PROGRESS);
            case Status.DONE -> {
                if (isAllSubDone(epic)) epic.setStatus(Status.DONE);
                else epic.setStatus(Status.IN_PROGRESS);
            }
        }
        return (Subtask) get(id);
    }

    private boolean isAllSubNew(Epictask epic) {
        var subList = subordinates.get(epic.getId());
        for (Integer subId : subList) {
            if (tasks.get(subId).getStatus() != Status.NEW) return false;
        }
        return true;
    }

    private boolean isAllSubDone(Epictask epic) {
        var subList = subordinates.get(epic.getId());
        for (Integer subId : subList) {
            if (tasks.get(subId).getStatus() != Status.DONE) return false;
        }
        return true;
    }

}

