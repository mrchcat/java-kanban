package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(5);
        for (Integer integer : arr) {
            System.out.println(integer);
        }
    }
}