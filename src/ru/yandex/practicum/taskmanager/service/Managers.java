package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.repository.InMemoryTreeMap;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.FileBackedHistoryManager;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Managers {
    private static final int START_ID_BY_DEFAULT = 1;
    private static final String PATH_HISTORY_BY_DEFAULT = "src/ru/yandex/practicum/taskmanager/repository/history.scv";
    private static final boolean LOAD_HISTORY_BY_DEFAULT = true;

    public static TaskManager getDefault() {
        Repository<Integer, Task> tasks = new InMemoryMap<>();
        Repository<Integer, ArrayList<Integer>> subordinates = new InMemoryMap<>();
        Generator generator = new SerialGenerator(START_ID_BY_DEFAULT);
        HistoryManager history = getDefaultHistory();
        Repository<LocalDateTime, Integer> starts = new InMemoryTreeMap<>(LocalDateTime::compareTo);
        Repository<LocalDateTime, Integer> finishes = new InMemoryTreeMap<>(LocalDateTime::compareTo);
        return new RegularTaskManager(tasks, subordinates, generator, history, starts, finishes);
    }

    public static HistoryManager getDefaultHistory() {
        return new FileBackedHistoryManager(PATH_HISTORY_BY_DEFAULT, LOAD_HISTORY_BY_DEFAULT);
    }

}
