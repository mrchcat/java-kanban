package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.repository.Repository;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CircularQueue implements HistoryManager {
    private final int size;
    private int pos;
    private int length;
    private final Repository<Integer, Task> data;

    public CircularQueue(int size, Repository<Integer, Task> data) {
        this.size = size;
        if (size > 0) this.data = data;
        else {
            throw new IllegalArgumentException("Некорректный размер очереди");
        }
        pos = 0;
        length = 0;
    }

    @Override
    public void put(Task item) {
        data.put(pos, item);
        pos = (pos + 1) % size;
        if (length < size) length++;
    }

    @Override
    public List<Task> getHistory() {
        if (length == 0) return Collections.emptyList();
        else {
            int pointer = pos;
            Task item;
            ArrayList<Task> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                pointer = (pointer - 1 + size) % size;
                item = data.get(pointer);
                list.add(item);
            }
            return list;
        }
    }

    @Override
    public void clear() {
        pos = 0;
        length = 0;
    }
}
