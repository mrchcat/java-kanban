package ru.yandex.practicum.taskmanager.repository;

import java.util.Collection;

public interface Repository<K,V> {
    void put(K key, V value);
    V get(K key);
    V delete(K key);
    void clear();
    Collection<V> values();
}
