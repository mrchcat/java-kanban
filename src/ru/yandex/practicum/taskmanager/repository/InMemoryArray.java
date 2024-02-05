package ru.yandex.practicum.taskmanager.repository;

import java.util.Arrays;
import java.util.Collection;

public class InMemoryArray<K, V> implements Repository<Integer, V> {

    private final V[] arr;

    public InMemoryArray(int size) {
        if (size > 0) this.arr = (V[]) new Object[size];
        else throw new IllegalArgumentException("Некорректный размер массива");
    }

    @Override
    public void put(Integer key, V value) {
        arr[key.intValue()] = value;
    }

    @Override
    public V get(Integer key) {
        return arr[key.intValue()];
    }

    @Override
    public V delete(Integer key) {
        V value = arr[key.intValue()];
        arr[key.intValue()] = null;
        return value;
    }

    @Override
    public Collection<V> values() {
        return Arrays.asList(arr);
    }

    @Override
    public void clear() {
        Arrays.fill(arr, null);
    }
}
