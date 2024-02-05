package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.enums.Status;
import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Epictask;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Subtask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Selftask task1 = taskManager.add(new Selftask("сходить в магазин", "хлеб, колбаса, сыр"));
        Epictask task2 = taskManager.add(new Epictask("сходить на рыбалку", "удочки, черви, водка"));
        Subtask task3 = taskManager.add(new Subtask("купить удочки", "спиннинг", task2.getId()));
        Subtask task4 = taskManager.add(new Subtask("набрать червей", "дождевых", task2.getId()));
        Subtask task5 = taskManager.add(new Subtask("водка", "Белуга 0,7", task2.getId()));
        List<Task> list = taskManager.getAll();
        System.out.println(list);
        System.out.println(taskManager.getAllSubs(task2.getId()));
        taskManager.delete(task1.getId());
        list = taskManager.getAll();
        System.out.println(list);
        List<Subtask> sublist = taskManager.getAllSubs(task2.getId());
        System.out.println(sublist);
        System.out.println(taskManager.get(2));
        task1.setStatus(Status.DONE);
        task1.setName("dddd");
        System.out.println(taskManager.get(1));
        taskManager.update(task1);
        System.out.println(taskManager.get(1));
        task1.setId(-1);
        taskManager.update(task1);
        System.out.println(taskManager.get(1));
        Selftask taskN = null;
        taskManager.update(taskN);
        System.out.println(taskManager.add(taskN));
        task2.setName("ssss");
        task2.setDescription("dscds");
        task2.setStatus(Status.DONE);
        System.out.println(taskManager.update(task2));
        task3.setStatus(Status.IN_PROGRESS);
        System.out.println(taskManager.update(task3));
        System.out.println(taskManager.get(2));
        task3.setStatus(Status.NEW);
        System.out.println(taskManager.update(task3));
        System.out.println(taskManager.get(2));
        task3.setStatus(Status.DONE);
        task4.setStatus(Status.DONE);
        task5.setStatus(Status.DONE);
        taskManager.update(task3);
        taskManager.update(task4);
        taskManager.update(task5);
        System.out.println(taskManager.get(2));
        list = taskManager.getAll();
        System.out.println(list);
        task5.setStatus(Status.NEW);
        taskManager.update(task5);
        System.out.println(taskManager.get(2));
        taskManager.delete(task3.getId());
        taskManager.delete(task4.getId());
        taskManager.delete(task5.getId());
        list = taskManager.getAll();
        System.out.println(list);
        Subtask task6 = new Subtask("ss", "ssdd", 2);
        task6 = taskManager.add(task6);
        System.out.println(taskManager.get(6));
        System.out.println(taskManager.get(2));
        task6.setStatus(Status.DONE);
        taskManager.update(task6);
        System.out.println(taskManager.get(6));
        list = taskManager.getAll();
        System.out.println(list);
        taskManager.clear();
        list = taskManager.getAll();
        System.out.println(list);
    }
}