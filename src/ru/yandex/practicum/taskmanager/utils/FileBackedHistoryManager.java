
package ru.yandex.practicum.taskmanager.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
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
    private final Path file;
    private final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(Task.FIELDS_NAMES)
            .setSkipHeaderRecord(false).build();

    public FileBackedHistoryManager(Path file, boolean doLoadFile) {
        this.file = file;
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

    private boolean isHeaderCorrupted(CSVRecord record) {
        int size = record.size();
        if (size != Task.FIELDS_NAMES.length) {
            return true;
        }
        for (int i = 0; i < size; i++) {
            if (!record.get(i).trim().equals(Task.FIELDS_NAMES[i])) {
                return true;
            }
        }
        return false;
    }

    private void loadFile() {
        if (!Files.exists(file)) {
            createNewFile();
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            ArrayList<Task> tasks = new ArrayList<>();
            CSVParser parser = new CSVParser(reader, csvFormat);
            List<CSVRecord> records = parser.getRecords();
            if (records.isEmpty() || isHeaderCorrupted(records.getFirst())) {
                throw new ManagerSaveException(
                        String.format("%s is not a task history file or corrupted! Header is absent", file));
            }
            for (int i = 1; i < records.size(); i++) {
                CSVRecord record = records.get(i);
                if (!record.isConsistent()) {
                    throw new ManagerSaveException(
                            String.format("File %s is corrupted! Number of fields differ from origin", file));
                }
                tasks.add(restoreTask(record));
            }
            for (int i = tasks.size() - 1; i >= 0; i--) {
                super.add(tasks.get(i));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("File access error for task history %s!", file), e);
        }
    }

    private Task restoreTask(CSVRecord record) {
        try {
            Integer id = Integer.parseInt(record.get(0));
            Subordination subordination = Subordination.valueOf(record.get(1));
            String name = record.get(2);
            Status status = Status.valueOf(record.get(3));
            String description = record.get(4);
            LocalDateTime dateTime;
            if (record.get(5).isEmpty()) {
                dateTime = null;
            } else {
                dateTime = LocalDateTime.of(LocalDate.ofEpochDay(Long.parseLong(record.get(5))),
                        LocalTime.ofSecondOfDay(Long.parseLong(record.get(6))));
            }
            Duration duration = (record.get(7).isEmpty()) ? null : Duration.ofSeconds(Long.parseLong(record.get(7)));
            Task task = switch (subordination) {
                case SELF -> new Selftask(name, description, dateTime, duration);
                case EPIC -> {
                    Epictask epic = new Epictask(name, description);
                    epic.setStartTime(dateTime);
                    epic.setDuration(duration);
                    yield epic;
                }
                case SUBTASK -> {
                    int epicId = Integer.parseInt(record.get(8));
                    yield new Subtask(name, description, dateTime, duration, epicId);
                }
            };
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
            CSVPrinter printer = new CSVPrinter(writer, csvFormat);
            for (Task task : tasks) {
                printer.printRecord(task.convertToObjectArray());
            }
            printer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Error writing the task history file %s!", file), e);
        }
    }
}
