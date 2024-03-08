package ru.yandex.practicum.taskmanager.service;

import enums.Status;
import enums.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import repository.InMemoryMap;
import repository.Repository;
import service.RegularTaskManager;
import service.TaskManager;
import tasks.Epictask;
import tasks.Selftask;
import tasks.Subtask;
import tasks.Task;
import utils.Generator;
import utils.HistoryManager;
import utils.SerialGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static service.Managers.getDefaultHistory;

class RegularTaskManagerTest {
    TaskManager taskManager;

    static Stream<Selftask> getSelfTasks() {
        return Stream.of(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог"),
                new Selftask("выгулять собаку", "пойти вечером погулять в парк"),
                new Selftask("скачать сериал", "Игра престолов"),
                new Selftask("работать", "работу"),
                new Selftask("смотреть на закат", "и на рассвет")
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
        taskManager = new RegularTaskManager(tasks, subordinates, generator, history);
    }

    @DisplayName("добавляем и считываем задачи типа Selftask")
    @Tag("add")
    @ParameterizedTest
    @MethodSource("getSelfTasks")
    void addAndGetSelfTaskTest(Selftask task) {
        Task addedTask = taskManager.add(task);
        int id = addedTask.getId();

        // проверяем, что метод "add" возвращает корректные таски
        assertAll(
                () -> assertInstanceOf(Selftask.class, addedTask),
                () -> assertSame(Type.SELF, addedTask.getType()),
                () -> assertEquals(task.getName(), addedTask.getName()),
                () -> assertEquals(task.getDescription(), addedTask.getDescription())
        );
        Task gettedTask = taskManager.get(id);

        // проверяем, что в базе сохранены корректные таски, вызывая каждый по id
        assertAll(
                () -> assertEquals(id, gettedTask.getId()),
                () -> assertInstanceOf(Selftask.class, gettedTask),
                () -> assertSame(Type.SELF, gettedTask.getType()),
                () -> assertEquals(task.getName(), gettedTask.getName()),
                () -> assertEquals(task.getDescription(), gettedTask.getDescription())
        );
    }

    @DisplayName("добавляем null Selftask")
    @Tag("add")
    @ParameterizedTest
    @NullSource
    void addNullSelfTaskTest(Selftask task) {
        assertNull(taskManager.add(task));
    }

    @DisplayName("добавляем null Epictask")
    @Tag("add")
    @ParameterizedTest
    @NullSource
    void addNullEpicTaskTest(Epictask task) {
        assertNull(taskManager.add(task));
    }

    @DisplayName("добавляем null Subtask")
    @Tag("add")
    @ParameterizedTest
    @NullSource
    void addNullSubTaskTest(Subtask task) {
        assertNull(taskManager.add(task));
    }

    @DisplayName("добавляем несколько одиночных задач сразу и проверяем, что они сохранились")
    @Tag("add")
    @Test
    void getAllSelfTaskTest() {
        HashSet<Task> set = new HashSet<>();
        List<Selftask> taskList = List.of(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог"),
                new Selftask("выгулять собаку", "пойти вечером погулять в парк"),
                new Selftask("скачать сериал", "Игра престолов"),
                new Selftask("работать", "работу"),
                new Selftask("смотреть на закат", "и на рассвет")
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

    @DisplayName("добавляем и считываем эпические задачи (без подзадач)")
    @Tag("add")
    @ParameterizedTest
    @MethodSource("getEpicTasks")
    void addAndGetEpicTest(Epictask task) {
        Task addedTask = taskManager.add(task);
        int id = addedTask.getId();

        // проверяем, что метод "add" возвращает корректные таски
        assertAll(
                () -> assertInstanceOf(Epictask.class, addedTask),
                () -> assertSame(Type.EPIC, addedTask.getType()),
                () -> assertEquals(task.getName(), addedTask.getName()),
                () -> assertEquals(task.getDescription(), addedTask.getDescription())
        );
        Task gettedTask = taskManager.get(id);

        // проверяем, что в базе сохранены корректные таски, вызывая каждый по id
        assertAll(
                () -> assertEquals(id, gettedTask.getId()),
                () -> assertInstanceOf(Epictask.class, gettedTask),
                () -> assertSame(Type.EPIC, gettedTask.getType()),
                () -> assertEquals(task.getName(), gettedTask.getName()),
                () -> assertEquals(task.getDescription(), gettedTask.getDescription())
        );
    }

    @DisplayName("добавляем несколько одиночных эпических задач сразу и проверяем, что они сохранились")
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

    @DisplayName("Проверяем возврат списка подзадач эпика")
    @Tag("subList")
    @Test
    void getAllSubsFromEpicFilledTest() {
        Epictask epic = new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго");
        epic = taskManager.add(epic);
        Subtask sub1 = new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic.getId());
        Subtask sub2 = new Subtask("наловить червей", "200 шт.", epic.getId());
        Subtask sub3 = new Subtask("купить алкоголь", "батя обещал самогон", epic.getId());
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

    @DisplayName("Проверяем возврат списка подзадач эпика при отсутствии подзадач")
    @Tag("subList")
    @Test
    void getAllSubsFromEpicEmptyTest() {
        Epictask epic = new Epictask("отпуск", "отпуск конец августа");
        epic = taskManager.add(epic);
        List<Subtask> list = taskManager.getAllSubs(epic.getId());
        assertEquals(Collections.emptyList(), list);
    }

    @DisplayName("Добавляем эпик и подзадачи и проверяем возврат полного списка")
    @Tag("add")
    @Test
    void getSubTaskFromWholeListTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic.getId()));
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.", epic.getId()));
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон", epic.getId()));
        List<Task> getAllList = taskManager.getAll();
        assertAll(
                () -> assertEquals(4, getAllList.size()),
                () -> assertTrue(getAllList.contains(epic)),
                () -> assertTrue(getAllList.contains(sub1)),
                () -> assertTrue(getAllList.contains(sub2)),
                () -> assertTrue(getAllList.contains(sub3))
        );
    }

