package ru.yandex.practicum.taskmanager.repository;

import java.util.Collection;
import java.util.HashMap;

public class InMemoryMap<K, V> implements Repository<K, V> {
    private final HashMap<K, V> base;

    public InMemoryMap() {
        this.base = new HashMap<>();
    }

    @Override
    public void put(K key, V value) {
        base.put(key, value);
    }

    @Override
    public V get(K key) {
        return base.get(key);
    }

    @Override
    public V delete(K key) {
        return base.remove(key);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public Collection<V> values() {
        return base.values();
    }
}
