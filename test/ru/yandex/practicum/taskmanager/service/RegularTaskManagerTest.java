package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    void addAndGetSelfTaskTest() {
        Task task;
        int id;
        for (var selfTask : selfTasks) {
            task = taskManager.add(selfTask);
            id = task.getId();
            assertInstanceOf(Selftask.class, task);
            assertTrue(task.getType() == Type.SELF);
            assertTrue(task.getName().equals(selfTask.getName()));
            assertTrue(task.getDescription().equals(selfTask.getDescription()));

            task = taskManager.get(id);
            assertEquals(id, task.getId());
            assertInstanceOf(Selftask.class, task);
            assertTrue(task.getType() == Type.SELF);
            assertTrue(task.getName().equals(selfTask.getName()));
            assertTrue(task.getDescription().equals(selfTask.getDescription()));
        }
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
            assertTrue(task.getType() == Type.EPIC);
            assertTrue(task.getName().equals(epicTask.getName()));
            assertTrue(task.getDescription().equals(epicTask.getDescription()));

            task = taskManager.get(id);
            assertEquals(id, task.getId());
            assertInstanceOf(Epictask.class, task);
            assertTrue(task.getType() == Type.EPIC);
            assertTrue(task.getName().equals(epicTask.getName()));
            assertTrue(task.getDescription().equals(epicTask.getDescription()));
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
        Task task;
        int id;
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
        assertTrue(newTask.getName().equals(updated.getName()));
        assertTrue(newTask.getDescription().equals(updated.getDescription()));
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
}