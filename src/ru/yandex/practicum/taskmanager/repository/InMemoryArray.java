package ru.yandex.practicum.taskmanager.repository;

import java.util.Arrays;
import java.util.Collection;

public class InMemoryArray<K, V> implements Repository<Integer, V> {
    private final V[] data;

    public InMemoryArray(int size) {
        if (size > 0) this.data = (V[]) new Object[size];
        else {
            throw new IllegalArgumentException("Некорректный размер массива, переданного в InMemoryArray ");
        }
    }

    @Override
    public void put(Integer key, V value) {
        data[key] = value;
    }

    @Override
    public V get(Integer key) {
        return data[key];
    }

    @Override
    public V delete(Integer key) {
        V value = data[key];
        data[key] = null;
        return value;
    }

    @Override
    public Collection<V> values() {
        return Arrays.asList(data);
    }

    @Override
    public void clear() {
        Arrays.fill(data, null);
    }
}
