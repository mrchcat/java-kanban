package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.enums.*;
import ru.yandex.practicum.taskmanager.repository.CacheBase;
import ru.yandex.practicum.taskmanager.service.Manager;
import ru.yandex.practicum.taskmanager.utils.SerialGenerator;
import ru.yandex.practicum.taskmanager.tasks.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Manager init() {
        var tasks = new CacheBase<Integer, Task>();
        var subordinates = new CacheBase<Integer, ArrayList<Integer>>();
        var generator = new SerialGenerator(1);
        return new Manager(tasks, subordinates, generator);
    }

    public static void main(String[] args) {
        Manager manager = init();

        Selftask task1 = manager.add(new Selftask("сходить в магазин", "хлеб, колбаса, сыр"));
        Epictask task2 = manager.add(new Epictask("сходить на рыбалку", "удочки, черви, водка"));
        Subtask task3 = manager.add(new Subtask("купить удочки", "спиннинг", task2.getId()));
        Subtask task4 = manager.add(new Subtask("набрать червей", "дождевых", task2.getId()));
        Subtask task5 = manager.add(new Subtask("водка", "Белуга 0,7", task2.getId()));
        List<Task> list = manager.getAll();
        System.out.println(list);
        System.out.println(manager.getAllSubs(task2.getId()));
        manager.delete(task1.getId());
        list = manager.getAll();
        System.out.println(list);
        List<Subtask> sublist = manager.getAllSubs(task2.getId());
        System.out.println(sublist);
        System.out.println(manager.get(2));
        task1.setStatus(Status.DONE);
        task1.setName("dddd");
        System.out.println(manager.get(1));
        manager.update(task1);
        System.out.println(manager.get(1));
        task1.setId(-1);
        manager.update(task1);
        System.out.println(manager.get(1));
        Selftask taskN = null;
        manager.update(taskN);
        System.out.println(manager.add(taskN));
        task2.setName("ssss");
        task2.setDescription("dscds");
        task2.setStatus(Status.DONE);
        System.out.println(manager.update(task2));
        task3.setStatus(Status.IN_PROGRESS);
        System.out.println(manager.update(task3));
        System.out.println(manager.get(2));
        task3.setStatus(Status.NEW);
        System.out.println(manager.update(task3));
        System.out.println(manager.get(2));
        task3.setStatus(Status.DONE);
        task4.setStatus(Status.DONE);
        task5.setStatus(Status.DONE);
        manager.update(task3);
        manager.update(task4);
        manager.update(task5);
        System.out.println(manager.get(2));
        list = manager.getAll();
        System.out.println(list);
        task5.setStatus(Status.NEW);
        manager.update(task5);
        System.out.println(manager.get(2));
        manager.delete(task3.getId());
        manager.delete(task4.getId());
        manager.delete(task5.getId());
        list = manager.getAll();
        System.out.println(list);
        Subtask task6 = new Subtask("ss", "ssdd", 2);
        task6 = manager.add(task6);
        System.out.println(manager.get(6));
        System.out.println(manager.get(2));
        task6.setStatus(Status.DONE);
        manager.update(task6);
        System.out.println(manager.get(6));
        list = manager.getAll();
        System.out.println(list);
        manager.clear();
        list = manager.getAll();
        System.out.println(list);
    }
}