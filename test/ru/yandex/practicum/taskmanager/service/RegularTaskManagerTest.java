package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.repository.InMemoryTreeMap;
import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.*;
import ru.yandex.practicum.taskmanager.utils.Generator;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.taskmanager.service.Managers.getDefaultHistory;

class RegularTaskManagerTest {
    TaskManager taskManager;
    static LocalDateTime startDateTime = LocalDateTime.of(2024, 04, 01, 13, 20);
    static Duration duration = Duration.ofDays(3);

    static Stream<Selftask> getSelfTasks() {
        return Stream.of(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime, duration),
                new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration),
                new Selftask("скачать сериал", "Игра престолов", startDateTime, duration),
                new Selftask("работать", "работу", startDateTime, duration),
                new Selftask("смотреть на закат", "и на рассвет", startDateTime, duration)
        );
    }

    static Stream<Epictask> getEpicTasks() {
        return Stream.of(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"),
                new Epictask("жениться", "родители просят внуков"),
                new Epictask("отпуск", "отпуск конец августа")
        );
    }

    @BeforeEach
    public void initTaskManager() {
        int START_ID_BY_DEFAULT = 1;
        Repository<Integer, Task> tasks = new InMemoryMap<>();
        Repository<Integer, ArrayList<Integer>> subordinates = new InMemoryMap<>();
        Generator generator = new SerialGenerator(START_ID_BY_DEFAULT);
        HistoryManager history = getDefaultHistory();
        Repository<LocalDateTime, Integer> starts = new InMemoryTreeMap<LocalDateTime, Integer>((u, v) -> u.compareTo(v));
        Repository<LocalDateTime, Integer> finishes = new InMemoryTreeMap<LocalDateTime, Integer>((u, v) -> u.compareTo(v));
        taskManager = new RegularTaskManager(tasks, subordinates, generator, history, starts, finishes);
        taskManager.clearHistory();
    }

    @DisplayName("add and get Selftasks")
    @Tag("add")
    @ParameterizedTest
    @MethodSource("getSelfTasks")
    void addAndGetSelfTaskTest(Selftask task) {
        Task addedTask = taskManager.add(task);
        int id = addedTask.getId();

        // проверяем, что метод "add" возвращает корректные таски
        assertAll(
                () -> assertInstanceOf(Selftask.class, addedTask),
                () -> assertSame(Subordination.SELF, addedTask.getSubordination()),
                () -> assertEquals(task.getName(), addedTask.getName()),
                () -> assertEquals(task.getDescription(), addedTask.getDescription()),
                () -> assertEquals(startDateTime, addedTask.getStartTime()),
                () -> assertEquals(duration, addedTask.getDuration())
        );
        Task gettedTask = taskManager.get(id);

        // проверяем, что в базе сохранены корректные таски, вызывая каждый по id
        assertAll(
                () -> assertEquals(id, gettedTask.getId()),
                () -> assertInstanceOf(Selftask.class, gettedTask),
                () -> assertSame(Subordination.SELF, gettedTask.getSubordination()),
                () -> assertEquals(task.getName(), gettedTask.getName()),
                () -> assertEquals(task.getDescription(), gettedTask.getDescription()),
                () -> assertEquals(startDateTime, task.getStartTime()),
                () -> assertEquals(duration, addedTask.getDuration())
        );
    }

    @DisplayName("add null Selftask")
    @Tag("add")
    @ParameterizedTest
    @NullSource
    void addNullSelfTaskTest(Selftask task) {
        assertNull(taskManager.add(task));
    }

    @DisplayName("add null Epictask")
    @Tag("add")
    @ParameterizedTest
    @NullSource
    void addNullEpicTaskTest(Epictask task) {
        assertNull(taskManager.add(task));
    }

    @DisplayName("add null Subtask")
    @Tag("add")
    @ParameterizedTest
    @NullSource
    void addNullSubTaskTest(Subtask task) {
        assertNull(taskManager.add(task));
    }

    @DisplayName("add alone tasks and check that we get correct back from TaskManager")
    @Tag("add")
    @Test
    void getAllSelfTaskTest() {
        LocalDateTime startDateTime1 = LocalDateTime.of(2020, 1, 1, 1, 1);
        LocalDateTime startDateTime2 = LocalDateTime.of(2021, 1, 1, 1, 1);
        LocalDateTime startDateTime3 = LocalDateTime.of(2022, 1, 1, 1, 1);
        LocalDateTime startDateTime4 = LocalDateTime.of(2023, 1, 1, 1, 1);
        LocalDateTime startDateTime5 = LocalDateTime.of(2024, 1, 1, 1, 1);

        HashSet<Task> set = new HashSet<>();
        List<Selftask> taskList = List.of(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime1, duration),
                new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime2, duration),
                new Selftask("скачать сериал", "Игра престолов", startDateTime3, duration),
                new Selftask("работать", "работу", startDateTime4, duration),
                new Selftask("смотреть на закат", "и на рассвет", startDateTime5, duration)
        );
        for (Selftask task : taskList) {
            set.add(taskManager.add(task));
        }
        List<Task> getAllList = taskManager.getAll();
        assertAll(
                () -> assertEquals(getAllList.size(), taskList.size()),
                () -> assertTrue(set.contains(getAllList.get(0))),
                () -> assertTrue(set.contains(getAllList.get(1))),
                () -> assertTrue(set.contains(getAllList.get(2))),
                () -> assertTrue(set.contains(getAllList.get(3))),
                () -> assertTrue(set.contains(getAllList.get(4)))
        );
    }

    @DisplayName("add and get epic tasks (without subtasks)")
    @Tag("add")
    @ParameterizedTest
    @MethodSource("getEpicTasks")
    void addAndGetEpicTest(Epictask task) {
        Task addedTask = taskManager.add(task);
        int id = addedTask.getId();

        // проверяем, что метод "add" возвращает корректные таски
        assertAll(
                () -> assertInstanceOf(Epictask.class, addedTask),
                () -> assertSame(Subordination.EPIC, addedTask.getSubordination()),
                () -> assertEquals(task.getName(), addedTask.getName()),
                () -> assertEquals(task.getDescription(), addedTask.getDescription()),
                () -> assertFalse(((Epictask) addedTask).isTimeDefined())
        );
        Task gettedTask = taskManager.get(id);

        // проверяем, что в базе сохранены корректные таски, вызывая каждый по id
        assertAll(
                () -> assertEquals(id, gettedTask.getId()),
                () -> assertInstanceOf(Epictask.class, gettedTask),
                () -> assertSame(Subordination.EPIC, gettedTask.getSubordination()),
                () -> assertEquals(task.getName(), gettedTask.getName()),
                () -> assertEquals(task.getDescription(), gettedTask.getDescription()),
                () -> assertFalse(((Epictask) addedTask).isTimeDefined())
        );
    }

    @DisplayName("add some alone epic tasks and check that we get correct back from TaskManager")
    @Tag("add")
    @Test
    void getAllEpicTaskTest() {
        HashSet<Task> set = new HashSet<>();
        List<Epictask> taskList = List.of(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"),
                new Epictask("жениться", "родители просят внуков"),
                new Epictask("отпуск", "отпуск конец августа")
        );
        for (Epictask task : taskList) {
            set.add(taskManager.add(task));
        }
        List<Task> getAllList = taskManager.getAll();
        assertAll(
                () -> assertEquals(getAllList.size(), taskList.size()),
                () -> assertTrue(set.contains(getAllList.get(0))),
                () -> assertTrue(set.contains(getAllList.get(1))),
                () -> assertTrue(set.contains(getAllList.get(2)))
        );
    }

    @DisplayName("return the list of epic subtasks")
    @Tag("subList")
    @Test
    void getAllSubsFromEpicFilledTest() {
        Epictask epic = new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго");
        epic = taskManager.add(epic);

        LocalDateTime startDateTime1 = LocalDateTime.of(2019, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        Subtask sub1 = new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", startDateTime1, duration1, epic.getId());

        LocalDateTime startDateTime2 = LocalDateTime.of(2020, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        Subtask sub2 = new Subtask("наловить червей", "200 шт.", startDateTime2, duration2, epic.getId());

        LocalDateTime startDateTime3 = LocalDateTime.of(2021, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(2);
        Subtask sub3 = new Subtask("купить алкоголь", "батя обещал самогон", startDateTime, duration, epic.getId());
        Task task1 = taskManager.add(sub1);
        Task task2 = taskManager.add(sub2);
        Task task3 = taskManager.add(sub3);
        List<Subtask> listOfSubs = taskManager.getAllSubs(epic.getId());
        assertAll(
                () -> assertEquals(3, listOfSubs.size()),
                () -> assertTrue(listOfSubs.contains(task1)),
                () -> assertTrue(listOfSubs.contains(task2)),
                () -> assertTrue(listOfSubs.contains(task3))
        );
    }

    @DisplayName("return the list of epic subtasks if it has not any subtasks")
    @Tag("subList")
    @Test
    void getAllSubsFromEpicEmptyTest() {
        Epictask epic = new Epictask("отпуск", "отпуск конец августа");
        epic = taskManager.add(epic);
        List<Subtask> list = taskManager.getAllSubs(epic.getId());
        assertEquals(Collections.emptyList(), list);
    }

    @DisplayName("add epic and subtasks and check the list of all tasks")
    @Tag("add")
    @Test
    void getSubTaskFromWholeListTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        int epicId = epic.getId();
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20",
                        LocalDateTime.of(3024, 1, 10, 12, 00),
                        Duration.ofDays(2),
                        epicId));
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.",
                        LocalDateTime.of(5024, 1, 1, 12, 00),
                        Duration.ofDays(20),
                        epicId));
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон",
                        LocalDateTime.of(6022, 1, 1, 12, 00),
                        Duration.ofDays(10),
                        epicId));
        List<Task> getAllList = taskManager.getAll();
        assertAll(
                () -> assertEquals(4, getAllList.size()),
                () -> assertTrue(getAllList.contains(epic)),
                () -> assertTrue(getAllList.contains(sub1)),
                () -> assertTrue(getAllList.contains(sub2)),
                () -> assertTrue(getAllList.contains(sub3))
        );
        Epictask updatedEpic = taskManager.getEpic(epicId);
        LocalDateTime start = sub1.getStartTime();
        LocalDateTime finish = sub3.getStartTime().plus(sub3.getDuration());
        Duration durationUpdated = Duration.between(start, finish);
        assertAll(
                () -> assertTrue(updatedEpic.isTimeDefined()),
                () -> assertEquals(start, updatedEpic.getStartTime()),
                () -> assertEquals(durationUpdated, updatedEpic.getDuration())
        );
    }

    @DisplayName("try to get absent task")
    @Tag("get")
    @Test
    void getNotExistingTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        assertAll(
                () -> assertNull(taskManager.get(600)),
                () -> assertNull(taskManager.get(null))
        );
    }

    @DisplayName("get absent Selftask")
    @Tag("get")
    @Test
    void getNotExistingSelfTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        assertAll(
                () -> assertNull(taskManager.getSelftask(600)),
                () -> assertNull(taskManager.getSelftask(null))
        );
    }

    @DisplayName("get absent Epictask")
    @Tag("get")
    @Test
    void getNotExistingEpicTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        assertAll(
                () -> assertNull(taskManager.getEpic(600)),
                () -> assertNull(taskManager.getEpic(null))
        );
    }

    @DisplayName("get absent Subtask")
    @Tag("get")
    @Test
    void getNotExistingSubTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime, duration));
        assertAll(
                () -> assertNull(taskManager.getSubtask(600)),
                () -> assertNull(taskManager.getSubtask(null))
        );
    }

    @DisplayName("delete Selftask, try to get")
    @Tag("delete")
    @Test
    void deleteSelfTest() {
        LocalDateTime startDateTime1 = LocalDateTime.of(2014, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime1, duration1));

        LocalDateTime startDateTime2 = LocalDateTime.of(2015, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime2, duration2));

        LocalDateTime startDateTime3 = LocalDateTime.of(2016, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(2);
        Selftask task = taskManager.add(new Selftask("все проходит", "и это пройдет", startDateTime3, duration3));
        int id = task.getId();
        assertEquals(task, taskManager.get(id));
        taskManager.delete(id);
        assertNull(taskManager.get(id));
    }

    @DisplayName("delete Epic, check that epic and subtasks deleted")
    @Tag("delete")
    @Test
    void deleteEpicTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));

        LocalDateTime startDateTime1 = LocalDateTime.of(2014, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", startDateTime1, duration1, epic.getId()));

        LocalDateTime startDateTime2 = LocalDateTime.of(2015, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.", startDateTime2, duration2, epic.getId()));

        LocalDateTime startDateTime3 = LocalDateTime.of(2016, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(2);
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон", startDateTime3, duration3, epic.getId()));

        taskManager.delete(epic.getId());
        assertNull(taskManager.get(epic.getId()));
        assertNull(taskManager.get(sub1.getId()));
        assertNull(taskManager.get(sub2.getId()));
        assertNull(taskManager.get(sub3.getId()));
    }

    @DisplayName("delete subtask, check that only subtask deleted")
    @Tag("delete")
    @Test
    void deleteSubTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));

        LocalDateTime startDateTime1 = LocalDateTime.of(2014, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", startDateTime1, duration1, epic.getId()));

        LocalDateTime startDateTime2 = LocalDateTime.of(2015, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.", startDateTime2, duration2, epic.getId()));

        LocalDateTime startDateTime3 = LocalDateTime.of(2016, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(2);
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон", startDateTime3, duration3, epic.getId()));
        taskManager.delete(sub1.getId());
        taskManager.getEpic(epic.getId());
        assertAll(
                () -> assertEquals(epic, taskManager.getEpic(epic.getId())),
                () -> assertNull(taskManager.get(sub1.getId())),
                () -> assertEquals(sub2, taskManager.getSubtask(sub2.getId())),
                () -> assertEquals(sub3, taskManager.getSubtask(sub3.getId()))
        );
        taskManager.delete(sub2.getId());
        assertAll(
                () -> assertEquals(epic, taskManager.getEpic(epic.getId())),
                () -> assertNull(taskManager.get(sub1.getId())),
                () -> assertNull(taskManager.get(sub2.getId())),
                () -> assertEquals(sub3, taskManager.getSubtask(sub3.getId()))
        );
        taskManager.delete(sub3.getId());
        assertAll(
                () -> assertEquals(epic, taskManager.getEpic(epic.getId())),
                () -> assertNull(taskManager.get(sub1.getId())),
                () -> assertNull(taskManager.get(sub2.getId())),
                () -> assertNull(taskManager.get(sub3.getId()))
        );

    }

    @DisplayName("clear all tasks")
    @Tag("clear")
    @Test
    void clearTest() {
        LocalDateTime startDateTime1 = LocalDateTime.of(2014, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime1, duration1));

        LocalDateTime startDateTime2 = LocalDateTime.of(2015, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime2, duration2));

        LocalDateTime startDateTime3 = LocalDateTime.of(2016, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(2);
        taskManager.add(new Selftask("все проходит", "и это пройдет", startDateTime3, duration3));
        assertEquals(3, taskManager.getAll().size());
        taskManager.clear();
        assertEquals(0, taskManager.getAll().size());
    }

    @DisplayName("update name, description, status of Selftask")
    @Tag("update")
    @Test
    void updateSelfTaskTest() {
        String oldName = "Иван";
        String newName = "Петр";
        String oldDesc = "Иванов";
        String newDesc = "Петров";
        Status newStatus = Status.IN_PROGRESS;
        Task task = taskManager.add(new Selftask(oldName, oldDesc, startDateTime, duration));
        assertAll(
                () -> assertEquals(task.getName(), oldName),
                () -> assertEquals(task.getDescription(), oldDesc),
                () -> assertEquals(task.getStatus(), Status.NEW),
                () -> assertEquals(startDateTime, task.getStartTime()),
                () -> assertEquals(duration, task.getDuration())
        );
        task.setName(newName);
        task.setDescription(newDesc);
        task.setStatus(newStatus);
        Task updatedTask = taskManager.update(task);
        assertAll(
                () -> assertEquals(task, updatedTask),
                () -> assertEquals(updatedTask.getName(), newName),
                () -> assertEquals(updatedTask.getDescription(), newDesc),
                () -> assertEquals(updatedTask.getStatus(), newStatus),
                () -> assertEquals(startDateTime, updatedTask.getStartTime()),
                () -> assertEquals(duration, updatedTask.getDuration())
        );
    }

    @Tag("update")
    @DisplayName("update name, description, status of EpicTask")
    @Test
    void updateEpicTaskTest() {
        String oldName = "Иван";
        String newName = "Петр";
        String oldDesc = "Иванов";
        String newDesc = "Петров";
        Status newStatus = Status.IN_PROGRESS;
        Task task = taskManager.add(new Epictask(oldName, oldDesc));
        assertAll(
                () -> assertEquals(task.getName(), oldName),
                () -> assertEquals(task.getDescription(), oldDesc),
                () -> assertEquals(task.getStatus(), Status.NEW)
        );
        task.setName(newName);
        task.setDescription(newDesc);
        task.setStatus(newStatus);
        Task updatedTask = taskManager.update(task);
        assertAll(
                () -> assertEquals(task, updatedTask),
                () -> assertEquals(updatedTask.getName(), newName),
                () -> assertEquals(updatedTask.getDescription(), newDesc),
                () -> assertEquals(updatedTask.getStatus(), Status.NEW)
        );
    }

    @Tag("update")
    @DisplayName("update of name, description, status of  SubTask")
    @Test
    void updateSubTaskTest() {
        String oldName = "Иван";
        String newName = "Петр";
        String oldDesc = "Иванов";
        String newDesc = "Петров";
        Status newStatus = Status.IN_PROGRESS;

        Epictask epic = new Epictask("sss", "sss");
        epic = taskManager.add(epic);
        int epicId = epic.getId();

        Task task = taskManager.add(new Subtask(oldName, oldDesc, startDateTime, duration, epicId));

        assertAll(
                () -> assertEquals(task.getName(), oldName),
                () -> assertEquals(task.getDescription(), oldDesc),
                () -> assertEquals(task.getStatus(), Status.NEW),
                () -> assertEquals(startDateTime, task.getStartTime()),
                () -> assertEquals(duration, task.getDuration())
        );
        task.setName(newName);
        task.setDescription(newDesc);
        task.setStatus(newStatus);
        Task updatedTask = taskManager.update(task);
        assertEquals(task, updatedTask);
        assertAll(
                () -> assertEquals(updatedTask.getName(), newName),
                () -> assertEquals(updatedTask.getDescription(), newDesc),
                () -> assertEquals(updatedTask.getStatus(), newStatus),
                () -> assertEquals(startDateTime, updatedTask.getStartTime()),
                () -> assertEquals(duration, updatedTask.getDuration())
        );
    }

    @DisplayName("equality SelfTasks with equal id")
    @Tag("equality")
    @Test
    void checkIfEqualSelfTasksTest() {
        Task selfTask1 = new Selftask("ss", "ffff", startDateTime, duration);
        selfTask1.setId(555);
        Task selfTask2 = new Selftask("arsgfs", "cdb gfv", startDateTime, duration);
        selfTask2.setId(555);
        assertEquals(selfTask1, selfTask2);
    }

    @DisplayName("equality of EpicTask with equal id")
    @Tag("equality")
    @Test
    void checkIfEqualEpicTasksTest() {
        Epictask epicTask1 = new Epictask("ss", "ffff");
        epicTask1.setId(555);
        Epictask epicTask2 = new Epictask("arsgfs", "cdb gfv");
        epicTask2.setId(555);
        assertEquals(epicTask1, epicTask2);
    }

    @DisplayName("equality SubTask with equal id")
    @Tag("equality")
    @Test
    void checkIfEqualSubTasksTest() {
        Subtask subTask1 = new Subtask("ss", "ffff", startDateTime, duration, 1);
        subTask1.setId(555);
        Subtask subTask2 = new Subtask("arsgfs", "cdb gfv", startDateTime, duration, 3);
        subTask2.setId(555);
        assertEquals(subTask1, subTask2);
    }

    @DisplayName("Selftask can not be Epic")
    @Tag("equality")
    @Test
    void selfCanNotBeEpicTest() {
        Selftask self = new Selftask("sss", "sss", startDateTime, duration);
        self = taskManager.add(self);
        int selfId = self.getId();
        Subtask subTask = new Subtask("ss", "ffff", startDateTime, duration, selfId);
        assertNull(taskManager.add(subTask));
    }

    @DisplayName("Subtask can not be Epic")
    @Tag("SubEpic")
    @Test
    void subCanNotBeEpicTest() {
        Epictask epic = new Epictask("ss", "ffff");
        epic = taskManager.add(epic);
        Subtask sub1 = new Subtask("sss", "sss", startDateTime, duration, epic.getId());
        sub1 = taskManager.add(sub1);
        Subtask sub2 = new Subtask("sss", "sss", startDateTime, duration, sub1.getId());
        assertNull(taskManager.add(sub2));
    }

    @DisplayName("id of SelfTask is assigned in TaskManager, not come from user")
    @Tag("SubEpic")
    @Test
    void isIdGetFromManagerForSelfTaskTest() {
        int fakeId = -1_000_000;
        Selftask self = new Selftask("sss", "sss", startDateTime, duration);
        self.setId(fakeId);
        self = taskManager.add(self);
        assertNotEquals(fakeId, self.getId());
    }

    @DisplayName("id of EoicTask is assigned in TaskManager, not come from user")
    @Tag("id")
    @Test
    void isIdGetFromManagerForEpicTaskTest() {
        int fakeId = -1_000_000;
        Epictask task = new Epictask("sss", "sss");
        task.setId(fakeId);
        task = taskManager.add(task);
        assertNotEquals(fakeId, task.getId());
    }

    @DisplayName("id of SubTask is assigned in TaskManager, not come from user")
    @Tag("id")
    @Test
    void isIdGetFromManagerForSubTaskTest() {
        int fakeId = -1_000_000;
        Epictask epic = new Epictask("sss", "sss");
        epic = taskManager.add(epic);
        Subtask sub = new Subtask("sss", "sss", startDateTime, duration, epic.getId());
        sub.setId(fakeId);
        sub = taskManager.add(sub);
        assertNotEquals(fakeId, sub.getId());
    }

    @DisplayName("status of Epic change when we change status of Subtasks")
    @Tag("SUbEpic")
    @Test
    void epicStatusTest() {
        Epictask epic = taskManager.add(new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));

        LocalDateTime startDateTime1 = LocalDateTime.of(2014, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", startDateTime1, duration1, epic.getId()));

        LocalDateTime startDateTime2 = LocalDateTime.of(2015, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.", startDateTime2, duration2, epic.getId()));

        LocalDateTime startDateTime3 = LocalDateTime.of(2016, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(2);
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон", startDateTime, duration, epic.getId()));
        int epicId = epic.getId();
        assertEquals(Status.NEW, epic.getStatus());

        sub1.setStatus(Status.DONE);
        taskManager.update(sub1);
        assertEquals(Status.IN_PROGRESS, taskManager.get(epicId).getStatus());

        sub2.setStatus(Status.DONE);
        taskManager.update(sub2);
        assertEquals(Status.IN_PROGRESS, taskManager.get(epicId).getStatus());

        sub3.setStatus(Status.DONE);
        taskManager.update(sub3);
        assertEquals(Status.DONE, taskManager.get(epicId).getStatus());

        sub3.setStatus(Status.IN_PROGRESS);
        taskManager.update(sub3);
        assertEquals(Status.IN_PROGRESS, taskManager.get(epicId).getStatus());

        taskManager.delete(sub1.getId());
        taskManager.delete(sub2.getId());
        taskManager.delete(sub3.getId());
        assertEquals(taskManager.get(epicId).getStatus(), Status.NEW);
    }

    @DisplayName("history is not changed when we add, remove and update tasks")
    @Tag("history")
    @Test
    void historyNotUpdatedTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));

        LocalDateTime startDateTime1 = LocalDateTime.of(2014, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        taskManager.add(new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20",
                startDateTime1, duration1, epic.getId()));

        LocalDateTime startDateTime2 = LocalDateTime.of(2015, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        Subtask sub2 = taskManager.add(new Subtask("наловить червей", "200 шт.", startDateTime2, duration2, epic.getId()));

        LocalDateTime startDateTime3 = LocalDateTime.of(2016, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(2);
        taskManager.add(new Subtask("купить алкоголь", "батя обещал самогон", startDateTime3, duration3, epic.getId()));

        LocalDateTime startDateTime4 = LocalDateTime.of(2017, 1, 1, 1, 1);
        Duration duration4 = Duration.ofDays(2);
        Selftask self1 = taskManager.add(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime4, duration4));

        LocalDateTime startDateTime5 = LocalDateTime.of(2018, 1, 1, 1, 1);
        Duration duration5 = Duration.ofDays(2);
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime5, duration5));

        LocalDateTime startDateTime6 = LocalDateTime.of(2019, 1, 1, 1, 1);
        Duration duration6 = Duration.ofDays(2);
        Selftask self3 = taskManager.add(new Selftask("скачать сериал", "Игра престолов", startDateTime6, duration6));
        assertTrue(taskManager.getHistory().isEmpty());

        taskManager.delete(self3.getId());
        taskManager.delete(sub2.getId());
        taskManager.delete(epic.getId());
        assertTrue(taskManager.getHistory().isEmpty());

        LocalDateTime startDateTime7 = LocalDateTime.of(2020, 1, 1, 1, 1);
        Duration duration7 = Duration.ofDays(2);
        Selftask newTask = new Selftask("bla", "bla", startDateTime7, duration7);
        newTask.setId(self1.getId());
        taskManager.update(newTask);
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("history update when we get Task")
    @Tag("history")
    @Test
    void historyUpdatedTest() {
        LocalDateTime startDateTime1 = LocalDateTime.of(2019, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(2);
        Selftask self1 = taskManager.add(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог", startDateTime1, duration1));

        LocalDateTime startDateTime2 = LocalDateTime.of(2020, 1, 1, 1, 1);
        Duration duration2 = Duration.ofDays(2);
        Selftask self2 = taskManager.add(
                new Selftask("выгулять собаку", "пойти вечером погулять в парк", startDateTime2, duration2));

        taskManager.add(new Selftask("скачать сериал", "Игра престолов", startDateTime, duration));
        taskManager.get(self1.getId());
        taskManager.get(self2.getId());
        Task[] correctList = {self2, self1};
        assertArrayEquals(correctList, taskManager.getHistory().toArray());
    }

    @DisplayName("add Selftask with time")
    @Tag("datetime")
    @Test
    void addSelfTaskWithTimeTest() {
        LocalDateTime localDateTime = LocalDateTime.of(1, 2, 3, 4, 5);
        Duration duration = Duration.ofDays(1);
        Selftask selftask = taskManager.add(new Selftask("задача1", "описание1", localDateTime, duration));
        assertAll(
                () -> assertEquals(localDateTime, taskManager.get(selftask.getId()).getStartTime()),
                () -> assertEquals(duration, taskManager.get(selftask.getId()).getDuration())
        );
    }

    @DisplayName("add Epictask without time")
    @Tag("datetime")
    @Test
    void addEpicTaskWithoutTimeTest() {
        Epictask epictask = taskManager.add(new Epictask("задача1", "описание1"));
        assertFalse(((Epictask) taskManager.get(epictask.getId())).isTimeDefined());
    }

    @DisplayName("add Subtask with time")
    @Tag("datetime")
    @Test
    void addSubTaskWithTimeTest() {
        Epictask epictask = taskManager.add(new Epictask("задача1", "описание1"));
        LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 12, 3, 5);
        Duration duration = Duration.ofDays(10);
        Subtask subtask = taskManager.add(new Subtask("задача1", "описание1",
                localDateTime, duration, epictask.getId()));
        assertAll(
                () -> assertEquals(localDateTime,
                        taskManager.get(subtask.getId()).getStartTime()),
                () -> assertEquals(duration, taskManager.get(subtask.getId()).getDuration())
        );
    }

    @DisplayName("add Epic and Subtask with time")
    @Tag("datetime")
    @Test
    void addEpicAndSubTaskWithTimeTest() {
        Epictask epictask = taskManager.add(new Epictask("задача1", "описание1"));
        assertFalse(((Epictask) taskManager.get(epictask.getId())).isTimeDefined());

        LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 12, 3, 5);
        Duration duration = Duration.ofDays(10);
        Subtask subtask = taskManager.add(new Subtask("задача1", "описание1",
                localDateTime, duration, epictask.getId()));
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epictask.getId())).isTimeDefined()),
                () -> assertEquals(localDateTime,
                        taskManager.get(epictask.getId()).getStartTime()),
                () -> assertEquals(duration, taskManager.get(epictask.getId()).getDuration())
        );
    }

    @DisplayName("add Epic and Subtask and delete Subtask then")
    @Tag("datetime")
    @Test
    void addEpicAndSubTaskWithTimeAndDeleteSubTest() {
        Epictask epictask = taskManager.add(new Epictask("задача1", "описание1"));

        LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 12, 3, 5);
        Duration duration = Duration.ofDays(10);
        Subtask subtask = taskManager.add(new Subtask("задача1", "описание1",
                localDateTime, duration, epictask.getId()));
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epictask.getId())).isTimeDefined()),
                () -> assertEquals(localDateTime,
                        taskManager.get(epictask.getId()).getStartTime()),
                () -> assertEquals(duration, taskManager.get(epictask.getId()).getDuration())
        );
        taskManager.delete(subtask.getId());
        assertFalse(((Epictask) taskManager.get(epictask.getId())).isTimeDefined());
    }

    @DisplayName("add Epic and several Subtasks. Check time change of Epic ")
    @Tag("datetime")
    @Test
    void changeSubtasksTimeAndCheckEpicTest() {
        Epictask epictask = taskManager.add(new Epictask("задача1", "описание1"));
        assertFalse(((Epictask) taskManager.get(epictask.getId())).isTimeDefined());

        LocalDateTime localDateTime1 = LocalDateTime.of(2024, 04, 04, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Subtask subtask1 = taskManager.add(new Subtask("задача1", "описание1",
                localDateTime1, duration1, epictask.getId()));
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epictask.getId())).isTimeDefined()),
                () -> assertEquals(localDateTime1,
                        taskManager.get(epictask.getId()).getStartTime()),
                () -> assertEquals(duration1, taskManager.get(epictask.getId()).getDuration())
        );

        LocalDateTime localDateTime2 = LocalDateTime.of(2025, 04, 04, 00, 00);
        ;
        Duration duration2 = Duration.ofDays(10);
        LocalDateTime endLocalDateTime2 = localDateTime2.plus(duration2);
        Subtask subtask2 = taskManager.add(new Subtask("задача1", "описание1",
                localDateTime2, duration2, epictask.getId()));
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epictask.getId())).isTimeDefined()),
                () -> assertEquals(localDateTime1,
                        taskManager.get(epictask.getId()).getStartTime()),
                () -> assertEquals(Duration.between(localDateTime1, endLocalDateTime2), taskManager.get(epictask.getId()).getDuration())
        );

        LocalDateTime localDateTime3 = LocalDateTime.of(2020, 02, 01, 10, 30);
        Duration duration3 = Duration.ofDays(11);
        Subtask subtask3 = taskManager.add(new Subtask("задача1", "описание1",
                localDateTime3, duration3, epictask.getId()));
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epictask.getId())).isTimeDefined()),
                () -> assertEquals(localDateTime3,
                        taskManager.get(epictask.getId()).getStartTime()),
                () -> assertEquals(Duration.between(localDateTime3, endLocalDateTime2),
                        taskManager.get(epictask.getId()).getDuration())
        );
        taskManager.delete(subtask3.getId());
    }

    @DisplayName("add Epic and several Subtasks and delete them. Check time change of Epic ")
    @Tag("datetime")
    @Test
    void epicTimeWhenDeleteSubs() {
        Epictask epictask = taskManager.add(new Epictask("задача1", "описание1"));
        int epicId = epictask.getId();

        LocalDateTime time1 = LocalDateTime.of(2000, 1, 1, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Subtask subtask1 = taskManager.add(new Subtask("задача1", "описание1", time1, duration1, epicId));

        LocalDateTime time2 = LocalDateTime.of(1999, 1, 1, 00, 00);
        Duration duration2 = Duration.ofDays(1);
        Subtask subtask2 = taskManager.add(new Subtask("задача1", "описание1", time2, duration2, epicId));

        LocalDateTime time3 = LocalDateTime.of(2001, 11, 1, 00, 00);
        Duration duration3 = Duration.ofDays(2);
        Subtask subtask3 = taskManager.add(new Subtask("задача1", "описание1", time3, duration3, epicId));

        LocalDateTime time4 = LocalDateTime.of(1998, 1, 1, 00, 00);
        Duration duration4 = Duration.ofDays(1);
        Subtask subtask4 = taskManager.add(new Subtask("задача1", "описание1", time4, duration4, epicId));

        Duration epicDuration = Duration.between(time4, time3.plus(duration3));
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epicId)).isTimeDefined()),
                () -> assertEquals(time4, taskManager.get(epicId).getStartTime()),
                () -> assertEquals(epicDuration, taskManager.get(epicId).getDuration())
        );

        taskManager.delete(subtask4.getId());
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epicId)).isTimeDefined()),
                () -> assertEquals(time2, taskManager.get(epicId).getStartTime()),
                () -> assertEquals(Duration.between(time2, time3.plus(duration3)), taskManager.get(epicId).getDuration())
        );
        taskManager.delete(subtask3.getId());
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epicId)).isTimeDefined()),
                () -> assertEquals(time2, taskManager.get(epicId).getStartTime()),
                () -> assertEquals(Duration.between(time2, time1.plus(duration1)), taskManager.get(epicId).getDuration())
        );
        taskManager.delete(subtask2.getId());
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epicId)).isTimeDefined()),
                () -> assertEquals(time1, taskManager.get(epicId).getStartTime()),
                () -> assertEquals(duration1, taskManager.get(epicId).getDuration())
        );
        taskManager.delete(subtask1.getId());
        assertFalse(((Epictask) taskManager.get(epicId)).isTimeDefined());
    }

    @DisplayName("add Epic and Subtasks and update it. Check time change of Epic ")
    @Tag("datetime")
    @Test
    void epicTimeWhenUpdateSubs() {
        Epictask epictask = taskManager.add(new Epictask("задача1", "описание1"));
        int epicId = epictask.getId();

        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 00, 00);
        Duration duration = Duration.ofDays(1);
        Subtask subtask = taskManager.add(new Subtask("задача1", "описание1", time, duration, epicId));
        int subId = subtask.getId();
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epicId)).isTimeDefined()),
                () -> assertEquals(time, taskManager.get(epicId).getStartTime()),
                () -> assertEquals(duration, taskManager.get(epicId).getDuration())
        );

        Duration duration1 = Duration.ofDays(10);
        Subtask updatedSub = new Subtask("задача1", "описание1", time, duration1, epicId);
        updatedSub.setId(subId);
        taskManager.update(updatedSub);
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epicId)).isTimeDefined()),
                () -> assertEquals(time, taskManager.get(epicId).getStartTime()),
                () -> assertEquals(duration1, taskManager.get(epicId).getDuration())
        );

        LocalDateTime time2 = LocalDateTime.of(1000, 1, 1, 00, 00);
        Duration duration2 = Duration.ofDays(100);
        updatedSub = new Subtask("задача1", "описание1", time2, duration2, epicId);
        updatedSub.setId(subId);
        taskManager.update(updatedSub);
        assertAll(
                () -> assertTrue(((Epictask) taskManager.get(epicId)).isTimeDefined()),
                () -> assertEquals(time2, taskManager.get(epicId).getStartTime()),
                () -> assertEquals(duration2, taskManager.get(epicId).getDuration())
        );
    }

    @DisplayName("add Selftasks with time intersections")
    @Tag("timeline")
    @Test
    void addSelfTimelineTest() {
        LocalDateTime time1 = LocalDateTime.of(2000, 1, 1, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", time1, duration1));
        assertNotNull(task1);

        LocalDateTime time2 = LocalDateTime.of(1000, 1, 1, 00, 00);
        Duration duration2 = Duration.ofDays(365);
        Selftask task2 = taskManager.add(new Selftask("name", "descript", time2, duration2));
        assertNotNull(task2);

        LocalDateTime time3 = LocalDateTime.of(1000, 2, 1, 00, 00);
        Duration duration3 = Duration.ofDays(1);
        Selftask task3 = taskManager.add(new Selftask("name", "descript", time3, duration3));
        assertNull(task3);

        LocalDateTime time4 = LocalDateTime.of(999, 12, 31, 00, 00);
        Duration duration4 = Duration.ofDays(10);
        Selftask task4 = taskManager.add(new Selftask("name", "descript", time4, duration4));
        assertNull(task4);

        LocalDateTime time5 = LocalDateTime.of(1000, 12, 30, 00, 00);
        Duration duration5 = Duration.ofDays(10);
        Selftask task5 = taskManager.add(new Selftask("name", "descript", time5, duration5));
        assertNull(task5);
    }

    @DisplayName("add Subtasks with time intersections")
    @Tag("timeline")
    @Test
    void addSubTimelineTest() {
        Epictask epic = taskManager.add(new Epictask("a", "s"));
        int epicId = epic.getId();

        LocalDateTime time1 = LocalDateTime.of(2000, 1, 1, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Subtask task1 = taskManager.add(new Subtask("name", "descript", time1, duration1, epicId));
        assertNotNull(task1);

        LocalDateTime time2 = LocalDateTime.of(1000, 1, 1, 00, 00);
        Duration duration2 = Duration.ofDays(365);
        Subtask task2 = taskManager.add(new Subtask("name", "descript", time2, duration2, epicId));
        assertNotNull(task2);

        LocalDateTime time3 = LocalDateTime.of(1000, 2, 1, 00, 00);
        Duration duration3 = Duration.ofDays(1);
        Subtask task3 = taskManager.add(new Subtask("name", "descript", time3, duration3, epicId));
        assertNull(task3);

        LocalDateTime time4 = LocalDateTime.of(999, 12, 31, 00, 00);
        Duration duration4 = Duration.ofDays(10);
        Subtask task4 = taskManager.add(new Subtask("name", "descript", time4, duration4, epicId));
        assertNull(task4);

        LocalDateTime time5 = LocalDateTime.of(1000, 12, 30, 00, 00);
        Duration duration5 = Duration.ofDays(10);
        Subtask task5 = taskManager.add(new Subtask("name", "descript", time5, duration5, epicId));
        assertNull(task5);
    }

    @DisplayName("add single Epictask")
    @Tag("timeline")
    @Test
    void addEpicTimelineTest() {
        Epictask epic = taskManager.add(new Epictask("a", "s"));
        assertNotNull(epic);
    }

    @DisplayName("update Selftask")
    @Tag("timeline")
    @Test
    void updateSelfTaskTimelineTest() {
        LocalDateTime time1 = LocalDateTime.of(2000, 1, 1, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", time1, duration1));
        assertNotNull(task1);

        LocalDateTime time2 = LocalDateTime.of(2000, 1, 10, 00, 00);
        Duration duration2 = Duration.ofDays(1);
        Selftask task2 = taskManager.add(new Selftask("name", "descript", time2, duration2));
        assertNotNull(task2);

        LocalDateTime time3 = LocalDateTime.of(2001, 1, 10, 00, 00);
        task2.setStartTime(time3);
        task2 = taskManager.update(task2);
        assertNotNull(task2);

        LocalDateTime time4 = LocalDateTime.of(1999, 1, 12, 00, 00);
        Duration duration4 = Duration.ofDays(365);
        task2.setStartTime(time4);
        task2.setDuration(duration4);
        task2 = taskManager.update(task2);
        assertNull(task2);
    }

    @DisplayName("get tasks according starttime")
    @Tag("timeline")
    @Test
    void getPrioritizedTasks() {
        LocalDateTime time1 = LocalDateTime.of(10, 1, 1, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", time1, duration1));

        LocalDateTime time2 = LocalDateTime.of(1, 1, 1, 00, 00);
        Duration duration2 = Duration.ofDays(1);
        Selftask task2 = taskManager.add(new Selftask("name", "descript", time2, duration2));

        LocalDateTime time3 = LocalDateTime.of(2, 1, 1, 00, 00);
        Duration duration3 = Duration.ofDays(1);
        Selftask task3 = taskManager.add(new Selftask("name", "descript", time3, duration3));

        LocalDateTime time4 = LocalDateTime.of(6, 1, 1, 00, 00);
        Duration duration4 = Duration.ofDays(1);
        Selftask task4 = taskManager.add(new Selftask("name", "descript", time4, duration4));

        LocalDateTime time5 = LocalDateTime.of(5, 1, 1, 00, 00);
        Duration duration5 = Duration.ofDays(1);
        Selftask task5 = taskManager.add(new Selftask("name", "descript", time5, duration5));

        List<Task> list = taskManager.getPrioritizedTasks();
        assertEquals(2, list.get(0).getId());
        assertEquals(3, list.get(1).getId());
        assertEquals(5, list.get(2).getId());
        assertEquals(4, list.get(3).getId());
        assertEquals(1, list.get(4).getId());
    }


    @DisplayName("update tasks and check starttime")
    @Tag("timeline")
    @Test
    void updateAndGetPrioritizedTasks() {
        LocalDateTime time1 = LocalDateTime.of(10, 1, 1, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", time1, duration1));

        LocalDateTime time2 = LocalDateTime.of(1, 1, 1, 00, 00);
        Duration duration2 = Duration.ofDays(1);
        Selftask task2 = taskManager.add(new Selftask("name", "descript", time2, duration2));

        List<Task> list = taskManager.getPrioritizedTasks();
        assertEquals(2, list.get(0).getId());
        assertEquals(1, list.get(1).getId());


        LocalDateTime newTime = LocalDateTime.of(0, 1, 1, 00, 00);
        task1.setStartTime(newTime);
        taskManager.update(task1);
        list = taskManager.getPrioritizedTasks();
        assertEquals(1, list.get(0).getId());
        assertEquals(2, list.get(1).getId());
    }

    @DisplayName("empty list of PrioritizedTasks ")
    @Tag("timeline")
    @Test
    void emptyGetPrioritizedTasks() {
        List<Task> list = taskManager.getPrioritizedTasks();
        assertTrue(list.isEmpty());
    }

    @DisplayName("delete tasks and get PrioritizedTasks ")
    @Tag("timeline")
    @Test
    void deleteAndGetPrioritizedTasks() {
        LocalDateTime time1 = LocalDateTime.of(10, 1, 1, 00, 00);
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", time1, duration1));

        LocalDateTime time2 = LocalDateTime.of(1, 1, 1, 00, 00);
        Duration duration2 = Duration.ofDays(1);
        Selftask task2 = taskManager.add(new Selftask("name", "descript", time2, duration2));

        LocalDateTime time3 = LocalDateTime.of(2, 1, 1, 00, 00);
        Duration duration3 = Duration.ofDays(1);
        Selftask task3 = taskManager.add(new Selftask("name", "descript", time3, duration3));

        LocalDateTime time4 = LocalDateTime.of(6, 1, 1, 00, 00);
        Duration duration4 = Duration.ofDays(1);
        Selftask task4 = taskManager.add(new Selftask("name", "descript", time4, duration4));

        LocalDateTime time5 = LocalDateTime.of(5, 1, 1, 00, 00);
        Duration duration5 = Duration.ofDays(1);
        Selftask task5 = taskManager.add(new Selftask("name", "descript", time5, duration5));

        List<Task> list = taskManager.getPrioritizedTasks();
        assertEquals(2, list.get(0).getId());
        assertEquals(3, list.get(1).getId());
        assertEquals(5, list.get(2).getId());
        assertEquals(4, list.get(3).getId());
        assertEquals(1, list.get(4).getId());

        taskManager.delete(task5.getId());
        list = taskManager.getPrioritizedTasks();
        assertEquals(2, list.get(0).getId());
        assertEquals(3, list.get(1).getId());
        assertEquals(4, list.get(2).getId());
        assertEquals(1, list.get(3).getId());
    }

    @DisplayName("add Selftask without start")
    @Tag("add")
    @Test
    void addTSelfTaskWithoutStart() {
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", duration1));
        int id1 = task1.getId();
        assertAll(
                () -> assertFalse(taskManager.get(id1).isTimeDefined()),
                () -> assertEquals(duration1, taskManager.get(id1).getDuration())
        );
    }

    @DisplayName("add Subtask without start")
    @Tag("add")
    @Test
    void addSubTaskWithoutStart() {
        Epictask epic = taskManager.add(new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        Duration duration1 = Duration.ofDays(1);
        Subtask task1 = taskManager.add(new Subtask("name", "descript", duration1, epic.getId()));
        int id1 = task1.getId();
        assertAll(
                () -> assertFalse(taskManager.get(id1).isTimeDefined()),
                () -> assertEquals(duration1, taskManager.get(id1).getDuration())
        );
    }

    @DisplayName("add Selftask with and without start and check timeline")
    @Tag("timeline")
    @Test
    void addSelfTaskWithWithoutStart() {
        LocalDateTime time1 = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", time1, duration1));

        Duration duration2 = Duration.ofDays(3);
        Selftask task2 = taskManager.add(new Selftask("name", "descript", duration2));

        LocalDateTime time3 = LocalDateTime.of(2, 1, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(10);
        Selftask task3 = taskManager.add(new Selftask("name", "descript", time3, duration3));

        List<Task> list = taskManager.getPrioritizedTasks();

        assertAll(
                () -> assertEquals(1, list.get(0).getId()),
                () -> assertEquals(3, list.get(1).getId())
        );
    }

    @DisplayName("add SubTask with and without start and check timeline")
    @Tag("timeline")
    @Test
    void addSubTaskWithWithoutStart() {
        Epictask epic = taskManager.add(new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        int epicId = epic.getId();
        LocalDateTime time1 = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(1);
        Subtask task1 = taskManager.add(new Subtask("name", "descript", time1, duration1, epicId));

        Duration duration2 = Duration.ofDays(3);
        Subtask task2 = taskManager.add(new Subtask("name", "descript", duration2, epicId));

        LocalDateTime time3 = LocalDateTime.of(2, 1, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(10);
        Subtask task3 = taskManager.add(new Subtask("name", "descript", time3, duration3, epicId));

        List<Task> list = taskManager.getPrioritizedTasks();

        assertAll(
                () -> assertEquals(2, list.get(0).getId()),
                () -> assertEquals(4, list.get(1).getId())
        );
    }


    @DisplayName("add Selftask with start, update and check timeline")
    @Tag("timeline")
    @Test
    void addSelfTaskWithStartUpdateTest() {
        LocalDateTime time1 = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        Duration duration1 = Duration.ofDays(1);
        Selftask task1 = taskManager.add(new Selftask("name", "descript", time1, duration1));

        Duration duration2 = Duration.ofDays(3);
        Selftask task2 = taskManager.add(new Selftask("name", "descript", duration2));

        LocalDateTime time3 = LocalDateTime.of(2, 1, 1, 1, 1, 1);
        Duration duration3 = Duration.ofDays(10);
        Selftask task3 = taskManager.add(new Selftask("name", "descript", time3, duration3));
        List<Task> list = taskManager.getPrioritizedTasks();
        assertAll(
                () -> assertEquals(1, list.get(0).getId()),
                () -> assertEquals(3, list.get(1).getId())
        );

        Selftask task3Update = new Selftask("name", "descript", duration3);
        task3Update.setId(task3.getId());
        taskManager.update(task3Update);
        List<Task> updatedList = taskManager.getPrioritizedTasks();
        assertAll(
                () -> assertEquals(1, updatedList.size()),
                () -> assertEquals(1, list.get(0).getId())
        );
    }


}