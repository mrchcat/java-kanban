package service;

import tasks.Epictask;
import tasks.Selftask;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    void clear();

    Selftask add(Selftask task);

    Epictask add(Epictask task);

    Subtask add(Subtask task);

    Task get(Integer id);

    Epictask getEpic(Integer id);

    Selftask getSelftask(Integer id);

    Subtask getSubtask(Integer id);

    Task delete(Integer id);

    List<Task> getAll();

    List<Subtask> getAllSubs(int id);

    // Обновление по образцу, содержащемуся в task.
    // Обновлению подлежат только текстовые поля и статус, остальные поля игнорируются.
    Selftask update(Selftask task);

    Epictask update(Epictask task);

    Subtask update(Subtask task);

    Task update(Task task);
}
