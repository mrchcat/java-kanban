package ru.yandex.practicum.taskmanager.repository;

import java.util.Collection;
import java.util.SortedMap;

public interface Repository<K, V> {
    void put(K key, V value);

    V get(K key);

    V delete(K key);

    void clear();

    Collection<V> values();

    boolean isEmpty();

    SortedMap<K, V> headMap(K key);

    SortedMap<K, V> tailMap(K key, boolean param);
}
