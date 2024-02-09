package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.repository.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CircularQueue<T> implements HistoryManager<T> {
    private final int size;
    private int pos;
    private int length;
    Repository<Integer, T> arr;

    public CircularQueue(int size, Repository<Integer, T> arr) {
        this.size = size;
        if (size > 0) this.arr = arr;
        else throw new IllegalArgumentException("Некорректный размер очереди");
        pos = 0;
        length = 0;
    }

    @Override
    public void put(T item) {
        arr.put(pos, item);
        pos = (pos + 1) % size;
        if (length < size) length++;
    }

    @Override
    public List<T> getAll() {
        if (length == 0) return Collections.emptyList();
        else {
            int pointer = pos;
            T item;
            ArrayList<T> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                pointer = (pointer - 1 + size) % size;
                item = arr.get(pointer);
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
