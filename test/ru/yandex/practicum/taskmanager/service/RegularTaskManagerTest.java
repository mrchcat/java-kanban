package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.taskmanager.enums.Status;
import ru.yandex.practicum.taskmanager.enums.Type;
import ru.yandex.practicum.taskmanager.repository.InMemoryMap;
import ru.yandex.practicum.taskmanager.tasks.Epictask;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Subtask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.taskmanager.service.Managers.getDefaultHistory;

class RegularTaskManagerTest {
    static TaskManager taskManager;
    static ArrayList<Selftask> selfTasks = new ArrayList<>();
    static ArrayList<Epictask> epicTasks = new ArrayList<>();

    static private void addAddEpicAndSubsToManager() {
        Epictask epic1 = new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго");
        epic1 = taskManager.add(epic1);
        Subtask sub11 = new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic1.getId());
        Subtask sub12 = new Subtask("наловить червей", "200 шт.", epic1.getId());
        Subtask sub13 = new Subtask("купить алкоголь", "батя обещал самогон", epic1.getId());
        taskManager.add(sub11);
        taskManager.add(sub12);
        taskManager.add(sub13);

        Epictask epic2 = new Epictask("жениться", "родители просят внуков");
        epic2 = taskManager.add(epic2);
        Subtask sub21 = new Subtask("найти жену", "клуб Фараон", epic2.getId());
        Subtask sub22 = new Subtask("взять кредит", "Быстроденьги", epic2.getId());
        Subtask sub23 = new Subtask("сыграть свадьбу", "дядю Лешу не приглашаем", epic2.getId());
        Subtask sub24 = new Subtask("медовый месяц", "выбрать Турция или Египет", epic2.getId());
        taskManager.add(sub21);
        taskManager.add(sub22);
        taskManager.add(sub23);
        taskManager.add(sub24);

