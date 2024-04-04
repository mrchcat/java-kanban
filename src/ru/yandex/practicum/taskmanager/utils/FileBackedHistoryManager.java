
package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.exceptions.ManagerSaveException;
import ru.yandex.practicum.taskmanager.tasks.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedHistoryManager extends LinkedHashHistoryManager {
    private static final String DELIMITER = "~";
    private static final String HEADER = String.join(DELIMITER, Task.FIELDS_NAMES).concat("\n");
    private final Path file;

    public FileBackedHistoryManager(String path, boolean doLoadFile) {
        file = Path.of(path);
        if (doLoadFile) {
            loadFile();
        } else {
            createNewFile();
        }
    }

    private void createNewFile() {
        try {
            if (Files.isRegularFile(file)) {
                Files.delete(file);
            }
            Files.createFile(file);
            save(Collections.emptyList());
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("The programme was unable to create file %s!", file), e);
        }
    }

    private void loadFile() {
        if (!Files.exists(file)) {
            createNewFile();
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine();
            if ((line == null) || (!line.trim().equals(HEADER.trim()))) {
                throw new ManagerSaveException(
                        String.format("%s is not a task history file or corrupted! Header is absent", file));
            }
            ArrayList<Task> tasks = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(DELIMITER);
                if (elements.length < Task.FIELDS_NAMES.length) {
                    throw new ManagerSaveException(String.format("File %s is corrupted! Not enough fields", file));
                }
                tasks.add(restoreTask(elements));
                for (int i = tasks.size() - 1; i >= 0; i--) {
                    super.add(tasks.get(i));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("File access error for task history %s!", file), e);
        }
    }

    private Task restoreTask(String[] elements) {
        try {
            Integer id = Integer.parseInt(elements[0]);
            Subordination subordination = Subordination.valueOf(elements[1]);
            String name = elements[2];
            Status status = Status.valueOf(elements[3]);
            String description = elements[4];
            boolean isTimeDefined = Boolean.parseBoolean(elements[5]);
            LocalDateTime dateTime = LocalDateTime.of(LocalDate.ofEpochDay(Long.parseLong(elements[6])),
                    LocalTime.ofSecondOfDay(Long.parseLong(elements[7])));
            Duration duration = Duration.ofSeconds(Long.parseLong(elements[8]));
            Task task = switch (subordination) {
                case SELF -> new Selftask(name, description, dateTime, duration);
                case EPIC -> {
                    Epictask epic = new Epictask(name, description);
                    epic.setStartTime(dateTime);
                    epic.setDuration(duration);
                    yield epic;
                }
                case SUBTASK -> {
                    int epicId = Integer.parseInt(elements[9]);
                    yield new Subtask(name, description, dateTime, duration, epicId);
                }
            };
            task.setTimeDefined(isTimeDefined);
            task.setId(id);
            task.setStatus(status);
            return task;
        } catch (Exception e) {
            throw new ManagerSaveException(String.format("File %s is corrupted! Error during unpacking", file), e);
        }
    }

    @Override
    public void add(Task item) {
        super.add(item);
        save(getHistory());
    }

    @Override
    public void remove(int id) {
        super.remove(id);
        save(super.getHistory());
    }

    @Override
    public void clear() {
        super.clear();
        save(super.getHistory());
    }

    private void save(List<Task> tasks) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(HEADER);
            for (Task task : tasks) {
                writer.write(String.join(DELIMITER, task.convertToStringArray()).concat("\n"));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Error writing the task history file %s!", file), e);
        }
    }
}
