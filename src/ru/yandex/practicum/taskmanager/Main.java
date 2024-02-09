package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Task;

public class Main {

    public static void main(String[] args) {
        Managers managers = new Managers();
        TaskManager manager = managers.getDefault();
        Selftask task1 = new Selftask("сходить за продуктами", "купить сыр, молоко, творог");
        Selftask task2 = new Selftask("выгулять собаку", "пойти вечером погулять в парк");
        Task task = manager.add(task1);

    }
}