package ru.yandex.practicum.taskmanager.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.exceptions.ManagerSaveException;
import ru.yandex.practicum.taskmanager.tasks.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedHistoryManagerTest {
    String filePath;
    Path path;
    static LocalDateTime dateTime = LocalDateTime.of(2024, 04, 01, 13, 20);
    static Duration duration = Duration.ofDays(3);
    String[] FIELDS = {"id", "subordination", "name", "status", "description", "isTimeDefined",
            "startdate", "starttime", "duration", "epicId"};
    String HEADER = String.join(Task.DELIMITER, FIELDS).concat("\n");

    @BeforeEach
    void initAndClearBefore() throws IOException {
        filePath = "src/ru/yandex/practicum/taskmanager/repository/history.txt";
        path = Path.of(filePath);
        Files.deleteIfExists(path);
    }

    @AfterEach
    void clearAfter() throws IOException {
        Files.deleteIfExists(path);
    }


    @DisplayName("create new file")
    @Test
    void createNewFileTest() {
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(filePath, false);
        assertTrue(Files.isRegularFile(path));
    }

    @DisplayName("create the same file twice")
    @Test
    void createNewFileTwiceTest() {
        FileBackedHistoryManager historyManager1 = new FileBackedHistoryManager(filePath, false);
        assertTrue(Files.isRegularFile(path));
    }

    @DisplayName("try to create file with bad name")
    @Test
    void createBadFileTest() {
        String badFileName = "src/ru/yandex/practicum/taskmanager/repository/noSuchDirectory/noSuchFIle";
        assertThrows(ManagerSaveException.class, () -> new FileBackedHistoryManager(badFileName, false));
    }

    @DisplayName("check content of new file")
    @Test
    void createAndCheckNewTest() throws IOException {
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(filePath, false);
        assertTrue(Files.isRegularFile(path));
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            assertAll(
                    () -> assertEquals(HEADER.trim(), line1.trim()),
                    () -> assertNull(line2)
            );
        }
    }

    @DisplayName("create and add Selftasks ")
    @Test
    void createAndAddSelftasksTest() throws IOException {
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(filePath, false);
        assertTrue(Files.isRegularFile(path));
        String answer = fillInHistoryManagerBySelftasks(historyManager);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String fileData = Files.readString(path);
            assertEquals(answer, fileData);
        }
    }

    String fillInHistoryManagerBySelftasks(HistoryManager historyManager) {
        Task task1 = new Selftask("name1", "desc1",
                LocalDateTime.of(1, 1, 1, 1, 1),
                Duration.ofDays(1));
        task1.setId(1);
        Task task2 = new Selftask("name2", "desc2",
                LocalDateTime.of(2, 2, 2, 2, 2),
                Duration.ofDays(2));
        task2.setId(2);
        Task task3 = new Selftask("name3", "desc3",
                LocalDateTime.of(3, 3, 3, 3, 3),
                Duration.ofDays(3));
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        String answer = """
                3~SELF~name3~NEW~desc3~true~-718371~10980~259200~null
                2~SELF~name2~NEW~desc2~true~-718765~7320~172800~null
                1~SELF~name1~NEW~desc1~true~-719162~3660~86400~null
                """;
        return HEADER + answer;
    }

    @DisplayName("create and add Epictasks ")
    @Test
    void createAndAddEpictasks() throws IOException {
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(filePath, false);
        assertTrue(Files.isRegularFile(path));
        String answer = fillInHistoryManagerByEpictasks(historyManager);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String fileData = Files.readString(path);
            assertEquals(answer, fileData);
        }
    }

    String fillInHistoryManagerByEpictasks(HistoryManager historyManager) {
        Task task1 = new Epictask("name1", "desc1");
        task1.setId(1);
        Task task2 = new Epictask("name2", "desc2");
        task2.setId(2);
        Task task3 = new Epictask("name3", "desc3");
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        String answer = """
                3~EPIC~name3~NEW~desc3~false~365241780471~86399~0~null
                2~EPIC~name2~NEW~desc2~false~365241780471~86399~0~null
                1~EPIC~name1~NEW~desc1~false~365241780471~86399~0~null
                """;
        return HEADER + answer;
    }

    @DisplayName("create and add Subtasks ")
    @Test
    void createAndAddSubtasksTest() throws IOException {
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(filePath, false);
        assertTrue(Files.isRegularFile(path));
        String answer = fillInHistoryManagerBySubtasks(historyManager);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String fileData = Files.readString(path);
            assertEquals(answer, fileData);
        }
    }

    String fillInHistoryManagerBySubtasks(HistoryManager historyManager) {
        Task task1 = new Subtask("name1", "desc1",
                LocalDateTime.of(1, 1, 1, 1, 1),
                Duration.ofDays(1), 1);
        task1.setId(1);
        Task task2 = new Subtask("name2", "desc2",
                LocalDateTime.of(2, 2, 2, 2, 2),
                Duration.ofDays(2), 2);
        task2.setId(2);
        Task task3 = new Subtask("name3", "desc3",
                LocalDateTime.of(3, 3, 3, 3, 3),
                Duration.ofDays(3), 3);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        String answer = """
                3~SUBTASK~name3~NEW~desc3~true~-718371~10980~259200~3
                2~SUBTASK~name2~NEW~desc2~true~-718765~7320~172800~2
                1~SUBTASK~name1~NEW~desc1~true~-719162~3660~86400~1
                """;
        return HEADER + answer;
    }

    @DisplayName("create and add same tasks  ")
    @Test
    void createAndAddSameTasksTest() throws IOException {
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(filePath, false);
        assertTrue(Files.isRegularFile(path));
        String answer = fillInHistoryManagerBySameTasks(historyManager);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String fileData = Files.readString(path);
            assertEquals(answer, fileData);
        }
    }

    String fillInHistoryManagerBySameTasks(HistoryManager historyManager) {
        ArrayList<Task> list = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            Task task = new Subtask("name" + i, "desc" + i, dateTime, duration, i);
            task.setId(i);
            list.add(task);
            historyManager.add(task);
        }
        historyManager.add(list.get(1));
        historyManager.add(list.get(3));
        String answer = """
                3~SUBTASK~name3~NEW~desc3~true~19814~48000~259200~3
                1~SUBTASK~name1~NEW~desc1~true~19814~48000~259200~1
                5~SUBTASK~name5~NEW~desc5~true~19814~48000~259200~5
                4~SUBTASK~name4~NEW~desc4~true~19814~48000~259200~4
                2~SUBTASK~name2~NEW~desc2~true~19814~48000~259200~2
                0~SUBTASK~name0~NEW~desc0~true~19814~48000~259200~0
                """;
        return HEADER + answer;
    }

    @DisplayName("load existing file")
    @Test
    void loadExistingFileTest() throws IOException {
        String fileContent = HEADER + """
                9~SUBTASK~name2~NEW~desc2~true~19814~48000~259200~8
                8~EPIC~name2~NEW~desc2~false~19814~48000~259200~null
                7~SUBTASK~name1~NEW~desc1~true~19814~48000~259200~6
                6~EPIC~name1~IN_PROGRESS~desc1~true~19814~48000~259200~null
                5~SUBTASK~name9~NEW~desc9~true~19814~48000~259200~4
                4~EPIC~name0~NEW~desc0~false~19814~48000~259200~null
                3~SELF~name2~NEW~desc2~true~19814~48000~259200~null
                2~SELF~name1~NEW~desc1~true~19814~48000~259200~null
                1~SELF~name0~DONE~desc0~true~19814~48000~259200~null
                """;
        Files.writeString(path, fileContent, CREATE);
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(filePath, true);
        List<Task> history = historyManager.getHistory();
        assertEquals(9, history.size());
        assertAll(
                () -> assertEquals(9, history.get(0).getId()),
                () -> assertEquals(Subordination.SUBTASK, history.get(0).getSubordination()),
                () -> assertEquals("name2", history.get(0).getName()),
                () -> assertEquals(Status.NEW, history.get(0).getStatus()),
                () -> assertEquals(8, ((Subtask) history.get(0)).getEpicId()),
                () -> assertEquals("desc2", history.get(0).getDescription()),
                () -> assertEquals(LocalDateTime.of(2024, 04, 01, 13, 20), history.get(0).getStartTime()),
                () -> assertEquals(Duration.ofDays(3), history.get(0).getDuration())
        );
        assertAll(
                () -> assertEquals(6, history.get(3).getId()),
                () -> assertEquals(Subordination.EPIC, history.get(3).getSubordination()),
                () -> assertEquals("name1", history.get(3).getName()),
                () -> assertEquals("desc1", history.get(3).getDescription()),
                () -> assertEquals(Status.IN_PROGRESS, history.get(3).getStatus()),
                () -> assertTrue(((Epictask) history.get(3)).isTimeDefined()),
                () -> assertEquals(LocalDateTime.of(2024, 04, 01, 13, 20), history.get(3).getStartTime()),
                () -> assertEquals(Duration.ofDays(3), history.get(3).getDuration())
        );
        assertAll(
                () -> assertEquals(1, history.get(8).getId()),
                () -> assertEquals(Subordination.SELF, history.get(8).getSubordination()),
                () -> assertEquals("name0", history.get(8).getName()),
                () -> assertEquals("desc0", history.get(8).getDescription()),
                () -> assertEquals(Status.DONE, history.get(8).getStatus()),
                () -> assertEquals(LocalDateTime.of(2024, 04, 01, 13, 20), history.get(1).getStartTime()),
                () -> assertEquals(Duration.ofDays(3), history.get(1).getDuration())
        );
    }

    @DisplayName("load if header absent")
    @Test
    void loadIfHeaderAbsentTest() throws IOException {
        Files.createFile(path);
        assertThrows(ManagerSaveException.class, () -> new FileBackedHistoryManager(filePath, true));
    }

    @DisplayName("load if file is absent")
    @Test
    void loadIfFileAbsentTest() {
        HistoryManager historyManager = new FileBackedHistoryManager(filePath, true);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @DisplayName("load if bad header ")
    @Test
    void loadIfBadHeaderAbsentTest() throws IOException {
        String fileContent = "id,subordination,name1,status,description,epicId";
        Files.writeString(path, fileContent, CREATE);
        assertThrows(ManagerSaveException.class, () -> new FileBackedHistoryManager(filePath, true));
    }

    @DisplayName("load if bad task ")
    @Test
    void loadIfBadTaskTest() throws IOException {
        String fileContent = HEADER + """
                9,SUBTASK,name2,NEW,desc2,8
                8,EЬЗШС,name2,NEW,desc2,null 
                7,SUBTASK,name1,NEW,desc1,6
                6,EPIC,name1,IN_PROGRESS,desc1,null
                5,SUBTASK,name0,NEW,desc0,4
                4,EPIC,name0,NEW,desc0,null
                3,SELF,name2,NEW,desc2,null
                2,SELF,name1,NEW,desc1,null
                1,SELF,name0,DONE,desc0,null
                """;
        Files.writeString(path, fileContent, CREATE);
        assertThrows(ManagerSaveException.class, () -> new FileBackedHistoryManager(filePath, true));
    }
}