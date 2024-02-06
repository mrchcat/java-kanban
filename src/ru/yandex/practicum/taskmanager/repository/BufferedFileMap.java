package ru.yandex.practicum.taskmanager.repository;

import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class BufferedFileMap<K, V> implements Repository<K, V> {
    Path path;
    Bu

    public BufferedFileMap(String fileName) {

        this.path = path;
        Path path = null;
        ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(path));
        open();
    }

    private void open() {


    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public V delete(K key) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public void close() {
        Repository.super.close();
    }
}