    @DisplayName("Возврат задачи, которой нет в списке")
    @Tag("get")
    @Test
    void getNotExistingTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        assertAll(
                () -> assertNull(taskManager.get(600)),
                () -> assertNull(taskManager.get(null))
        );
    }

    @DisplayName("Возврат Selftask, которой нет в списке")
    @Tag("get")
    @Test
    void getNotExistingSelfTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        assertAll(
                () -> assertNull(taskManager.getSelftask(600)),
                () -> assertNull(taskManager.getSelftask(null))
        );
    }

    @DisplayName("Возврат Epictask, которой нет в списке")
    @Tag("get")
    @Test
    void getNotExistingEpicTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        assertAll(
                () -> assertNull(taskManager.getEpic(600)),
                () -> assertNull(taskManager.getEpic(null))
        );
    }

    @DisplayName("Возврат Subtask, которой нет в списке")
    @Tag("get")
    @Test
    void getNotExistingSubTaskTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        assertAll(
                () -> assertNull(taskManager.getSubtask(600)),
                () -> assertNull(taskManager.getSubtask(null))
        );
    }

    @DisplayName("Удаляем Selftask, проверяем, что оно исчезла из базы")
    @Tag("delete")
    @Test
    void deleteSelfTest() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        Selftask task = taskManager.add(new Selftask("все проходит", "и это пройдет"));
        int id = task.getId();
        assertEquals(task, taskManager.get(id));
        taskManager.delete(id);
        assertNull(taskManager.get(id));
    }

    @DisplayName("Удаляем Epic, проверяем, что из базы пропал и эпик и подзадачи")
    @Tag("delete")
    @Test
    void deleteEpicTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic.getId()));
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.", epic.getId()));
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон", epic.getId()));
        taskManager.delete(epic.getId());
        assertNull(taskManager.get(epic.getId()));
        assertNull(taskManager.get(sub1.getId()));
        assertNull(taskManager.get(sub2.getId()));
        assertNull(taskManager.get(sub3.getId()));
    }

    @DisplayName("Удаляем подзадачу, проверяем, что из базы пропала только подзадача")
    @Tag("delete")
    @Test
    void deleteSubTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic.getId()));
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.", epic.getId()));
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон", epic.getId()));
        taskManager.delete(sub1.getId());
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

    @DisplayName("Полностью очищаем менеджер")
    @Tag("clear")
    @Test
    void Clear() {
        taskManager.add(new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        taskManager.add(new Selftask("все проходит", "и это пройдет"));
        assertEquals(3, taskManager.getAll().size());
        taskManager.clear();
        assertEquals(0, taskManager.getAll().size());
    }

    @DisplayName("проверка, что при обновлении меняется имя, описание, статус для Selftask")
    @Tag("update")
    @Test
    void updateSelfTask() {
        String oldName = "Иван";
        String newName = "Петр";
        String oldDesc = "Иванов";
        String newDesc = "Петров";
        Status newStatus = Status.IN_PROGRESS;

        Selftask task = new Selftask(oldName, oldDesc);
        task = taskManager.add(task);
        assertEquals(task.getName(), oldName);
        assertEquals(task.getDescription(), oldDesc);
        assertEquals(task.getStatus(), Status.NEW);

        task.setName(newName);
        task.setDescription(newDesc);
        task.setStatus(newStatus);
        Task updatedTask = taskManager.update(task);
        assertEquals(task, updatedTask);
        assertEquals(updatedTask.getName(), newName);
        assertEquals(updatedTask.getDescription(), newDesc);
        assertEquals(updatedTask.getStatus(), newStatus);
    }

    @Tag("update")
    @DisplayName("проверка, что при обновлении меняется имя, описание, статус для EpicTask")
    @Test
    void updateEpicTaskTest() {
        String oldName = "Иван";
        String newName = "Петр";
        String oldDesc = "Иванов";
        String newDesc = "Петров";
        Status newStatus = Status.IN_PROGRESS;

        Epictask task = new Epictask(oldName, oldDesc);
        task = taskManager.add(task);
        assertEquals(task.getName(), oldName);
        assertEquals(task.getDescription(), oldDesc);
        assertEquals(task.getStatus(), Status.NEW);

        task.setName(newName);
        task.setDescription(newDesc);
        task.setStatus(newStatus);
        Task updatedTask = taskManager.update(task);
        assertEquals(task, updatedTask);
        assertEquals(updatedTask.getName(), newName);
        assertEquals(updatedTask.getDescription(), newDesc);
        assertEquals(updatedTask.getStatus(), Status.NEW);
    }

    @Tag("update")
    @DisplayName("проверка, что при обновлении меняется имя, описание, статус для SubTask")
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

        Subtask task = new Subtask(oldName, oldDesc, epicId);
        task = taskManager.add(task);
        assertEquals(task.getName(), oldName);
        assertEquals(task.getDescription(), oldDesc);
        assertEquals(task.getStatus(), Status.NEW);

        task.setName(newName);
        task.setDescription(newDesc);
        task.setStatus(newStatus);
        Task updatedTask = taskManager.update(task);
        assertEquals(task, updatedTask);
        assertAll(() -> {
            assertEquals(updatedTask.getName(), newName);
            assertEquals(updatedTask.getDescription(), newDesc);
            assertEquals(updatedTask.getStatus(), newStatus);
        });
    }

    @DisplayName("равенство SelfTask с одинаковыми id")
    @Tag("equality")
    @Test
    void checkIfEqualSelfTasksTest() {
        Task selfTask1 = new Selftask("ss", "ffff");
        selfTask1.setId(555);
        Task selfTask2 = new Selftask("arsgfs", "cdb gfv");
        selfTask2.setId(555);
        assertEquals(selfTask1, selfTask2);
    }

    @DisplayName("равенство EpicTask с одинаковыми id")
    @Tag("equality")
    @Test
    void checkIfEqualEpicTasksTest() {
        Epictask epicTask1 = new Epictask("ss", "ffff");
        epicTask1.setId(555);
        Epictask epicTask2 = new Epictask("arsgfs", "cdb gfv");
        epicTask2.setId(555);
        assertEquals(epicTask1, epicTask2);
    }

    @DisplayName("равенство SubTask с одинаковыми id")
    @Tag("equality")
    @Test
    void checkIfEqualSubTasksTest() {
        Subtask subTask1 = new Subtask("ss", "ffff", 1);
        subTask1.setId(555);
        Subtask subTask2 = new Subtask("arsgfs", "cdb gfv", 3);
        subTask2.setId(555);
        assertEquals(subTask1, subTask2);
    }

    @DisplayName("одиночная задача не может быть эпиком при добавлении подзадачи")
    @Tag("equality")
    @Test
    void SelfCanNotBeEpicTest() {
        Selftask self = new Selftask("sss", "sss");
        self = taskManager.add(self);
        int selfId = self.getId();
        Subtask subTask = new Subtask("ss", "ffff", selfId);
        assertNull(taskManager.add(subTask));
    }

    @DisplayName("подзадача не может быть эпиком при добавлении подзадачи")
    @Tag("SubEpic")
    @Test
    void SubCanNotBeEpicTest() {
        Epictask epic = new Epictask("ss", "ffff");
        epic = taskManager.add(epic);
        Subtask sub1 = new Subtask("sss", "sss", epic.getId());
        sub1 = taskManager.add(sub1);
        Subtask sub2 = new Subtask("sss", "sss", sub1.getId());
        assertNull(taskManager.add(sub2));
    }

    @DisplayName("проверка, что id присваивается в менеджере, а не передается с задачей для SelfTask")
    @Tag("SubEpic")
    @Test
    void isIdGetFromManagerForSelfTaskTest() {
        int fakeId = -1_000_000;
        Selftask self = new Selftask("sss", "sss");
        self.setId(fakeId);
        self = taskManager.add(self);
        assertNotEquals(fakeId, self.getId());
    }

    @DisplayName("проверка, что id присваивается в менеджере, а не передается с задачей для EpicTask")
    @Tag("id")
    @Test
    void isIdGetFromManagerForEpicTaskTest() {
        int fakeId = -1_000_000;
        Epictask task = new Epictask("sss", "sss");
        task.setId(fakeId);
        task = taskManager.add(task);
        assertNotEquals(fakeId, task.getId());
    }

    @DisplayName("проверка, что id присваивается в менеджере, а не передается с задачей для SubTask")
    @Tag("id")
    @Test
    void isIdGetFromManagerForSubTaskTest() {
        int fakeId = -1_000_000;
        Epictask epic = new Epictask("sss", "sss");
        epic = taskManager.add(epic);
        Subtask sub = new Subtask("sss", "sss", epic.getId());
        sub.setId(fakeId);
        sub = taskManager.add(sub);
        assertNotEquals(fakeId, sub.getId());
    }

    @DisplayName("проверка изменения статуса Epic при изменении статуса подзадач")
    @Tag("SUbEpic")
    @Test
    void EpicStatusTest() {
        Epictask epic = taskManager.add(new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        Subtask sub1 = taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic.getId()));
        Subtask sub2 = taskManager.add(
                new Subtask("наловить червей", "200 шт.", epic.getId()));
        Subtask sub3 = taskManager.add(
                new Subtask("купить алкоголь", "батя обещал самогон", epic.getId()));
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

    @DisplayName("проверка, что список не обновляется при добавлении, удалении и обновлении задач")
    @Tag("history")
    @Test
    void HistoryNotUpdatedTest() {
        Epictask epic = taskManager.add(
                new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго"));
        taskManager.add(
                new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic.getId()));
        Subtask sub2 = taskManager.add(new Subtask("наловить червей", "200 шт.", epic.getId()));
        taskManager.add(new Subtask("купить алкоголь", "батя обещал самогон", epic.getId()));
        Selftask self1 = taskManager.add(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        taskManager.add(new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        Selftask self3 = taskManager.add(new Selftask("скачать сериал", "Игра престолов"));
        assertTrue(taskManager.getHistory().isEmpty());

        taskManager.delete(self3.getId());
        taskManager.delete(sub2.getId());
        taskManager.delete(epic.getId());
        assertTrue(taskManager.getHistory().isEmpty());

        Selftask newTask = new Selftask("bla", "bla");
        newTask.setId(self1.getId());
        taskManager.update(newTask);
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("проверка, что список обновляется при get")
    @Tag("history")
    @Test
    void HistoryUpdatedTest() {
        Selftask self1 = taskManager.add(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог"));
        Selftask self2 = taskManager.add(
                new Selftask("выгулять собаку", "пойти вечером погулять в парк"));
        Selftask self3 = taskManager.add(
                new Selftask("скачать сериал", "Игра престолов"));
        taskManager.get(self1.getId());
        taskManager.get(self2.getId());
        Task[] correctList = {self2, self1};
        assertArrayEquals(correctList, taskManager.getHistory().toArray());
    }
}
