package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.repository.InMemoryTreeMap;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.FileBackedHistoryManager;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class Managers {
    private static final int START_ID_BY_DEFAULT = 1;
    private static final Path PATH_HISTORY_BY_DEFAULT = Path.of("src", "ru", "yandex", "practicum", "taskmanager",
            "repository", "history.scv");
    private static final boolean LOAD_HISTORY_BY_DEFAULT = true;

    private static final Path configPath = Path.of("src", "ru", "yandex", "practicum",
            "taskmanager", "service", "config.properties");


    private static TaskManager get(int startId, Path pathHistory, boolean loadHistory) {
        Repository<Integer, Task> tasks = new InMemoryMap<>();
        Repository<Integer, ArrayList<Integer>> subordinates = new InMemoryMap<>();
        Generator generator = new SerialGenerator(startId);
        HistoryManager history = getHistory(pathHistory, loadHistory);
        Repository<LocalDateTime, Integer> starts = new InMemoryTreeMap<>(LocalDateTime::compareTo);
        Repository<LocalDateTime, Integer> finishes = new InMemoryTreeMap<>(LocalDateTime::compareTo);
        return new RegularTaskManager(tasks, subordinates, generator, history, starts, finishes);
    }

    public static TaskManager getDefault() {
        return get(START_ID_BY_DEFAULT, PATH_HISTORY_BY_DEFAULT, LOAD_HISTORY_BY_DEFAULT);
    }

    public static TaskManager getFromConfig() {
        Properties config = new Properties();
        try (InputStream input = Files.newInputStream(configPath)) {
            config.load(input);
        } catch (IOException e) {
            return getDefault();
        }
        int startId;
        String start = config.getProperty("startId", String.valueOf(START_ID_BY_DEFAULT));
        try {
            startId = Integer.parseInt(start);
        } catch (NumberFormatException e) {
            startId = START_ID_BY_DEFAULT;
        }
        String pathHistoryString = config.getProperty("pathHistory", PATH_HISTORY_BY_DEFAULT.toString());
        Path pathHistory = Path.of(pathHistoryString);

        String loadHistoryString = config.getProperty("loadHistory", String.valueOf(LOAD_HISTORY_BY_DEFAULT));
        boolean loadHistory = Boolean.parseBoolean(loadHistoryString);
        return get(startId, pathHistory, loadHistory);
    }

    public static HistoryManager getHistory(Path pathHistory, boolean loadHistory) {
        return new FileBackedHistoryManager(pathHistory, loadHistory);
    }

}