        Epictask epic3 = new Epictask("отпуск", "отпуск конец августа");
        taskManager.add(epic3);
    }

    @BeforeEach
    public void initTaskManager() {
        int START_ID_BY_DEFAULT = 1;
        var tasks = new InMemoryMap<Integer, Task>();
        var subordinates = new InMemoryMap<Integer, ArrayList<Integer>>();
        var generator = new SerialGenerator(START_ID_BY_DEFAULT);
        var history = getDefaultHistory();
        taskManager = new RegularTaskManager(tasks, subordinates, generator, history);

        Selftask task1 = new Selftask("сходить за продуктами", "купить сыр, молоко, творог");
        Selftask task2 = new Selftask("выгулять собаку", "пойти вечером погулять в парк");
        Selftask task3 = new Selftask("скачать сериал", "Игра престолов");
        Selftask task4 = new Selftask("работать", "работу");
        Selftask task5 = new Selftask("смотреть на закат", "и на рассвет");
        selfTasks.addAll(Arrays.asList(task1, task2, task3, task4, task5));

        Epictask epic1 = new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго");
        Epictask epic2 = new Epictask("жениться", "родители просят внуков");
        Epictask epic3 = new Epictask("отпуск", "отпуск конец августа");
        epicTasks.addAll(Arrays.asList(epic1, epic2, epic3));
    }

    private void addAllSelfTasksToManager() {
        for (var selfTask : selfTasks) {
            taskManager.add(selfTask);
        }
    }

    private void addAllEpicTasksToManager() {
        for (var task : epicTasks) {
            taskManager.add(task);
        }
    }

    static Stream<Selftask> getSelfTasks() {
        return Stream.of(
                new Selftask("сходить за продуктами", "купить сыр, молоко, творог"),
                new Selftask("выгулять собаку", "пойти вечером погулять в парк"),
                new Selftask("скачать сериал", "Игра престолов"),
                new Selftask("работать", "работу"),
                new Selftask("смотреть на закат", "и на рассвет")
        );
    }

    ;

    @DisplayName("добавляем и считываем задачи типа Selftask")
    @ParameterizedTest
    @MethodSource("getSelfTasks")
    void addAndGetSelfTaskTest(Selftask task) {
        Task addedTask = taskManager.add(task);
        int id = addedTask.getId();

        assertAll(
                () -> assertInstanceOf(Selftask.class, addedTask),
                () -> assertSame(Type.SELF, addedTask.getType()),
                () -> assertEquals(task.getName(), addedTask.getName()),
                () -> assertEquals(task.getDescription(), addedTask.getDescription())
        );
        Task gettedTask = taskManager.get(id);

        assertAll(
                () -> assertEquals(id, gettedTask.getId()),
                () -> assertInstanceOf(Selftask.class, gettedTask),
                () -> assertSame(Type.SELF, gettedTask.getType()),
                () -> assertEquals(task.getName(), gettedTask.getName()),
                () -> assertEquals(task.getDescription(), gettedTask.getDescription())
        );
    }


    @Test
    void getAllSelfTaskTheSamePower() {
        addAllSelfTasksToManager();
        List<Task> list = taskManager.getAll();
        assertEquals(list.size(), selfTasks.size());
    }

    @Test
    void addAndGetEpic() {
        Task task;
        int id;
        for (var epicTask : epicTasks) {
            task = taskManager.add(epicTask);
            id = task.getId();
            assertInstanceOf(Epictask.class, task);
            assertSame(task.getType(), Type.EPIC);
            assertEquals(task.getName(), epicTask.getName());
            assertEquals(task.getDescription(), epicTask.getDescription());

            task = taskManager.get(id);
            assertEquals(id, task.getId());
            assertInstanceOf(Epictask.class, task);
            assertSame(task.getType(), Type.EPIC);
            assertEquals(task.getName(), epicTask.getName());
            assertEquals(task.getDescription(), epicTask.getDescription());
        }
    }

    @Test
    void getAllEpicTaskTheSamePower() {
        addAllEpicTasksToManager();
        List<Task> list = taskManager.getAll();
        assertEquals(list.size(), epicTasks.size());
    }

    @Test
    void getAllSubsFromEpicFilled() {
        Epictask epic1 = new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго");
        epic1 = taskManager.add(epic1);
        Subtask sub11 = new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic1.getId());
        Subtask sub12 = new Subtask("наловить червей", "200 шт.", epic1.getId());
        Subtask sub13 = new Subtask("купить алкоголь", "батя обещал самогон", epic1.getId());
        Task task1 = taskManager.add(sub11);
        Task task2 = taskManager.add(sub12);
        Task task3 = taskManager.add(sub13);
        List<Subtask> list = taskManager.getAllSubs(epic1.getId());
        assertEquals(list.size(), 3);
        assertTrue(list.contains(task1));
        assertTrue(list.contains(task2));
        assertTrue(list.contains(task3));
    }

    @Test
    void getAllSubsFromEpicEmpty() {
        Epictask epic3 = new Epictask("отпуск", "отпуск конец августа");
        epic3 = taskManager.add(epic3);
        List<Subtask> list = taskManager.getAllSubs(epic3.getId());
        assertEquals(list, Collections.emptyList());
    }

    @Test
    void getSubTaskFromWholeList() {
        addAddEpicAndSubsToManager();
        List<Task> list = taskManager.getAll();
        int count = 0;
        for (Task t : list) {
            if (t.getType() == Type.SUBTASK) count++;
        }
        assertEquals(count, 7);
    }

    @Test
    void deleteTest() {
        addAddEpicAndSubsToManager();
        Selftask task = new Selftask("все проходит", "и это пройдет");
        task = taskManager.add(task);
        int id = task.getId();
        assertEquals(task, taskManager.get(id));
        taskManager.delete(id);
        assertNotEquals(task, taskManager.get(id));
    }

    @Test
    void updateTest() {
        addAllSelfTasksToManager();
        List<Task> list = taskManager.getAll();
        Task task = list.get(1);
        int id = task.getId();
        Selftask newTask = new Selftask("new", "new");
        newTask.setId(id);
        taskManager.update(newTask);
        Task updated = taskManager.get(id);
        assertEquals(newTask.getName(), updated.getName());
        assertEquals(newTask.getDescription(), updated.getDescription());
    }

    @Test
    void History() {
        addAllSelfTasksToManager();
        addAddEpicAndSubsToManager();
        List<Task> list = taskManager.getAll();
        taskManager.get(list.get(0).getId());
        taskManager.get(list.get(1).getId());
        taskManager.get(list.get(4).getId());
        taskManager.get(list.get(7).getId());
        taskManager.get(list.get(7).getId());
        var task10 = taskManager.get(list.get(14).getId());
        var task9 = taskManager.get(list.get(6).getId());
        var task8 = taskManager.get(list.get(13).getId());
        var task7 = taskManager.get(list.get(1).getId());
        var task6 = taskManager.get(list.get(4).getId());
        var task5 = taskManager.get(list.get(7).getId());
        var task4 = taskManager.get(list.get(7).getId());
        var task3 = taskManager.get(list.get(14).getId());
        var task2 = taskManager.get(list.get(6).getId());
        var task1 = taskManager.get(list.get(13).getId());
        List<Task> history = taskManager.getHistory();
        Task[] arr1 = {history.get(0), history.get(1), history.get(2), history.get(3), history.get(4), history.get(5),
                history.get(6), history.get(7), history.get(8), history.get(9)};
        Task[] arr2 = {task1, task2, task3, task4, task5, task6, task7, task8, task9, task10};
        assertArrayEquals(arr1, arr2);
    }

    @Test
    void Clear() {
        addAllSelfTasksToManager();
        addAddEpicAndSubsToManager();
        int size = taskManager.getAll().size();
        taskManager.clear();
        int empty = taskManager.getAll().size();
        assertTrue(size > 0);
        assertEquals(empty, 0);
    }

    @Test
    void EpicStatus() {
        addAllSelfTasksToManager();
        Epictask epic1 = new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго");
        epic1 = taskManager.add(epic1);
        int epic1Id = epic1.getId();
        Subtask sub11 = new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic1Id);
        Subtask sub12 = new Subtask("наловить червей", "200 шт.", epic1Id);
        Subtask sub13 = new Subtask("купить алкоголь", "батя обещал самогон", epic1Id);
        Task task1 = taskManager.add(sub11);
        Task task2 = taskManager.add(sub12);
        Task task3 = taskManager.add(sub13);
        assertEquals(epic1.getStatus(), Status.NEW);

        task1.setStatus(Status.DONE);
        taskManager.update(task1);
        assertEquals(taskManager.get(epic1Id).getStatus(), Status.IN_PROGRESS);

        task2.setStatus(Status.DONE);
        taskManager.update(task2);
        assertEquals(taskManager.get(epic1Id).getStatus(), Status.IN_PROGRESS);

        task3.setStatus(Status.DONE);
        taskManager.update(task3);
        assertEquals(taskManager.get(epic1Id).getStatus(), Status.DONE);

        task3.setStatus(Status.IN_PROGRESS);
        taskManager.update(task3);
        assertEquals(taskManager.get(epic1Id).getStatus(), Status.IN_PROGRESS);

        taskManager.delete(task1.getId());
        taskManager.delete(task2.getId());
        taskManager.delete(task3.getId());
        assertEquals(taskManager.get(epic1Id).getStatus(), Status.NEW);
    }

    @Test
    void deleteEpic() {
        Epictask epic1 = new Epictask("пойти на рыбалку", "Селигер, в районе оз Волго");
        epic1 = taskManager.add(epic1);
        int epic1Id = epic1.getId();
        Subtask sub11 = new Subtask("купить удочку", "магазин Охотник, проспект Ленина, 20", epic1Id);
        Subtask sub12 = new Subtask("наловить червей", "200 шт.", epic1Id);
        Subtask sub13 = new Subtask("купить алкоголь", "батя обещал самогон", epic1Id);
        Task task1 = taskManager.add(sub11);
        Task task2 = taskManager.add(sub12);
        Task task3 = taskManager.add(sub13);
        taskManager.delete(epic1Id);
        assertNull(taskManager.get(epic1Id));
        assertNull(taskManager.get(task1.getId()));
        assertNull(taskManager.get(task2.getId()));
        assertNull(taskManager.get(task3.getId()));
    }

    @Test
    void deleteSelf() {
        addAllSelfTasksToManager();
        List<Task> list = taskManager.getAll();
        for (Task task : list) {
            taskManager.delete(task.getId());
            assertNull(taskManager.get(task.getId()));
        }
    }

    @DisplayName("равенство SelfTask с одинаковыми id")
    @Test
    void checkIfEqualSelfTasks() {
        Task selfTask1 = new Selftask("ss", "ffff");
        selfTask1.setId(555);
        Task selfTask2 = new Selftask("arsgfs", "cdb gfv");
        selfTask2.setId(555);
        assertEquals(selfTask1, selfTask2);
    }

    @DisplayName("равенство EpicTask с одинаковыми id")
    @Test
    void checkIfEqualEpicTasks() {
        Epictask epicTask1 = new Epictask("ss", "ffff");
        epicTask1.setId(555);
        Epictask epicTask2 = new Epictask("arsgfs", "cdb gfv");
        epicTask2.setId(555);
        assertEquals(epicTask1, epicTask2);
    }

    @DisplayName("равенство SubTask с одинаковыми id")
    @Test
    void checkIfEqualSubTasks() {
        Subtask subTask1 = new Subtask("ss", "ffff", 1);
        subTask1.setId(555);
        Subtask subTask2 = new Subtask("arsgfs", "cdb gfv", 3);
        subTask2.setId(555);
        assertEquals(subTask1, subTask2);
    }

    @DisplayName("одиночная задача не может быть эпиком при добавлении подзадачи")
    @Test
    void SelfCanNotBeEpic() {
        Selftask self = new Selftask("sss", "sss");
        self = taskManager.add(self);
        int selfId = self.getId();
        Subtask subTask = new Subtask("ss", "ffff", selfId);
        assertNull(taskManager.add(subTask));
    }

    @DisplayName("подзадача не может быть эпиком при добавлении подзадачи")
    @Test
    void SubCanNotBeEpic() {
        Epictask epic = new Epictask("ss", "ffff");
        epic = taskManager.add(epic);
        Subtask sub1 = new Subtask("sss", "sss", epic.getId());
        sub1 = taskManager.add(sub1);
        Subtask sub2 = new Subtask("sss", "sss", sub1.getId());
        assertNull(taskManager.add(sub2));
    }

    @DisplayName("проверка, что id присваивается в менеджере, а не передается с задачей для SelfTask")
    @Test
    void isIdGetFromManagerForSelfTask() {
        int fakeId = -1_000_000;
        Selftask self = new Selftask("sss", "sss");
        self.setId(fakeId);
        self = taskManager.add(self);
        assertNotEquals(fakeId, self.getId());
    }

    @DisplayName("проверка, что id присваивается в менеджере, а не передается с задачей для EpicTask")
    @Test
    void isIdGetFromManagerForEpicTask() {
        int fakeId = -1_000_000;
        Epictask task = new Epictask("sss", "sss");
        task.setId(fakeId);
        task = taskManager.add(task);
        assertNotEquals(fakeId, task.getId());
    }

    @DisplayName("проверка, что id присваивается в менеджере, а не передается с задачей для SubTask")
    @Test
    void isIdGetFromManagerForSubTask() {
        int fakeId = -1_000_000;
        Epictask epic = new Epictask("sss", "sss");
        epic = taskManager.add(epic);
        Subtask sub = new Subtask("sss", "sss", epic.getId());
        sub.setId(fakeId);
        sub = taskManager.add(sub);
        assertNotEquals(fakeId, sub.getId());
    }

    @Tag("update")
    @DisplayName("проверка, что при обновлении меняется имя, описание, статус для Selftask")
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
    void updateEpicTask() {
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
    void updateSubTask() {
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
}