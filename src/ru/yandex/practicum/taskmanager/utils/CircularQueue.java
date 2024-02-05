package ru.yandex.practicum.taskmanager.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CircularQueue<T> {

    private final int size;
    private final T[] arr;
    private int pos;

    public CircularQueue(int size) {
        this.size = size;
        if (size > 0) this.arr = (T[]) new Object[size];
        else throw new IllegalArgumentException("Некорректный размер очереди");
        pos = 0;
    }

    public void put(T item) {
        arr[pos] = item;
        pos = (pos + 1) % size;
    }

    public List<T> getAll() {
        int pointer = pos;
        T item;
        ArrayList<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pointer = (pointer - 1 + size) % size;
            item = arr[pointer];
            if (item != null) list.add(item);
        }
        if (list.isEmpty()) return Collections.emptyList();
        else return list;
    }

    public int getSize() {
        return size;
    }
}
