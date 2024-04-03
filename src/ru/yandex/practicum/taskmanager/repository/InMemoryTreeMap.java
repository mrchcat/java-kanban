package ru.yandex.practicum.taskmanager.repository;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class InMemoryTreeMap<K, V> implements Repository<K, V> {
    private final TreeMap<K, V> base;

    public InMemoryTreeMap() {
        this.base = new TreeMap<>();
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

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public SortedMap<K, V> headMap(K key) {
        return base.headMap(key);
    }

    @Override
    public SortedMap<K, V> tailMap(K key, boolean param) {
        return base.tailMap(key, param);
    }
}
