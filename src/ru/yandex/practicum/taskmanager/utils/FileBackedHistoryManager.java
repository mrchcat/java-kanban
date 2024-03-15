package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.exceptions.ManagerSaveException;
import ru.yandex.practicum.taskmanager.tasks.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.yandex.practicum.taskmanager.tasks.Subordination.SUBTASK;

public class FileBackedHistoryManager extends LinkedHashHistoryManager {

    private final String header = "id,subordination,name,status,description,epicId\n";
    private final int numberOfFields = 6;
    private final Path file;

    public FileBackedHistoryManager(String path, boolean doLoadFile) {
        file = Path.of(path);
        if (doLoadFile) loadFile();
        else createNewFile();
    }

    private void createNewFile() {
        try {
            if (Files.isRegularFile(file)) {
                Files.delete(file);
            }
            Files.createFile(file);
            save(Collections.emptyList());
        } catch (IOException e) {
            String message = String.format("Ошибка при создании файла истории задач %s!", file);
            throw new ManagerSaveException(message, e);
        }
    }

    private void loadFile() {
        if (!Files.isRegularFile(file)) {
            createNewFile();
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine();
            if ((line == null) || (!line.trim().equals(header.trim()))) {
                String message = String.format("%s не является файлом истории задач или поврежден!", file);
                throw new ManagerSaveException(message);
            }
            ArrayList<Task> tasks = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                if (elements.length < numberOfFields) {
                    String message = String.format("Файл %s поврежден!", file);
                    throw new ManagerSaveException(message);
                }
                tasks.add(restoreTask(elements));
                for (int i = tasks.size() - 1; i >= 0; i--) {
                    super.add(tasks.get(i));
                }
            }
        } catch (IOException e) {
            String message = String.format("Ошибка доступа к файлу истории задач %s!", file);
            throw new ManagerSaveException(message, e);
        }
    }

    private Task restoreTask(String[] elements) {
        try {
            Integer id = Integer.parseInt(elements[0]);
            Subordination subordination = Subordination.valueOf(elements[1]);
            String name = elements[2];
            Status status = Status.valueOf(elements[3]);
            String description = elements[4];
            int epicId;
            Task task = switch (subordination) {
                case SELF -> new Selftask(name, description);
                case EPIC -> new Epictask(name, description);
                case SUBTASK -> {
                    epicId = Integer.parseInt(elements[5]);
                    yield new Subtask(name, description, epicId);
                }
            };
            task.setId(id);
            task.setStatus(status);
            return task;
        } catch (Exception e) {
            String message = String.format("Файл %s поврежден", file);
            throw new ManagerSaveException(message, e);
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
            writer.write(header);
            for (Task task : tasks) {
                writer.write(convertTasktoString(task) + "\n");
            }
        } catch (IOException e) {
            String message = String.format("Ошибка при записи файла истории задач %s!", file);
            throw new ManagerSaveException(message, e);
        }
    }

    private String convertTasktoString(Task task) {
        return String.join(",",
                task.getId().toString(),
                task.getSubordination().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                (task.getSubordination() == SUBTASK) ? ((Subtask) task).getEpicId().toString() : "null");
    }

}
