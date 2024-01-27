import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {
    private HashMap<Integer, DeepTask> deepTasks;
    private static int count;

    Manager(HashMap<Integer, DeepTask> deepTasks, int count) {
        this.deepTasks = deepTasks;
        this.count = count;
    }

    //полная очистка
    void clear() {
        deepTasks.clear();
    }

    // добавление
    Task add(Task task) {
        Integer id = generateId();
        Task newTask = null;
        DeepTask deepTask;
        String name = task.name;
        String description = task.description;
        switch (task.type) {
            case Type.EPIC -> {
                deepTask = new DeepTask(Type.EPIC, name, description, Status.NEW, new ArrayList<Integer>(), null);
                deepTasks.put(id, deepTask);
                newTask = new Task(id, Type.EPIC, name, description, Status.NEW, null);
            }
            case Type.STANDARD -> {
                deepTask = new DeepTask(Type.STANDARD, name, description, Status.NEW, null, null);
                deepTasks.put(id, deepTask);
                newTask = new Task(id, Type.STANDARD, name, description, Status.NEW, null);
            }
            case Type.SUB -> {
                Integer epicId = task.epicId;
                DeepTask epic = deepTasks.get(epicId);
                if ((epic != null) && (epic.getType() == Type.EPIC)) {
                    deepTask = new DeepTask(Type.SUB, name, description, Status.NEW, null, epicId);
                    deepTasks.put(id, deepTask);
                    epic.getSubTasks().add(id);
                    newTask = new Task(id, Type.SUB, name, description, Status.NEW, epicId);
                }
            }
            default -> throw new RuntimeException("Неизвестный тип");
        }
        return newTask;
    }

    //    Получение по идентификатору
    Task get(Integer id) {
        DeepTask dt = deepTasks.get(id);
        if (dt == null) return null;
        else return new Task(id, dt.getType(), dt.getName(), dt.getDescription(), dt.getStatus(), dt.getEpicId());
    }

    //удаление по идентификатору.При удалении эпика удаляются все подзадачи
    void delete(Integer id) {
        DeepTask dt = deepTasks.get(id);
        if (dt != null) {
            if (dt.getType() == Type.EPIC) {
                for (Integer subTaskId : dt.getSubTasks()) {
                    deepTasks.remove(subTaskId);
                }
            } else if (dt.getType() == Type.SUB) {
                DeepTask epic = deepTasks.get(dt.getEpicId());
                epic.getSubTasks().remove(id);
                if ((epic.getStatus() != Status.NEW) && (epic.getSubTasks().isEmpty())) epic.setStatus(Status.NEW);
            }
            deepTasks.remove(id);
        }
    }

    //Получение списка всех задач
    List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        for (var entry : deepTasks.entrySet()) {
            Integer id = entry.getKey();
            DeepTask dt = entry.getValue();
            tasks.add(new Task(id, dt.getType(), dt.getName(), dt.getDescription(), dt.getStatus(), dt.getEpicId()));
        }
        return tasks;
    }

    //Получение списка всех подзадач определённого эпика.
    List<Task> getAllSubs(int epicId) {
        ArrayList<Task> subs = null;
        DeepTask epic = deepTasks.get(epicId);
        if ((epic != null) && (!epic.getSubTasks().isEmpty())) {
            subs = new ArrayList<>();
            for (Integer subId : epic.getSubTasks()) {
                DeepTask dt = deepTasks.get(subId);
                subs.add(new Task(subId, dt.getType(), dt.getName(), dt.getDescription(), dt.getStatus(), dt.getEpicId()));
            }
        }
        return subs;
    }

    //Обновление по образцу, содержащемуся в task. Обновлению подлежат только текстовые поля и статус. Остальные поля игнорируются
    Task update(Integer id, Task task) {
        String name = task.name;
        String description = task.description;
        Status status = task.status;
        DeepTask dt = deepTasks.get(id);
        if (dt != null) {
            if (name != null) dt.setName(name);
            if (description != null) dt.setDescription(description);
            if ((status != null) && (dt.getType() != Type.EPIC)) {
                dt.setStatus(status);
                // обновление статуса связанных объектов
                if (dt.getType() == Type.SUB) {
                    DeepTask epic = deepTasks.get(dt.getEpicId());
                    switch (status) {
                        case NEW -> {
                            if (isAllSubNew(dt.getEpicId())) epic.setStatus(Status.NEW);
                            else epic.setStatus(Status.IN_PROGRESS);
                        }
                        case IN_PROGRESS -> epic.setStatus(Status.IN_PROGRESS);
                        case DONE -> {
                            if (isAllSubDone(dt.getEpicId())) epic.setStatus(Status.DONE);
                            else epic.setStatus(Status.IN_PROGRESS);
                        }
                    }
                }
            }
        }
        return get(id);
    }

    private boolean isAllSubNew(Integer epicId) {
        for (Integer subId : deepTasks.get(epicId).getSubTasks()) {
            if (deepTasks.get(subId).getStatus() != Status.NEW) return false;
        }
        return true;
    }

    private boolean isAllSubDone(Integer epicId) {
        for (Integer subId : deepTasks.get(epicId).getSubTasks()) {
            if (deepTasks.get(subId).getStatus() != Status.DONE) return false;
        }
        return true;
    }

    private Integer generateId() {
        return count++;
    }
}
