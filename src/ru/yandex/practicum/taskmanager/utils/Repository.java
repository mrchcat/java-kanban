package ru.yandex.practicum.taskmanager.utils;

import java.util.Collection;

public interface Repository<K,V> {
    public void put(K key, V value);
    public V get(K key);
    public V delete(K key);
    public void clear();
    public Collection<V> values();
}
