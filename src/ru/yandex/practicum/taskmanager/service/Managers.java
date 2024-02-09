package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryArray;
import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.CircularQueue;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {
    private static int HISTORY_SIZE = 10;
    private static int START_ID_BY_DEFAULT = 1;

    public Managers() {
    }

    public Managers(int history_size, int start_id) {
        this.HISTORY_SIZE = history_size;
        this.START_ID_BY_DEFAULT = start_id;
    }

    public static TaskManager getDefault() {
        var tasks = new InMemoryMap<Integer, Task>();
        var subordinates = new InMemoryMap<Integer, ArrayList<Integer>>();
        var generator = new SerialGenerator(START_ID_BY_DEFAULT);
        var history = getDefaultHistory();
        return new RegularTaskManager(tasks, subordinates, generator, history);
    }

    public static HistoryManager getDefaultHistory() {
        Repository repositoryForHistoryManager = new InMemoryArray<Integer, Task>(HISTORY_SIZE);
        return new CircularQueue<>(HISTORY_SIZE, repositoryForHistoryManager);
    }
}
